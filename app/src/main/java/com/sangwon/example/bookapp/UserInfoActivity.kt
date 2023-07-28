package com.sangwon.example.bookapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sangwon.example.bookapp.databinding.ActivityUserInfoBinding

class UserInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserInfoBinding

    companion object {
        const val RESULT_CODE_SUCCESS = 200 // 선호하는 고유한 정수 값
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전에 저장된 사용자 정보 불러와서 화면에 표시
        val userName = intent.getStringExtra("userName")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        binding.nameEditText.setText(userName)
        binding.phoneNumberEditText.setText(phoneNumber)

        // 저장 버튼 클릭 시 수정된 정보를 MyPageActivity로 돌려주기
        binding.saveButton.setOnClickListener {
            val newName = binding.nameEditText.text.toString()
            val newPhoneNumber = binding.phoneNumberEditText.text.toString()

            if (newName.isNotEmpty() && newPhoneNumber.isNotEmpty()) {
                // 파이어베이스에 사용자 정보를 업데이트
                val currentUser = FirebaseAuth.getInstance().currentUser
                currentUser?.let {
                    val userId = it.uid
                    updateUserInfo(userId, newName, newPhoneNumber)
                }
            } else {
                Toast.makeText(this, "이름과 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserInfo(userId: String, newName: String, newPhoneNumber: String) {
        val db = FirebaseFirestore.getInstance()

        // 사용자 정보를 업데이트할 Map 생성
        val userUpdates = hashMapOf<String, Any>(
            "name" to newName,
            "phoneNumber" to newPhoneNumber
        )

        // 사용자 정보 업데이트
        db.collection("users").document(userId)
            .update(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "사용자 정보가 업데이트되었습니다.", Toast.LENGTH_SHORT).show()

                // 수정된 정보를 MyPageActivity로 돌려주기
                val resultIntent = Intent()
                resultIntent.putExtra("userName", newName)
                resultIntent.putExtra("phoneNumber", newPhoneNumber)
                setResult(RESULT_CODE_SUCCESS, resultIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "사용자 정보 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }
}