package com.sangwon.example.bookapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sangwon.example.bookapp.databinding.ActivityUserInfoBinding

class UserInfoActivity : AppCompatActivity() {

    companion object {
        const val RESULT_CODE_SUCCESS = 1001
    }

    private lateinit var binding: ActivityUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
            // 사용자 정보 입력 완료 후 데이터를 전달
            val name = binding.nameEditText.text.toString()
            val phoneNumber = binding.phoneNumberEditText.text.toString()

            if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
                val intent = Intent()
                intent.putExtra("userName", name)
                intent.putExtra("phoneNumber", phoneNumber)
                setResult(RESULT_CODE_SUCCESS, intent)
                finish()
            } else {
                Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
