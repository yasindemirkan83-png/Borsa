package com.bistscreener.app

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bistscreener.app.data.FakeApi
import com.bistscreener.app.data.StockRepository
import com.bistscreener.app.logic.FilterEngine
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Çökme yakalayıcı: uygulama bir şekilde çökerse, hata bilgisini dosyaya
        // yazar. Bir sonraki açılışta bu dosya okunup ekranda gösterilir,
        // böylece logcat'e ihtiyaç olmadan hatayı görebiliriz.
        installCrashHandler()

        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_main)

            recyclerView = findViewById(R.id.recyclerView)
            swipeRefresh = findViewById(R.id.swipeRefresh)
            progressBar = findViewById(R.id.progressBar)
            errorText = findViewById(R.id.tvError)

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.addItemDecoration(
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            )

            swipeRefresh.setOnRefreshListener { loadStocks() }

            val previousCrash = readAndClearCrashLog()
            if (previousCrash != null) {
                errorText.visibility = View.VISIBLE
                errorText.text = "Önceki açılışta hata oluştu:\n$previousCrash"
            }

            loadStocks()
        } catch (e: Exception) {
            // setContentView veya findViewById sırasında bir hata olursa
            // en azından bir mesaj göstermeye çalış, tamamen kapanma.
            errorText = TextView(this).apply {
                text = "Başlatma hatası: ${e.stackTraceToString()}"
                setPadding(24, 48, 24, 24)
            }
            setContentView(errorText)
        }
    }

    /**
     * Uygulama çökerse, hata mesajını dosyaya yazar ve süreci sonlandırır.
     * Bir sonraki açılışta bu mesaj okunup ekranda gösterilir.
     */
    private fun installCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                File(filesDir, "crash_log.txt").writeText(throwable.stackTraceToString())
            } catch (_: Exception) {
                // yazılamazsa yok say
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun readAndClearCrashLog(): String? {
        val file = File(filesDir, "crash_log.txt")
        if (!file.exists()) return null
        val content = try {
            file.readText()
        } catch (_: Exception) {
            null
        }
        file.delete()
        return content
    }

    /**
     * Yahoo Finance üzerinden canlı BIST verisini çeker. Başarısız olursa
     * (internet yok, endpoint değişti vb.) yedek olarak FakeApi verisini gösterir.
     */
    private fun loadStocks() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = StockRepository.getStocks()

            progressBar.visibility = View.GONE
            swipeRefresh.isRefreshing = false

            result.onSuccess { stocks ->
                errorText.visibility = View.GONE
                showStocks(stocks)
            }.onFailure { e ->
                errorText.visibility = View.VISIBLE
                errorText.text =
                    "Canlı veri alınamadı, örnek (demo) veriler gösteriliyor.\nHata: ${e.message}"
                showStocks(FakeApi.getStocks())
            }
        }
    }

    private fun showStocks(stocks: List<com.bistscreener.app.models.Stock>) {
        val sorted = stocks.sortedByDescending { FilterEngine.score(it) }
        recyclerView.adapter = StockAdapter(sorted)
    }
}
