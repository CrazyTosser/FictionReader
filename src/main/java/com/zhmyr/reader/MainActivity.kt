package com.zhmyr.reader

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProviders
import com.zhmyr.parser.FictionBook
import kotlinx.coroutines.*


const val filePick = 42
const val exit = 47
const val prefFile = "file"
const val CHANNEL_ID = "FB2Id"

class MainActivity : AppCompatActivity() {
    var workFile: String = ""
    lateinit var notif: NotificationCompat.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        workFile =
            getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
                .getString(prefFile, "").toString()

        if (workFile.isNotEmpty()) startActivityForResult(
            Intent(this, BookActivity::class.java).putExtra(
                "uri",
                Uri.parse(workFile)
            ), exit
        )
    }

    override fun onStart() {
        super.onStart()
        if (workFile.isEmpty()) getFile()
    }

    private fun getFile() {
        notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText("Идет выбор файла")

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(filePick, notif.build())

        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("*/*")
        }, filePick)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.notificationChannels.any { it.id == CHANNEL_ID }) return
            val name = getString(R.string.app_name)
            val descriptionText = "FictionBook Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == filePick) {
            if (data != null && resultCode == Activity.RESULT_OK) {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .cancel(filePick)

                val uri = data.data!!
                workFile = uri.toString()
                Log.v("Book", workFile)
                getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
                    .edit().apply {
                        putString(prefFile, workFile)
                    }.apply()
                startActivity(Intent(this, BookActivity::class.java).putExtra("uri", uri))

            } else getFile()
        } else if (requestCode == exit) {
            if (resultCode == Activity.RESULT_CANCELED)
                finish()
            else if (resultCode == Activity.RESULT_OK) {
                workFile = ""
                getFile()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
