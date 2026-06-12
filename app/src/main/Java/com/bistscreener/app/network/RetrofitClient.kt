package com.bistscreener.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Yahoo Finance gayri resmi sorgu endpoint'ine bağlanan Retrofit istemcisi.
 *
 * NOT: Bu, Yahoo'nun resmi/desteklenen bir API'si değildir. Endpoint URL'i veya
 * yanıt şeması değişebilir/devre dışı bırakılabilir. Üretim/ticari kullanım için
 * lisanslı bir veri sağlayıcısına (Foreks, Matriks, Finnet, vb.) geçilmesi önerilir.
 * BASE_URL ve YahooFinanceApi'yi yeni sağlayıcının dokümantasyonuna göre değiştirmek
 * yeterli olacaktır; StockRepository'deki mapleme mantığını da güncellemeniz gerekir.
 */
object RetrofitClient {

    private const val BASE_URL = "https://query1.finance.yahoo.com/"

    val api: YahooFinanceApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                // Yahoo bazı durumlarda User-Agent olmadan isteği reddedebiliyor.
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Android; BIST-Screener/1.0)")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YahooFinanceApi::class.java)
    }
}
