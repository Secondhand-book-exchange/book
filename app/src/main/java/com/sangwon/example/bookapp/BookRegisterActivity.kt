package com.sangwon.example.bookapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sangwon.example.bookapp.databinding.ActivityBookRegisterBinding
import com.sangwon.example.bookapp.databinding.ActivityMainBinding

class BookRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookRegisterBinding
    val db = Firebase.firestore
    private lateinit var imageUri: Uri

    var storage = Firebase.storage
    var storageReference = storage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.RegisterBook.setOnClickListener {
            val BookTitle = binding.BookTitle.text.toString()
            val Author = binding.Author.text.toString()
            val Location = binding.Location.text.toString()
            val BookStatus = binding.BookStatus.toString()
            val Subscript = binding.Subscript.text.toString()
            val IsSale = false

            val Post = Posts(
                BookTitle,
                Author,
                Location,
                BookStatus,
                Subscript,
                null,
                IsSale,
                null,
                null,
            )

            db.collection("Posts")
                .add(Post)
                .addOnSuccessListener { documentReference ->
                    Log.d("db", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("db", "Error adding document", e)
                }
            finish()
        }

        binding.BookImage.setOnClickListener {
            // 갤러리 접근 암시적 인텐트? 그거인듯
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 1)
        }








    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the request code is the same as the one we sent
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            // 갤러리에서 사진에 대한 uri줌
            imageUri = data.data!!
            //Log.e("img","${imageUri}")
            //content://com.android.providers.media.documents/document/image%3A13


            // Uri 이미지 뷰에 넣으면 사진 나와
            binding.BookImage.setImageURI(imageUri)

        }
    }
}