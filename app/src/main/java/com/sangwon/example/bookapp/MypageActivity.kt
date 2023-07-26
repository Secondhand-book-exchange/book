package com.sangwon.example.bookapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sangwon.example.bookapp.databinding.ActivityMypageBinding

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        title = "마이페이지"
        /*
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.back) // 뒤로가기 버튼 아이콘 설정
            setTitle("") // 타이틀 비움
        }*/

        binding.logoutButton.setOnClickListener {
            logout()
        }

        // TODO: 프로필 이미지, 이름 설정
        // binding.profileImage.setImageResource(R.drawable.profile_image_placeholder)
        // binding.usernameTextView.text = "사용자 이름"

        // 버튼 클릭 이벤트 등록
        // ...

    }

    private fun logout() {
        auth.signOut() // Firebase 로그아웃
        // 로그아웃 처리를 여기에 구현 (FirebaseAuth에서 로그아웃 등)
        // ...

        // LoginActivity로 이동
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed() // 뒤로가기 버튼 클릭 시 동작
            // 다른 메뉴 항목이 있다면 여기에 추가
        }
        return super.onOptionsItemSelected(item)
    }
}
