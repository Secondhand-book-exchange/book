package com.sangwon.example.bookapp.Adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sangwon.example.bookapp.BookItem
import com.sangwon.example.bookapp.R
import java.text.SimpleDateFormat

class BookListAdapter : BaseAdapter() {
    private val items = ArrayList<BookItem>()
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val context = parent?.context
        val view = if (convertView != null) convertView
        else {
            val inflate: LayoutInflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflate.inflate(R.layout.book_item, parent,false)
        }


        val iconImageView:ImageView = view.findViewById(R.id.bookCover)
        Glide.with(context!!)
            .load(items[position].Img)
            .into(iconImageView)


        view.findViewById<TextView>(R.id.title).text = items[position].BookTitle
        Log.d("locate", items[position].Locate)
        view.findViewById<TextView>(R.id.locate).text = items[position].Locate.split(" ")[2]
        view.findViewById<TextView>(R.id.author).text = items[position].Author
        view.findViewById<TextView>(R.id.date).text = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분").format(items[position].Date)
        val type = view.findViewById<TextView>(R.id.type)
        if (items[position].type()) {
            type.text = "거래 완료"
            type.setTextColor(Color.RED)
            view.setBackgroundColor(Color.parseColor("#DDDDDD"))
            iconImageView.alpha=0.6f
        } else {
            type.text = "판매중"
            type.setTextColor(Color.parseColor("#157000"))
        }

        return view
    }

    fun addBook(item: BookItem) {
        for (i:Int in 0 until items.size){
            if (item.Date > items[i].Date) {
                items.add(i, item)
                return
            }
        }
        items.add(item)
    }

    fun clear() {
        items.clear()
    }
}