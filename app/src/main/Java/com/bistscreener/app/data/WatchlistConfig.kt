package com.bistscreener.app.data

/**
 * Takip listesi: hangi BIST hisselerinin canlı verisi çekilecek + serbest API'de
 * bulunmayan ama puanlama için gerekli alanlar (Net Borç/FAVÖK, katalizör).
 *
 * Bu listeyi dilediğin gibi düzenle (yeni hisse ekle/çıkar). Yahoo Finance
 * sembolleri için BIST kodlarının sonuna otomatik olarak ".IS" eklenir.
 *
 * debtEbitda ve catalyst alanları manuel girilir; periyodik olarak (örn. bilanço
 * dönemlerinde) güncellemen gerekir.
 */
data class WatchlistItem(
    val symbol: String,      // BIST kodu, örn. "THYAO"
    val name: String,        // Şirket adı (Yahoo'dan boş gelirse yedek olarak kullanılır)
    val debtEbitda: Double,  // Net Borç / FAVÖK
    val catalyst: Boolean    // Yakın zamanda olumlu bir gelişme/haber var mı?
)

object WatchlistConfig {

    val items = listOf(
        WatchlistItem("THYAO", "Türk Hava Yolları", 1.5, true),
        WatchlistItem("ASELS", "Aselsan", 0.8, true),
        WatchlistItem("EREGL", "Ereğli Demir Çelik", 2.0, false),
        WatchlistItem("BIMAS", "BİM Mağazalar", 0.3, false),
        WatchlistItem("KCHOL", "Koç Holding", 1.9, true),
        WatchlistItem("SISE", "Şişecam", 2.4, false),
        WatchlistItem("TUPRS", "Tüpraş", 1.7, true),
        WatchlistItem("EKGYO", "Emlak Konut GYO", 0.5, true),
        WatchlistItem("PETKM", "Petkim", 4.1, false),
        WatchlistItem("AKBNK", "Akbank", 0.0, true)
    )

    /** Yahoo Finance için "SEMBOL.IS,SEMBOL2.IS,..." formatında sembol listesi. */
    fun yahooSymbols(): String = items.joinToString(",") { "${it.symbol}.IS" }

    fun find(symbol: String): WatchlistItem? =
        items.find { it.symbol.equals(symbol, ignoreCase = true) }
}
