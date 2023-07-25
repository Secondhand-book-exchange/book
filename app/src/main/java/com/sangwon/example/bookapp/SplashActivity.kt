package com.sangwon.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        KakaoSdk.init(this, "0272df0de0ac0b5316dc14c4e4e15362")

        //auth = Firebase.auth

        // 회원가입이 안되어있으므로, joinActivity
        Handler().postDelayed({
            /*if (auth.currentUser?.uid == null) {
                startActivity(Intent(this, SignUpActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }*/
/*            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _, error ->
                    if (error == null) {
                        //nextMainActivity()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
            } else {
                startActivity(Intent(this, SignUpActivity::class.java))
            }*/
            startActivity(Intent(this, LoginActivity::class.java))

            Handler().postDelayed({finish()}, 1000)
        }, 2000)
    }
}