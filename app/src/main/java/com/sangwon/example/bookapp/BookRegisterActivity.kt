package com.sangwon.example.bookapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sangwon.example.bookapp.databinding.ActivityBookRegisterBinding

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
            val BookStatus = binding.BookStatus.selectedItem.toString()
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

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.status))
        binding.BookStatus.adapter = spinnerAdapter
        binding.BookStatus.setSelection(0)

        val rg = binding.category
        for (v: String in resources.getStringArray(R.array.category)){
            val rbtn = RadioButton(this)
            rbtn.text = v
            rg.addView(rbtn)
        }
    }
}