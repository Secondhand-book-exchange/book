package com.sangwon.example.bookapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sangwon.example.bookapp.databinding.ActivityMypageBinding

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val REQUEST_USER_INFO = 1001
        private const val TAG = "MypageActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        title = "                마이페이지"

        binding.editProfileButton.setOnClickListener {
            // 개인정보 수정 화면으로 이동할 때 사용자 정보 전달
            val currentUser = auth.currentUser
            currentUser?.let {
                val intent = Intent(this, UserInfoActivity::class.java)
                intent.putExtra("userName", binding.usernameTextView.text.toString())
                intent.putExtra("phoneNumber", binding.PhoneNumberTextView.text.toString())
                startActivityForResult(intent, REQUEST_USER_INFO)
            }
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }

        val currentUser = auth.currentUser
        currentUser?.let {
            // Firebase에서 사용자 정보를 읽어옴
            val userId = it.uid
            loadUserInfo(userId)
        }
    }

    private fun loadUserInfo(userId: String) {
        db.collection("users").document(Firebase.auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        // 사용자 정보를 UI에 설정
                        binding.usernameTextView.text = it.name
                        binding.userEmailTextView.text = it.userId
                        binding.PhoneNumberTextView.text = it.phoneNumber
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun logout() {
        auth.signOut() // Firebase 로그아웃

        // LoginActivity로 이동
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_USER_INFO && resultCode == UserInfoActivity.RESULT_CODE_SUCCESS) {
            // UserInfoActivity로부터 전달받은 정보를 사용하여 화면 업데이트
            val newName = data?.getStringExtra("userName")
            val newPhoneNumber = data?.getStringExtra("phoneNumber")
            if (!newName.isNullOrEmpty()) {
                binding.usernameTextView.text = newName
            }
            if (!newPhoneNumber.isNullOrEmpty()) {
                binding.PhoneNumberTextView.text = newPhoneNumber
            }
        }
    }
}
