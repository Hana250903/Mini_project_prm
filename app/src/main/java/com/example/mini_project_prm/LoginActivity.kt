package com.example.mini_project_prm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mini_project_prm.api.AppInfo.GOOGLE_CLIENT_ID
import com.example.mini_project_prm.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private var GOOGLE_WEB_CLIENT_ID = GOOGLE_CLIENT_ID

    private val googleSignInLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    val email = account.email ?: ""
                    Toast.makeText(this, "Đăng nhập thành công với ${account.email}", Toast.LENGTH_SHORT).show()
                    updateUI(account)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Đăng nhập Google thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            } else {
                Toast.makeText(this, "Đăng nhập Google bị hủy hoặc không thành công.", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

    fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                updateUI(null)
                Toast.makeText(this, "Đã đăng xuất Google.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Đăng xuất Google thất bại.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            binding.btnGoogleSignIn.visibility = View.GONE

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", account.email)
            startActivity(intent)
            finish()
        } else {
            binding.btnGoogleSignIn.visibility = View.VISIBLE
        }
    }
}