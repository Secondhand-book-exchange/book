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
import com.sangwon.example.bookapp.Chat
import com.sangwon.example.bookapp.R

class ChatListAdapter : BaseAdapter() {
    private val items = ArrayList<Chat>()
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
            inflate.inflate(R.layout.chat_item, parent,false)
        }



        val iconImageView:ImageView = view.findViewById(R.id.profile)
        Glide.with(context!!)
            .load(items[position].profile)
            .into(iconImageView)


        view.findViewById<TextView>(R.id.name).text = items[position].name
        val check = view.findViewById<ImageView>(R.id.checked)
        if (items[position].check) {
            check.setBackgroundColor(Color.RED)
        } else {
            check.setBackgroundColor(Color.WHITE)
        }


        if (items[position].name == "") {
            view.findViewById<TextView>(R.id.name).text = "(알 수 없음)"
        }

             return view
    }

    fun addChat(item: Chat) {
        items.add(item)
    }

    fun clear() {
        items.clear()
    }

    fun getChatItem(position: Int): Chat {
        return items[position]
    }

    //정렬 드가자
    fun sortByTimestamp() {
        items.sortBy { it.timestamp }
        // sortByDescending
    }
}