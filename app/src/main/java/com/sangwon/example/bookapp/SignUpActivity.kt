package com.sangwon.example.bookapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sangwon.example.bookapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var Name: String
    private lateinit var ID: String
    private lateinit var PassWord: String
    private lateinit var PhoneNumber: String

    private val db = FirebaseFirestore.getInstance() // Firestore 인스턴스 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.createAccountButton.setOnClickListener {
            createAccount(
                binding.signupID.text.toString(),
                binding.signupPassword.text.toString()
            )
            Name = binding.signupName.text.toString()
            ID = binding.signupID.text.toString()
            PassWord = binding.signupPassword.text.toString()
            PhoneNumber = binding.phoneNumber.text.toString()
        }
    }

    private fun createAccount(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "계정 생성 완료.", Toast.LENGTH_SHORT).show()

                        // 회원가입이 성공한 경우 사용자 정보를 데이터베이스에 저장
                        saveUserInfoToDatabase(Name, ID, PassWord, PhoneNumber)

                        finish() // 가입창 종료
                    } else {
                        // 계정 생성 실패 원인을 확인하여 메시지 표시
                        val errorMessage = task.exception?.message ?: "계정 생성 실패"
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserInfoToDatabase(Name: String, UserId: String, PassWord: String, PhoneNumber: String) {
        // 사용자 정보를 User 객체에 저장
        val user = User(Name, UserId, PassWord, PhoneNumber)

        // Firestore 데이터베이스에 사용자 정보 저장
        db.collection("users")
            .document(UserId)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "회원 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()

                // 회원 정보가 Firestore에 저장된 후 마이페이지로 이동
                val intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "회원 정보 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error saving user information", e)
            }
    }

    companion object {
        private const val TAG = "SignUpActivity"
    }
}
