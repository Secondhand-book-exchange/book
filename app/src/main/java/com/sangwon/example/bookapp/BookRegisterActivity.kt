package com.sangwon.example.bookapp

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
        }








    }
}