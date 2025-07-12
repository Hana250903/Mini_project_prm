package com.example.mini_project_prm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mini_project_prm.api.AppInfo
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
    private val TAG = "LoginActivity"

    private val googleSignInLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d(TAG, "Google sign-in result received")

            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("LoginActivity", "Token: ${account.idToken}")
                    handleLoginWithSupabase(account)
                } catch (e: ApiException) {
                    Log.e(TAG, "Google Sign-In failed with statusCode: ${e.statusCode}", e)
                    showError("Google Sign-In thất bại. Mã lỗi: ${e.statusCode} (${e.message})")
                }
            } else {
                // Phân tích thêm trong trường hợp result không OK
                val data = result.data
                if (data == null) {
                    Log.w(TAG, "Google sign-in cancelled or failed - no data returned")
                    showError("Đăng nhập thất bại: Không có dữ liệu trả về từ Google.")
                } else {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        task.getResult(ApiException::class.java)
                    } catch (e: ApiException) {
                        Log.e(TAG, "Google Sign-In failed during fallback check. Code: ${e.statusCode}", e)
                        showError("Lỗi Google Sign-In: ${e.statusCode} - ${e.message}")
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "LoginActivity started")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(AppInfo.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogleSignIn.setOnClickListener {
            Log.d(TAG, "Google Sign-In button clicked")
            signInWithGoogle()
        }

        if (intent.getBooleanExtra("logout", false)) {
            signOut()
        }
    }

    private fun signInWithGoogle() {
        Log.d(TAG, "Launching Google sign-in intent")
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d(TAG, "Signed out successfully")
            binding.btnGoogleSignIn.visibility = View.VISIBLE
            Toast.makeText(this, "Đã đăng xuất.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLoginWithSupabase(account: GoogleSignInAccount) {
        val email = account.email ?: return showError("Email không hợp lệ")
        val fullName = account.displayName ?: "No Name"

        Log.d(TAG, "Handling Supabase login for: $email")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users = RetrofitClient.instance.getUsers()
                Log.d(TAG, "Fetched ${users.size} users from Supabase")

                val user = users.find { it.email == email }

                if (user == null) {
                    Log.d(TAG, "User not found in Supabase, creating new user")

                    val newUser = User(
                        fullName = fullName,
                        email = email,
                        passwordHash = null,
                        phone = null,
                        address = null,
                        role = "customer",
                        createdAt = LocalDateTime.now().toString()
                    )

                    val response = RetrofitClient.instance.createUser(newUser)

                    if (!response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            val errorBody = response.errorBody()?.string()
                            Log.e(TAG, "Chi tiết lỗi từ Supabase: $errorBody")
                            showError("Tạo người dùng thất bại: $errorBody")

                        }
                        return@launch
                    }

                    Log.d(TAG, "User created successfully")
                } else {
                    Log.d(TAG, "User already exists in Supabase")
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Chào mừng, $email", Toast.LENGTH_SHORT).show()
                    navigateToMain(email)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Supabase login error", e)
                    showError("Lỗi kết nối đến Supabase: ${e.message}")
                }
            }
        }
    }

    private fun navigateToMain(email: String) {
        Log.d(TAG, "Navigating to MainActivity with email: $email")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    private fun showError(msg: String) {
        Log.e(TAG, "Error: $msg")
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
