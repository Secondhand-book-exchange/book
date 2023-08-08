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
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sangwon.example.bookapp.BookItem
import com.sangwon.example.bookapp.R
import org.w3c.dom.Text
import java.text.SimpleDateFormat

class AreaAdapter : BaseAdapter() {
    private var items = ArrayList<String>()
    private  var p = -1
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
            inflate.inflate(R.layout.area_item, parent,false)
        }

        if (position == p)
            view.setBackgroundColor(Color.parseColor("#D0F5BE"))
        else
            view.setBackgroundColor(Color.parseColor("#FFFFFF"))

        view.findViewById<TextView>(R.id.area).text = items[position]

        return view
    }

    fun setArea(areas: ArrayList<String>) {
        items = areas
    }

    fun selectedView(p:Int){
        this.p = p
    }
}