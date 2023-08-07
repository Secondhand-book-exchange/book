package com.sangwon.example.bookapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sangwon.example.bookapp.databinding.ActivityLoginBinding
import java.io.ByteArrayOutputStream

class LoginActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private lateinit var binding: ActivityLoginBinding // 바인딩 객체 선언
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var name: String
    private var LOCATION_PERMISSION_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater) // 레이아웃 바인딩
        setContentView(binding.root) // 레이아웃 설정
        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    getGoogleInfo(task)
                    Toast.makeText(this, "구글 로그인 중", Toast.LENGTH_SHORT).show()
                }
            }


        // 회원가입 창으로
        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // 로그인 버튼
        binding.loginButton.setOnClickListener {
            signIn(binding.idEditText.text.toString(), binding.passwordEditText.text.toString())
        }

        binding.loginForGoogle.setOnClickListener {
            googleLogin()
        }

//        requestPermission()
    }

    // 로그아웃하지 않을 시 자동 로그인 , 회원가입시 바로 로그인 됨
    public override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    // 로그인
    private fun signIn(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "로그인에 성공 하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveMainPage(auth?.currentUser)
                    } else {
                        if (password == "GoogleLoginPassword") {
                            createAccount(email, password)
                        } else
                            Toast.makeText(
                                baseContext, "로그인에 실패 하였습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
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
                        saveUserInfoToDatabase(name, email, password, "01000000000")

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
        Name: String,
        UserId: String,
        PassWord: String,
        PhoneNumber: String
    ) {
        // 사용자 정보를 User 객체에 저장
        val user = User(Name, UserId, PassWord, PhoneNumber, "지역 선택")
        val db = FirebaseFirestore.getInstance()

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

    // 유저정보 넘겨주고 메인 액티비티 호출
    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            account?.let {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun googleLogin() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun getGoogleInfo(completedTask: Task<GoogleSignInAccount>) {
        val TAG = "google111"
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, account.id!!)
            Log.d(TAG, account.familyName!!)
            Log.d(TAG, account.givenName!!)
            Log.d(TAG, account.email!!)
            signIn(account.email!!, "GoogleLoginPassword")
            name = account.familyName + account.givenName
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun requestPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val permissionGranted = PackageManager.PERMISSION_GRANTED
        if (ContextCompat.checkSelfPermission(this, permission) != permissionGranted) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // 위치 권한이 이미 허용된 경우, 현재 위치로 지도 이동
//            moveMapToCurrentLocation()
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 3)
        }
    }


    companion object {
        private const val TAG = "LoginActivity"
    }
}

