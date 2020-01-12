package com.zhmyr.reader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhmyr.parser.FictionBook
import com.zhmyr.parser.Section
import kotlinx.android.synthetic.main.section_fragment.view.*
import kotlinx.android.synthetic.main.title_fragment.view.*

class ViewPagerAdapter(fictionBook: FictionBook):
  RecyclerView.Adapter<PagerVH>() {

  val section = normal(fictionBook.body.sections)
  val description = fictionBook.description

  private fun normal(array: List<Section>): List<Section> {
    val res = mutableListOf<Section>()
    array.forEach {
      res += it
      if (it.sections != null)
        res += normal(it.sections!!)
    }
    return res
  }

  override fun getItemViewType(position: Int): Int {
    return if (position > 0) 1 else 0
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
    if (viewType == 1)
      PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.section_fragment, parent, false))
    else
      PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.title_fragment, parent, false))

  override fun getItemCount(): Int = section.size

  override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {
    if (position > 0) {
      page.layoutManager = LinearLayoutManager(context)
      page.adapter = PageAdapter(section[position - 1])
    } else {
      title.layoutManager = LinearLayoutManager(context)
      title.adapter = TitleAdapter(description)
    }
  }
}

class PagerVH(itemView: View): RecyclerView.ViewHolder(itemView)