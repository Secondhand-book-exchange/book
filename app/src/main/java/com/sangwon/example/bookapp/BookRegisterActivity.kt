package com.sangwon.example.bookapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sangwon.example.bookapp.databinding.ActivityBookRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BookRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookRegisterBinding
    val db = Firebase.firestore
    private lateinit var imageUri: Uri
    val auth = Firebase.auth
    private lateinit var BookTitle: String
    private lateinit var Author: String
    private lateinit var Location: String
    private lateinit var BookStatus: String
    private lateinit var Subscript: String
    private var IsSale: Int = 1
    private lateinit var name: String
    private lateinit var Category: String
    private lateinit var Price : String


    val user = auth.currentUser
    var imagePath: String = " "


    var storage = Firebase.storage
    var storageRef = storage.reference

    val timestamp = Timestamp.now()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        binding.location.text = intent.getStringExtra("location")

        //이름 추가
        val docRef = db.collection("users").document(user!!.uid)
        // 게시물을 등록하기 전에 이름을 db에 등록하지 않으면 에러남
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // 문서가 존재하고 데이터를 가져온 경우
                    name = document.getString("name").toString()
                } else {
                    // 문서가 없거나 가져오지 못한 경우
                    Log.d("TAG", "No such document")
                }
            }

        binding.RegisterBook.setOnClickListener {
            BookTitle = binding.bookTitle.text.toString()
            Author = binding.Author.text.toString()
            Location = binding.location.text.toString()
            BookStatus = binding.bookStatus.selectedItem.toString()
            Subscript = binding.Subscript.text.toString()
            Price = binding.Price.text.toString()

            when {
                BookTitle.replace(" ", "") == "" ->
                    Toast.makeText(this, "책 제목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                Author.replace(" ", "") == "" ->
                    Toast.makeText(this, "작가 이름을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                Location == "거래 동네" ->
                    Toast.makeText(this, "거래 동네를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                else ->
                    uploadToFirestore(imageUri) //아무 이미지도 안넣으면 어떻게 되냐? 비동기라서 밑에 imagePath에 값 들어가기 전에 등록되는거 아니야? 아니 그냥 튕기던데
            }
        }

        binding.bookImage.setOnClickListener {
            // 갤러리 접근 암시적 인텐트? 그거인듯
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 1)
        }
        binding.location.setOnClickListener {
            val galleryIntent = Intent(this, SelectAreaActivity::class.java)
            startActivityForResult(galleryIntent, 2)
        }
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.status)
        )
        binding.bookStatus.adapter = spinnerAdapter
        binding.bookStatus.setSelection(0)

        val rg = binding.category
        for (v: String in resources.getStringArray(R.array.category)) {
            val rbtn = RadioButton(this)
            rbtn.text = v
            rg.addView(rbtn)
        }
        rg.setOnCheckedChangeListener { _, id ->
            val radioButton = findViewById<RadioButton>(id)
            val index = rg.indexOfChild(radioButton)
            if (index >= 0 && index < resources.getStringArray(R.array.category).size) {
                Category = resources.getStringArray(R.array.category)[index]
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the request code is the same as the one we sent
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            // 갤러리에서 사진에 대한 uri줌
            imageUri = data.data!!


            // Uri 이미지 뷰에 넣으면 사진 나와
            binding.bookImage.setBackgroundResource(0)
            binding.bookImage.setImageURI(imageUri)

        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Location = data.getStringExtra("location").toString()
            binding.location.text = Location
        }
    }

    // Upload the image to Firestore
    private fun uploadToFirestore(imageUri: Uri) {
        val imageName = System.currentTimeMillis().toString() + "." + getFileExtension(imageUri)
        val fileref = storageRef.child("images/$imageName")

        // 업로드를 코루틴으로 처리
        lifecycleScope.launch {
            try {
                // 파일 업로드 비동기 작업을 완료할 때까지 대기
                val uploadTask = withContext(Dispatchers.IO) {
                    fileref.putFile(imageUri).await()
                }

                // 업로드가 성공적으로 완료되면 이미지 경로 설정
                imagePath = "images/$imageName"

                // 이제 여기서 Post 객체를 생성하고 데이터베이스에 추가할 수 있습니다.
                val Post = Posts(
                    BookTitle,
                    Author,
                    BookStatus,
                    Subscript,
                    imagePath,
                    IsSale,
                    user!!.uid,
                    Location,
                    timestamp,
                    Category,
                    name,
                    Price
                )

                // 데이터베이스에 추가하는 코드는 여기에 작성하면 됩니다.
                db.collection("Posts")
                    .add(Post)
                    .addOnSuccessListener { documentReference ->
                        Log.d("db", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("db", "Error adding document", e)
                    }

                Toast.makeText(this@BookRegisterActivity, "업로드 성공", Toast.LENGTH_SHORT).show()
                // 작업 완료
                finish()
            } catch (e: Exception) {
                // 업로드 실패 처리
                Toast.makeText(this@BookRegisterActivity, "업로드 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFileExtension(imageUri: Uri): String {

        // Get the content resolver
        val contentResolver = contentResolver

        // Get the mime type
        val mimeType = contentResolver.getType(imageUri)

        // Get the file extension
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

        return extension.toString()
    }
}