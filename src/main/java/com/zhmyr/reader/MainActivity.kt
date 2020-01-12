package com.zhmyr.reader

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.format.Formatter.formatShortFileSize
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipInputStream

const val filePick = 42
const val prefFile = "FBWork"

class MainActivity: AppCompatActivity() {

  lateinit var pref: SharedPreferences
  lateinit var tp: TextView
  var scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    tp = findViewById(R.id.progressT)

    pref = getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
      != PackageManager.PERMISSION_GRANTED
    ) {

      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
        44
      )
    }

    if (pref.getString(prefFile, "")?.isEmpty()!! ||
      !File(application.cacheDir.path + "/work").exists()
    ) {
      startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        setType("*/*")
      }, filePick)
    } else
      startActivityForResult(Intent(this, ReaderActivity::class.java), filePick + 1)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {

    when (requestCode) {
      44 -> if (grantResults.isEmpty() || !(PackageManager.PERMISSION_GRANTED in grantResults))
        ActivityCompat.requestPermissions(
          this,
          arrayOf(Manifest.permission_group.STORAGE),
          44
        )
    }
  }

  fun processFile(workFile: String) {
    scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    scope.launch {
      Log.v("Book", workFile)
      when (workFile.split(".").last().toLowerCase()) {
        "zip" -> {
          ZipInputStream(File(workFile).inputStream()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
              if (entry.name.split(".").last() != "fb2")
                zip.skip(entry.size)
              else {
                val progressBar = findViewById<ProgressBar>(R.id.progress)
                progressBar.max = entry.size.toInt()
                val sz = entry.size
                var cur: Long = 0
                launch(Dispatchers.Main) {
                  tp.text = "${formatShortFileSize(
                    applicationContext,
                    cur
                  )}/${formatShortFileSize(applicationContext, sz)}"
                }
                File(application.cacheDir.path + "/work").outputStream().use {
                  var byte = ByteArray(128) {0}
                  var res = zip.read(byte, 0, 128)
                  while (res != -1) {
                    it.write(byte, 0, res)
                    cur += 128
                    progressBar.incrementProgressBy((cur / sz).toInt())
                    launch(Dispatchers.Main) {
                      tp.text = "${formatShortFileSize(
                        applicationContext,
                        cur
                      )}/${formatShortFileSize(applicationContext, sz)}"
                    }
                    res = zip.read(byte, 0, 128)
                  }
                  it.flush()
                }
              }
              entry = zip.nextEntry
            }
          }
        }
        "fb2" ->
          File(workFile).copyTo(File(application.cacheDir.path + "/work"))
      }
      pref.edit().putString(prefFile, workFile).apply()
      launch(Dispatchers.Main) {
        startActivityForResult(
          Intent(
            applicationContext,
            ReaderActivity::class.java
          ), filePick + 1
        )
      }
    }
  }

  @SuppressLint("NewApi")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == filePick) {
      if (data != null && resultCode == Activity.RESULT_OK) {
        try {
          val workFile = Files.readSymbolicLink(
            Paths.get(
              "/proc/self/fd/" + contentResolver.openFileDescriptor(
                data.data!!,
                "r"
              )?.fd.toString()
            )
          ).normalize().toString()
          if (workFile == pref.getString(prefFile, ""))
            startActivityForResult(
              Intent(
                applicationContext,
                ReaderActivity::class.java
              ), filePick + 1
            )
          else {
            pref.edit().putInt("curSection", 0).apply()
            processFile(workFile)
          }
        } catch (ex: Exception) {
          ex.printStackTrace()
          Toast.makeText(
            this,
            "Something gone wrong ^^` Maybe try again?)",
            Toast.LENGTH_LONG
          ).show()
          startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("*/*")
          }, filePick)
        }
      } else if (resultCode != Activity.RESULT_CANCELED)
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
          addCategory(Intent.CATEGORY_OPENABLE)
          setType("*/*")
        }, filePick)
      else finish()
    } else if (requestCode == filePick + 1) {
      if (resultCode == Activity.RESULT_OK)
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
          addCategory(Intent.CATEGORY_OPENABLE)
          setType("*/*")
        }, filePick)
      else if (resultCode == Activity.RESULT_CANCELED)
        finish()
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  override fun onDestroy() {
    scope.cancel()
    super.onDestroy()
  }
}
