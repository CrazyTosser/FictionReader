package com.zhmyr.reader

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zhmyr.parser.Description


abstract class PageViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
  abstract fun bind(entry: T)
}

class TitleHolder(view: View) : PageViewHolder<Description>(view) {
  override fun bind(entry: Description) {

  }

}