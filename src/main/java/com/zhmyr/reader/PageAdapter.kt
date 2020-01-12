package com.zhmyr.reader

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zhmyr.parser.Description
import com.zhmyr.parser.Element
import com.zhmyr.parser.Section

class PageAdapter(val section: Section) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  val y = 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return TagViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tag, parent, false))
  }

  override fun getItemCount(): Int {
    return if (section.title != null) section.elements!!.size + 1 else section.elements!!.size
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (position == 0 && section.title != null)
      (holder as TagViewHolder).bind(Element(section.title.toString()), "center", true)
    else
      (holder as TagViewHolder).bind(section.elements!![if (section.title != null) position - 1 else position])
  }
}

class TitleAdapter(val title: Description) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

  override fun getItemViewType(position: Int): Int =
    if (position >= (itemCount - title.titleInfo?.coverPage!!.size)) 1 else 0


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    return if (viewType == 1)
      ImgViewHolder(layoutInflater.inflate(R.layout.img, parent, false))
    else
      TagViewHolder(layoutInflater.inflate(R.layout.tag, parent, false))
  }

  override fun getItemCount(): Int {
    var i = 1
    if (title.titleInfo?.authors != null) i++
    if (title.titleInfo?.annotation != null) i++
    i += title.titleInfo?.coverPage!!.size
    return i
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is TagViewHolder) {
      when (position) {
        0 -> holder.bind(Element(title.titleInfo?.bookTitle), "center")
        1 -> holder.bind(Element("<h2>${title.titleInfo?.authors?.joinToString(", ") {it.fullName}}</h2>"), "center")
        2 -> holder.bind(Element(title.titleInfo?.annotation.toString()))
      }
    } else {
      (holder as ImgViewHolder).bind(title.titleInfo?.coverPage!![position - 3].value!!)
    }
  }

}