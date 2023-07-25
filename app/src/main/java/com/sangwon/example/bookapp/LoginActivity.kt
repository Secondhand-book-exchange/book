package com.sangwon.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sangwon.example.bookapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding.login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.login.id->
                startActivity(Intent(this, MainActivity::class.java))
        }
    }
}