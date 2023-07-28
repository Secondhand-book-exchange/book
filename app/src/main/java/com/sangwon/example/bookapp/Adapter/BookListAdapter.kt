package com.sangwon.example.bookapp.Adapter

import android.content.Context
import android.graphics.Color
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sangwon.example.bookapp.Item.BookItem
import com.sangwon.example.bookapp.R

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


        val iconImageView:ImageView = view.findViewById<ImageView>(R.id.bookImage)
        Glide.with(context!!)
            .load(items[position].Img)
            .into(iconImageView)


        view.findViewById<TextView>(R.id.title).text = items[position].BookTitle
        view.findViewById<TextView>(R.id.note).text = items[position].Subscript
        val type = view.findViewById<TextView>(R.id.type)
        if (items[position].type()) {
            type.text = "판매중"
            type.setTextColor(Color.GREEN)
        } else {
            type.text = "판매 완료"
            type.setTextColor(Color.RED)
        }

        return view
    }

    fun addBook(item: BookItem) {
        items.add(item)
    }
}