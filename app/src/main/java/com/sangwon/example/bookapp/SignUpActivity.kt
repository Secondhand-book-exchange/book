package com.sangwon.example.bookapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.sangwon.example.bookapp.databinding.ActivitySignUpBinding
import java.io.ByteArrayOutputStream

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var Name: String
    private lateinit var PhoneNumber: String
    private lateinit var Location: String
    private lateinit var certificateId: String
    private var certify = false

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    var mGoogleSignInClient: GoogleSignInClient? = null

    private val db = FirebaseFirestore.getInstance() // Firestore 인스턴스 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    certificateSuccess(task.result.id.toString())
                }
            }

        binding.createAccountButton.setOnClickListener {
            if (binding.signupID.text.trim() == "" || binding.signupPassword.text.trim() == "" || binding.signupName.text.trim() == "" || binding.phoneNumber.text.trim() == "")
                Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
            else if (!certify)
                Toast.makeText(this, "인증을 완료해주세요", Toast.LENGTH_SHORT).show()
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
            startActivityForResult(Intent(this, SelectAreaActivity::class.java), 2)
        }

        binding.certifyKakao.setOnClickListener {
            kakaoCertificate()
        }

        binding.certifyGoogle.setOnClickListener {
            googleCertificate()
        }
    }

    private fun kakaoCertificate() {
        // 로그인 조합 예제

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->

                    if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    UserApiClient.instance.me { user, error ->
                        if (user != null) certificateSuccess(user.id.toString())
                        else if (error!=null) Log.e(TAG, "사용자 정보 요청 실패 $error")
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun googleCertificate() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun certificateSuccess(id: String) {
        certificateId = id
        binding.certifyKakao.isEnabled = false
        binding.certifyGoogle.isEnabled = false
        certify = true
        db.collection("users").get()
            .addOnSuccessListener {
                for (user in it)
                    if (user.getString("certificateId") == id) {
                        Toast.makeText(this, "이미 등록되어있는 계정 입니다.", Toast.LENGTH_SHORT).show()
                        binding.certifyKakao.isEnabled = true
                        binding.certifyGoogle.isEnabled = true
                        certify = false
                        break
                        Log.e("USER", "중복")
                    }
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
        val user = User(Name, UserId, PassWord, PhoneNumber, Location, certificateId)

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
                val uploadTask =
                    storageRef.child("profile_images/${Firebase.auth.currentUser!!.uid}")
                        .putBytes(data)
                uploadTask
                    .addOnSuccessListener {
                        // 회원 정보가 Firestore에 저장된 후 마이페이지로 이동
                        val intent = Intent(this, MyPageActivity::class.java)
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

    private fun getGoogleInfo(completedTask: Task<GoogleSignInAccount>) {
        val TAG = "google111"
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, account.id!!)
            Log.d(TAG, account.familyName!!)
            Log.d(TAG, account.givenName!!)
            Log.d(TAG, account.email!!)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    companion object {
        private const val TAG = "SignUpActivity"
    }
}
