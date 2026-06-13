package com.bistscreener.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                File(filesDir, "crash_log.txt").writeText(throwable.stackTraceToString())
            } catch (_: Exception) {
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv = findViewById<TextView>(R.id.tvDiag)

        val crashFile = File(filesDir, "crash_log.txt")
        if (crashFile.exists()) {
            tv.text = "Önceki açılışta hata oluştu:\n\n${crashFile.readText()}"
            crashFile.delete()
        }
    }
}
