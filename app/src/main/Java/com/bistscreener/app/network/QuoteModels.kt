package com.bistscreener.app.network

import com.google.gson.annotations.SerializedName

/**
 * Yahoo Finance "v7/finance/quote" gayri resmi endpoint'inin yanıt modeli.
 * Örnek istek: https://query1.finance.yahoo.com/v7/finance/quote?symbols=THYAO.IS,ASELS.IS
 */
data class QuoteResponseWrapper(
    @SerializedName("quoteResponse") val quoteResponse: QuoteResponse
)

data class QuoteResponse(
    @SerializedName("result") val result: List<QuoteResult>,
    @SerializedName("error") val error: String?
)

data class QuoteResult(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("shortName") val shortName: String?,
    @SerializedName("longName") val longName: String?,
    @SerializedName("regularMarketPrice") val regularMarketPrice: Double?,
    @SerializedName("regularMarketVolume") val regularMarketVolume: Long?,
    @SerializedName("trailingPE") val trailingPE: Double?,
    @SerializedName("priceToBook") val priceToBook: Double?
)
