package com.zhmyr.reader

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.zhmyr.parser.FictionBook
import kotlinx.android.synthetic.main.activity_reader.*
import java.io.File

class ReaderActivity: AppCompatActivity() {
  private var doubleBackToExitPressedOnce = false
  lateinit var pref: SharedPreferences

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_reader)

    val toolbar = findViewById<Toolbar>(R.id.menu)
    setSupportActionBar(toolbar)

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)

    pref = getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)

    reader.adapter = ViewPagerAdapter(
      FictionBook(File(application.cacheDir.path + "/work")))
    reader.currentItem = pref.getInt("curSection", 0)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      setResult(Activity.RESULT_OK)
      finish()
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    pref.edit().apply {
      putInt("curSection", reader.currentItem)
    }.apply()
    super.onDestroy()
  }

  override fun onBackPressed() {
    if (doubleBackToExitPressedOnce) {
      setResult(Activity.RESULT_CANCELED)
      finish()
    }

    this.doubleBackToExitPressedOnce = true
    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

    Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
  }
}
