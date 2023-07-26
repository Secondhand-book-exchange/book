package com.sangwon.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sangwon.example.bookapp.databinding.ActivityBookRegisterBinding
import com.sangwon.example.bookapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener(this)
        binding.post.setOnClickListener {
            val intent = Intent(this, BookRegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.login.id->
                startActivity(Intent(this, MainActivity::class.java))
        }
    }
}