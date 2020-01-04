package com.zhmyr.reader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Base64
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

/**
 * A placeholder fragment containing a simple view.
 */
class BookActivityFragment(private val toolbar: ActionBar?) : Fragment() {

    var viewModel = BookViewModel()
    private var text = listOf<TextView>()
    private lateinit var img: ImageView
    private lateinit var scr: Pair<Int, Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_book, container, false)
        text = listOf(root.findViewById(R.id.bookTitle), root.findViewById(R.id.bookAuthors), root.findViewById(R.id.bookAnn))
        img = root.findViewById(R.id.bookImg)
        val disp = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(disp)
        root.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                if (img.visibility == View.INVISIBLE)
                text.map { it.visibility = View.INVISIBLE }
                img.visibility = View.VISIBLE
            }

            override fun onSwipeRight() {
                if (img.visibility == View.VISIBLE) return
                text.map { it.visibility = View.VISIBLE }
                img.visibility = View.INVISIBLE
            }
        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        img.visibility = View.INVISIBLE
        viewModel = ViewModelProviders.of(this.activity!!).get(BookViewModel::class.java)
        viewModel.book.observe(viewLifecycleOwner, Observer {
            text[0].text = it.description.srcTitleInfo?.bookTitle
            text[1].text = it.description.titleInfo?.authors?.joinToString { it.fullName }
            toolbar?.title = it.description.srcTitleInfo?.bookTitle
            text[2].text = it.description.titleInfo?.annotation?.elements
                ?.joinToString(separator = "\n", transform = {it.text.toString()})
            val rawImg = Base64.decode(it.binaries[it.description.titleInfo?.coverPage?.get(0)?.value?.substring(1)]?.binary?.toByteArray(), Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(rawImg, 0, rawImg.size)
            img.setImageBitmap(bitmap)
        } )
    }
}
