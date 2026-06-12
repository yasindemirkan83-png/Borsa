package com.bistscreener.app.logic

import com.bistscreener.app.models.Stock

/**
 * Basit kural tabanlı tarama / puanlama motoru.
 *
 * Her kriterin sağlanması durumunda 20 puan eklenir, toplam 0-100 arası bir skor üretir.
 * Eşik değerleri (PE, PBV, Borç/FAVÖK, Hacim) ihtiyacına göre kolayca değiştirilebilir.
 */
object FilterEngine {

    // --- Eşik değerleri (buradan kolayca ayarlanabilir) ---
    private const val MAX_PE = 25.0
    private const val MAX_PBV = 3.0
    private const val MAX_DEBT_EBITDA = 3.0
    private const val MIN_VOLUME = 2_000_000.0

    private const val BUY_THRESHOLD = 80
    private const val WATCH_THRESHOLD = 60

    /**
     * Verilen hisse için 0-100 arasında bir skor hesaplar.
     */
    fun score(stock: Stock): Int {
        var score = 0

        // F/K oranı pozitif ve eşik altında olmalı (negatif F/K, zarar eden şirketi gösterir)
        if (stock.pe > 0 && stock.pe < MAX_PE) score += 20

        // Piyasa Değeri / Defter Değeri eşik altında olmalı
        if (stock.pbv > 0 && stock.pbv < MAX_PBV) score += 20

        // Net Borç / FAVÖK eşik altında olmalı (yüksek borçluluk riski)
        if (stock.debtEbitda < MAX_DEBT_EBITDA) score += 20

        // Likidite: yeterli işlem hacmi
        if (stock.volume > MIN_VOLUME) score += 20

        // Olumlu katalizör (haber akışı, anlaşma, ihale, vb.)
        if (stock.catalyst) score += 20

        return score
    }

    /** Skoru AL eşiğinin üzerinde mi? */
    fun isBuy(stock: Stock): Boolean = score(stock) >= BUY_THRESHOLD

    /** İnsan tarafında okunabilir sinyal etiketi döndürür. */
    fun signal(stock: Stock): String {
        val s = score(stock)
        return when {
            s >= BUY_THRESHOLD -> "AL"
            s >= WATCH_THRESHOLD -> "İZLE"
            else -> "PAS"
        }
    }
}
