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

        // 계정 생성 버튼 (id를 createAccountButton으로 수정)
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
                        // 계정 생성 실패 원인을 확인하여 메시지 표시
                        val errorMessage = task.exception?.message ?: "계정 생성 실패"
                        Toast.makeText(
                            this, errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                this, "이메일과 비밀번호를 입력해주세요.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}