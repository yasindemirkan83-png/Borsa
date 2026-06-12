package com.bistscreener.app.network

import retrofit2.http.GET
import retrofit2.http.Query

interface YahooFinanceApi {

    /**
     * @param symbols Virgülle ayrılmış BIST sembolleri, Yahoo formatında ".IS" sonekiyle.
     *   Örn: "THYAO.IS,ASELS.IS,EREGL.IS"
     */
    @GET("v7/finance/quote")
    suspend fun getQuotes(
        @Query("symbols") symbols: String
    ): QuoteResponseWrapper
}
