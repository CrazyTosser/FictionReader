package com.zhmyr.reader

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.zhmyr.parser.FictionBook
import kotlinx.android.synthetic.main.activity_book.*

class BookActivity : AppCompatActivity() {

    lateinit var model: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        model = ViewModelProviders.of(this).get(BookViewModel::class.java)
        try {
            model.book
                .setValue(
                    contentResolver.openInputStream(intent.extras?.get("uri") as Uri)?.let {
                        FictionBook(
                            it
                        )
                    }
                )
        } catch (ex: SecurityException) {
            setResult(Activity.RESULT_OK)
            finish()
        }


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentTemp, BookActivityFragment(supportActionBar))
                .commit()
        }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            setResult(Activity.RESULT_CANCELED, null)
            finish()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

}
