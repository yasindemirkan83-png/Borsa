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

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        loadStocks()
    }

    /**
     * Yahoo Finance üzerinden canlı BIST verisini çeker. Başarısız olursa
     * (internet yok, endpoint değişti vb.) yedek olarak FakeApi verisini gösterir.
     */
    private fun loadStocks() {
        progressBar.visibility = View.VISIBLE
        errorText.visibility = View.GONE

        lifecycleScope.launch {
            val result = StockRepository.getStocks()

            progressBar.visibility = View.GONE
            swipeRefresh.isRefreshing = false

            result.onSuccess { stocks ->
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
