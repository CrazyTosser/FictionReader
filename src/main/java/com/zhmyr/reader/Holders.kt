package com.zhmyr.reader

import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zhmyr.parser.Element
import java.util.*

class TagViewHolder(view: View) : RecyclerView.ViewHolder(view){
  private val wrapper: TextView = view.findViewById(R.id.wrapper)

  fun bind(tag: Element, align: String = "left", bold: Boolean = false) {
    wrapper.text = Html.fromHtml(tag.toString(),
      Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH or Html.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE)
    if (bold) wrapper.setTypeface(null, Typeface.BOLD)
    wrapper.gravity = when (align) {
      "center" -> Gravity.CENTER
      "right" -> Gravity.RIGHT
      else -> Gravity.LEFT
    }
  }
}

class ImgViewHolder(view: View): RecyclerView.ViewHolder(view) {

  private val img: ImageView = view.findViewById(R.id.img)

  fun bind(encoded: String) {
    val bytes =
      Base64.getDecoder().decode(encoded.substring(encoded.indexOf(",")  + 1))
    img.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))

  }
}