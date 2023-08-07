package com.sangwon.example.bookapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sangwon.example.bookapp.databinding.ActivitySignUpBinding
import java.io.ByteArrayOutputStream

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var Name: String
    private lateinit var PhoneNumber: String
    private lateinit var Location: String

    private val db = FirebaseFirestore.getInstance() // Firestore 인스턴스 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createAccountButton.setOnClickListener {
            if (binding.signupID.text.trim() == "" || binding.signupPassword.text.trim() == "" || binding.signupName.text.trim() == "" || binding.phoneNumber.text.trim() == "")
                Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
            else
                createAccount(
                    binding.signupID.text.toString().replace(" ", ""),
                    binding.signupPassword.text.toString().replace(" ", "")
                )
            Name = binding.signupName.text.toString()
            PhoneNumber = binding.phoneNumber.text.toString()
            Location = binding.location.text.toString()
        }

        binding.location.setOnClickListener {
            val galleryIntent = Intent(this, SelectAreaActivity::class.java)
            startActivityForResult(galleryIntent, 2)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Location = data.getStringExtra("location").toString()
            binding.location.text = Location
        }
    }

    private fun createAccount(email: String, password: String) {
        val auth = Firebase.auth

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "계정 생성 완료.", Toast.LENGTH_SHORT).show()

                        // 회원가입이 성공한 경우 사용자 정보를 데이터베이스에 저장
                        saveUserInfoToDatabase(Name, email, password, PhoneNumber, Location)

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

    private fun saveUserInfoToDatabase(
        Name: String, UserId: String, PassWord: String, PhoneNumber: String, Location: String
    ) {
        // 사용자 정보를 User 객체에 저장
        val user = User(Name, UserId, PassWord, PhoneNumber, Location)

        // Firestore 데이터베이스에 사용자 정보 저장
        db.collection("users").document(Firebase.auth.currentUser!!.uid).set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "회원 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                val drawable = getDrawable(R.drawable.profile)
                val bitmapDrawable = drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                val storageRef = FirebaseStorage.getInstance().reference
                var uploadTask =
                    storageRef.child("profile_images/${Firebase.auth.currentUser!!.uid}")
                        .putBytes(data)
                uploadTask
                    .addOnSuccessListener {
                        // 회원 정보가 Firestore에 저장된 후 마이페이지로 이동
                        val intent = Intent(this, MyPageActivity::class.java)
//                      Handler().postDelayed({ startActivity(intent) }, 1000)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firebase Storage", "새 이미지 업로드 실패: $exception")
                    }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "회원 정보 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error saving user information", e)
            }
    }

    companion object {
        private const val TAG = "SignUpActivity"
    }
}
