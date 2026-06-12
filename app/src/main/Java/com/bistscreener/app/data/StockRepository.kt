package com.bistscreener.app.data

import com.bistscreener.app.models.Stock
import com.bistscreener.app.network.RetrofitClient

/**
 * Gerçek BIST verisini Yahoo Finance gayri resmi endpoint'inden çeker ve
 * [WatchlistConfig] içindeki manuel alanlarla (Net Borç/FAVÖK, katalizör)
 * birleştirerek [Stock] listesine dönüştürür.
 */
object StockRepository {

    suspend fun getStocks(): Result<List<Stock>> {
        return try {
            val symbols = WatchlistConfig.yahooSymbols()
            val response = RetrofitClient.api.getQuotes(symbols)

            response.quoteResponse.error?.let { err ->
                return Result.failure(Exception(err))
            }

            val stocks = response.quoteResponse.result.mapNotNull { r ->
                val code = r.symbol.removeSuffix(".IS")
                val config = WatchlistConfig.find(code)
                val price = r.regularMarketPrice ?: return@mapNotNull null

                Stock(
                    symbol = code,
                    name = config?.name ?: r.shortName ?: r.longName ?: code,
                    price = price,
                    pe = r.trailingPE ?: 0.0,
                    pbv = r.priceToBook ?: 0.0,
                    debtEbitda = config?.debtEbitda ?: 0.0,
                    volume = (r.regularMarketVolume ?: 0L).toDouble(),
                    catalyst = config?.catalyst ?: false
                )
            }

            if (stocks.isEmpty()) {
                Result.failure(Exception("Sonuç boş döndü"))
            } else {
                Result.success(stocks)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
