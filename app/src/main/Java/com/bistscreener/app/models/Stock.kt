package com.bistscreener.app.models

/**
 * Basit hisse senedi veri modeli.
 *
 * @param symbol BIST kodu (örn: THYAO)
 * @param name Şirket adı
 * @param price Güncel fiyat (TL)
 * @param pe Fiyat / Kazanç oranı (F/K)
 * @param pbv Fiyat / Defter Değeri oranı (P/B)
 * @param debtEbitda Net Borç / FAVÖK oranı
 * @param volume Ortalama işlem hacmi (TL veya adet)
 * @param catalyst Yakın zamanda olumlu bir katalizör var mı? (haber, anlaşma, vb.)
 */
data class Stock(
    val symbol: String,
    val name: String,
    val price: Double,
    val pe: Double,
    val pbv: Double,
    val debtEbitda: Double,
    val volume: Double,
    val catalyst: Boolean
)
