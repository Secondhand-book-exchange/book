package com.sangwon.example.bookapp.Adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sangwon.example.bookapp.Item.BookTheme
import com.sangwon.example.bookapp.R

class ThemeAdapter : RecyclerView.Adapter<ThemeAdapter.ThemeView>() {
    val list = ArrayList<BookTheme>()
    val icons = arrayListOf<Int>(
        R.drawable.baseline_account_circle_24,
        R.drawable.baseline_home_24,
        R.drawable.baseline_map_24,
        R.drawable.baseline_search_24
    )
    val themes = arrayListOf<String>("동화", "잡지", "소설", "시", "인문학")

    inner class ThemeView(view: View) : ViewHolder(view)

    class HorizontalItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position

            if (position != 0)
                outRect.left = space

            outRect.right = space
            outRect.bottom = space
            outRect.top = space
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeView {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.theme_button, parent, false)

        return ThemeView(view)
    }

    override fun onBindViewHolder(view: ThemeView, position: Int) {
        view.itemView.findViewById<ImageView>(R.id.icon).setImageResource(list[position].icon)
        view.itemView.findViewById<TextView>(R.id.theme).text = list[position].theme
    }

    fun add(item:BookTheme){
        list.add(item)
    }
}