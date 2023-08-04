package com.sangwon.example.bookapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sangwon.example.bookapp.databinding.ActivityUserInfoBinding

class UserInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserInfoBinding

    private var imageUri: Uri? = null // 선택된 이미지 URI

    companion object {
        const val RESULT_CODE_SUCCESS = 200 // 고유한 정수 값
    }

    // 갤러리 열기를 위한 ActivityResultLauncher
    private val galleryLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let {
                // 이미지 URI를 가져옴
                imageUri = it
                binding.profileImage.setImageURI(imageUri)
                Log.d("ImageUri", "$imageUri")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "              개인정보수정"

        // 이전에 저장된 사용자 정보 불러와서 화면에 표시
        val userName = intent.getStringExtra("userName")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val profileImage = intent.getStringExtra("profileImage")
        binding.nameEditText.setText(userName)
        binding.phoneNumberEditText.setText(phoneNumber)
        if (profileImage.isNullOrBlank())
            binding.profileImage.setBackgroundResource(R.drawable.profile)
        else
            Glide.with(this)
                .load(profileImage)
                .into(binding.profileImage)

        // 프로필 사진을 클릭하면 갤러리에서 사진 선택
        binding.profileImage.setOnClickListener {
            openGallery()
        }

        // 저장 버튼 클릭 시 수정된 정보와 프로필 사진을 MyPageActivity로 돌려주기
        binding.saveButton.setOnClickListener {
            val newName = binding.nameEditText.text.toString()
            val newPhoneNumber = binding.phoneNumberEditText.text.toString()

            if (newName.isNotEmpty() && newPhoneNumber.isNotEmpty()) {
                // 파이어베이스에 사용자 정보와 프로필 사진을 업데이트
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
                // 프로필 사진을 업로드하고 URL을 가져옴
                imageUri?.let { uri ->
                    uploadProfileImage(userId, uri, newName, newPhoneNumber)
                } ?: run {
                    // 프로필 사진을 선택하지 않은 경우 바로 돌려주기
                    returnUserInfo(newName, newPhoneNumber)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "사용자 정보 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadProfileImage(
        userId: String,
        uri: Uri,
        newName: String,
        newPhoneNumber: String
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val profileImageRef = storageRef.child("profile_images").child(userId)

        profileImageRef.putFile(uri)
            .addOnSuccessListener {
                // 프로필 사진 업로드 성공 후, URL을 가져와서 사용자 정보를 업데이트
                profileImageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()
                        val userUpdatesWithImage = hashMapOf<String, Any>(
                            "name" to newName,
                            "phoneNumber" to newPhoneNumber,
                        )

                        // 사용자 정보 업데이트
                        FirebaseFirestore.getInstance().collection("users").document(userId)
                            .update(userUpdatesWithImage)
                            .addOnSuccessListener {
                                // 수정된 정보와 프로필 사진 URL을 MyPageActivity로 돌려주기
                                returnUserInfo(newName, newPhoneNumber, imageUrl)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "사용자 정보 업데이트에 실패했습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "프로필 사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "프로필 사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun returnUserInfo(newName: String, newPhoneNumber: String, imageUrl: String? = null) {
        // 수정된 정보와 프로필 사진 URL을 MyPageActivity로 돌려주기
        val resultIntent = Intent()
        resultIntent.putExtra("userName", newName)
        resultIntent.putExtra("phoneNumber", newPhoneNumber)
        imageUrl?.let {
            resultIntent.putExtra("profileImageUrl", it)
        }
        setResult(RESULT_CODE_SUCCESS, resultIntent)
        finish()
    }

    private fun openGallery() {
        // 갤러리에서 이미지를 선택하는 Intent 생성
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        galleryLauncher.launch(galleryIntent)
    }
}
