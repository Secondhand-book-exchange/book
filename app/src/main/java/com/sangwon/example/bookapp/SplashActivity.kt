package com.sangwon.example.bookapp

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        getKeyHash()

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
    private fun getKeyHash() {
        val packageInfo =
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
        for (signature in packageInfo.signingInfo.apkContentsSigners) {
            try {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d(
                    "getKeyHash",
                    "key hash: ${Base64.encodeToString(md.digest(), Base64.NO_WRAP)}"
                )
            } catch (e: NoSuchAlgorithmException) {
                Log.w("getKeyHash", "Unable to get MessageDigest. signature=$signature", e)
            }
        }
    }
}