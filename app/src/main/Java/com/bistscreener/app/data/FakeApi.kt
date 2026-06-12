package com.bistscreener.app.data

import com.bistscreener.app.models.Stock

/**
 * Şimdilik gerçek bir borsa API'sine bağlanmıyoruz; örnek/sahte verilerle
 * (Türkiye Borsası'nda işlem gören gerçek hisse kodları, TEMSİLİ rakamlar)
 * uygulamanın akışını test etmeye yarar.
 *
 * Gerçek veriye geçmek için bu nesneyi Retrofit tabanlı bir servisle
 * değiştirebilirsiniz (örn. Foreks, Matriks, Investing veya kendi API'niz).
 *
 * NOT: Buradaki sayılar gerçek piyasa verisi DEĞİLDİR, sadece demo amaçlıdır.
 */
object FakeApi {

    fun getStocks(): List<Stock> {
        return listOf(
            Stock("THYAO", "Türk Hava Yolları", 285.50, 6.2, 1.8, 1.5, 45_000_000.0, true),
            Stock("ASELS", "Aselsan", 68.40, 18.5, 4.2, 0.8, 30_000_000.0, true),
            Stock("EREGL", "Ereğli Demir Çelik", 42.10, 9.8, 1.1, 2.0, 20_000_000.0, false),
            Stock("BIMAS", "BİM Mağazalar", 540.00, 24.0, 12.5, 0.3, 3_000_000.0, false),
            Stock("KCHOL", "Koç Holding", 195.30, 5.5, 1.4, 1.9, 8_000_000.0, true),
            Stock("SISE", "Şişecam", 38.20, 8.1, 1.2, 2.4, 15_000_000.0, false),
            Stock("TUPRS", "Tüpraş", 165.00, 4.9, 2.1, 1.7, 6_500_000.0, true),
            Stock("EKGYO", "Emlak Konut GYO", 9.85, 7.3, 0.9, 0.5, 60_000_000.0, true),
            Stock("PETKM", "Petkim", 18.40, 30.5, 2.9, 4.1, 25_000_000.0, false),
            Stock("AKBNK", "Akbank", 58.70, 4.2, 0.95, 0.0, 50_000_000.0, true)
        )
    }
}
