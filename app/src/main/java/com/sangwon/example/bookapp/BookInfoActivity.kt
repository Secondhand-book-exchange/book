package com.sangwon.example.bookapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sangwon.example.bookapp.databinding.ActivityBookInfoBinding

class BookInfoActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding:ActivityBookInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        binding.BookTitle.text = intent.getStringExtra("BookTitle")
        binding.Author.text = intent.getStringExtra("Author")
        binding.ISBN.text = intent.getStringExtra("ISBN")
        binding.Subscript.text = intent.getStringExtra("Subscript")

        val uri = intent.getStringExtra("BookCover")
        if (uri != "defaulImage.jpeg")
            Glide.with(this)
                .load(Uri.parse(uri))
                .into(binding.imageViewBookCover)
//            binding.imageViewBookCover.setImageURI(Uri.parse(intent.getStringExtra("BookCover")))

        binding.chat.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.chat.id ->
                startActivity(Intent(this, ChatActivity::class.java))
        }
    }
}