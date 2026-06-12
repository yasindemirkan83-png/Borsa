package com.bistscreener.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bistscreener.app.logic.FilterEngine
import com.bistscreener.app.models.Stock

class StockAdapter(private val stocks: List<Stock>) :
    RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    class StockViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val symbol: TextView = view.findViewById(R.id.tvSymbol)
        val name: TextView = view.findViewById(R.id.tvName)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val score: TextView = view.findViewById(R.id.tvScore)
        val signal: TextView = view.findViewById(R.id.tvSignal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val stock = stocks[position]
        val score = FilterEngine.score(stock)
        val signal = FilterEngine.signal(stock)

        holder.symbol.text = stock.symbol
        holder.name.text = stock.name
        holder.price.text = String.format("%.2f ₺", stock.price)
        holder.score.text = "Skor: $score"
        holder.signal.text = signal

        val color = when (signal) {
            "AL" -> 0xFF2E7D32.toInt()    // yeşil
            "İZLE" -> 0xFFF9A825.toInt()  // sarı
            else -> 0xFFC62828.toInt()    // kırmızı
        }
        holder.signal.setTextColor(color)
    }

    override fun getItemCount(): Int = stocks.size
}
