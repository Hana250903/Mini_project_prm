package com.example.mini_project_prm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mini_project_prm.api.AppInfo.GOOGLE_CLIENT_ID
import com.example.mini_project_prm.api.RetrofitClient
import com.example.mini_project_prm.databinding.ActivityLoginBinding
import com.example.mini_project_prm.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private var GOOGLE_WEB_CLIENT_ID = GOOGLE_CLIENT_ID

    companion object {
        private const val TAG = "LoginActivity"
    }

    private val googleSignInLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "Token: ${account.idToken}")
                    handleLoginWithSupabase(account)
                } catch (e: ApiException) {
                    Toast.makeText(
                        this,
                        "Đăng nhập Google thất bại: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            } else {
                val data = result.data
                if (data == null) {
                    Log.w(TAG, "Google sign-in cancelled or failed - no data returned")
                    showError("Đăng nhập thất bại: Không có dữ liệu trả về từ Google.")
                } else {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        task.getResult(ApiException::class.java)
                    } catch (e: ApiException) {
                        Log.e(
                            TAG,
                            "Google Sign-In failed during fallback check. Code: ${e.statusCode}",
                            e
                        )
                        showError("Lỗi Google Sign-In: ${e.statusCode} - ${e.message}")
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(GOOGLE_WEB_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        if (intent.getBooleanExtra("logout", false)) {
            signOut()
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(this, "Đã đăng xuất.", Toast.LENGTH_SHORT).show()
            binding.btnGoogleSignIn.visibility = View.VISIBLE
        }
    }

    private fun handleLoginWithSupabase(account: GoogleSignInAccount) {
        val email = account.email ?: return showError("Email không hợp lệ")
        val fullName = account.displayName ?: "No Name"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users = RetrofitClient.instance.getUsers()
                val user = users.find { it.email == email }

                var userId: Int

                if (user == null) {
                    val newUser = User(
                        fullName = fullName,
                        email = email,
                        passwordHash = null,
                        phone = null,
                        address = null,
                        role = "customer",
                        createdAt = LocalDateTime.now().toString(),
                        city = null,
                        countries = null,
                        dob = null,
                        gender = null,
                        id = TODO()
                    )

                    val response = RetrofitClient.instance.createUser(newUser)

                    if (!response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            val errorBody = response.errorBody()?.string()
                            showError("Tạo người dùng thất bại: $errorBody")
                        }
                        return@launch
                    }

                    val updatedUsers = RetrofitClient.instance.getUsers()
                    val createdUser = updatedUsers.find { it.email == email }
                    userId = createdUser?.id ?: -1
                } else {
                    userId = user.id ?: -1
                }

                withContext(Dispatchers.Main) {
                    if (userId == -1) {
                        showError("Không thể lấy ID người dùng")
                    } else {
                        Toast.makeText(this@LoginActivity, "Chào mừng, $email", Toast.LENGTH_SHORT)
                            .show()
                        navigateToMain(userId, email, fullName)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Lỗi kết nối đến Supabase: ${e.message}")
                }
            }
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            binding.btnGoogleSignIn.visibility = View.GONE
        } else {
            binding.btnGoogleSignIn.visibility = View.VISIBLE
        }
    }

    private fun navigateToMain(userid: Int, email: String, fullName: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userid", userid)
        intent.putExtra("email", email)
        intent.putExtra("fullName", fullName)
        startActivity(intent)
        finish()
    }

    private fun showError(msg: String) {
        Log.e(TAG, "Error: $msg")
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
