package com.sangwon.example.bookapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sangwon.example.bookapp.databinding.ActivityLoginBinding
import com.sangwon.example.bookapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private lateinit var binding: ActivitySignUpBinding // 바인딩 객체 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater) // 레이아웃 바인딩
        setContentView(binding.root) // 레이아웃 설정
        auth = Firebase.auth

        // 계정 생성 버튼
        binding.createAccountButton.setOnClickListener {
            createAccount(binding.signupID.text.toString(), binding.signupPassword.text.toString())
        }
    }

    // 계정 생성
    private fun createAccount(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this, "계정 생성 완료.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish() // 가입창 종료
                    } else {
                        Toast.makeText(
                            this, "계정 생성 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}