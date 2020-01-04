package com.zhmyr.reader

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zhmyr.parser.FictionBook

class BookViewModel() : ViewModel() {
    enum class Elements {Title, Subtitle, Section }
    var book: MutableLiveData<FictionBook> = MutableLiveData()
    val cur: MutableLiveData<Elements> = MutableLiveData()
}