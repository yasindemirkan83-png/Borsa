# BIST Smart Screener (Android / Kotlin)

Basit bir BIST tarama uygulaması: Grafik çizmez, sadece **filtre + liste + skor** gösterir.

## Proje Yapısı

```
BIST-Screener/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/bistscreener/app/
│       │   ├── MainActivity.kt
│       │   ├── StockAdapter.kt
│       │   ├── models/Stock.kt
│       │   ├── data/FakeApi.kt
│       │   └── logic/FilterEngine.kt
│       └── res/
│           ├── layout/activity_main.xml
│           ├── layout/item_stock.xml
│           └── values/{strings.xml, themes.xml}
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
└── .github/workflows/build.yml   (otomatik APK üretimi için)
```

## Sistemin Çalışma Mantığı

`FilterEngine.kt` içindeki puanlama sistemi her hisse için 0-100 arası bir **skor** hesaplar:

| Kriter | Koşul | Puan |
|---|---|---|
| F/K (PE) | 0 < PE < 25 | +20 |
| P/B (PBV) | 0 < PBV < 3 | +20 |
| Net Borç/FAVÖK | < 3 | +20 |
| İşlem Hacmi | > 2.000.000 | +20 |
| Katalizör | varsa | +20 |

- **Skor ≥ 80 → "AL"**
- **Skor ≥ 60 → "İZLE"**
- **Skor < 60 → "PAS"**

Eşik değerlerini `FilterEngine.kt` başındaki `MAX_PE`, `MAX_PBV`, `MAX_DEBT_EBITDA`, `MIN_VOLUME`, `BUY_THRESHOLD`, `WATCH_THRESHOLD` sabitlerinden kolayca değiştirebilirsin.

`FakeApi.kt` içinde örnek BIST hisseleri (THYAO, ASELS, EREGL, KCHOL, TUPRS, AKBNK, vb.) bulunuyor. **Buradaki sayılar gerçek piyasa verisi DEĞİL**, demo amaçlıdır. Gerçek veri için bu dosyayı bir API çağrısıyla (Retrofit + gerçek bir borsa veri sağlayıcısı) değiştirmen gerekir.

## Canlı BIST Verisi (Retrofit)

Uygulama artık açılışta **Yahoo Finance**'in gayri resmi `v7/finance/quote` endpoint'inden canlı fiyat, hacim, F/K (PE) ve P/B (priceToBook) verilerini çekiyor. BIST kodları Yahoo'da `.IS` soneki ile sorgulanıyor (örn. `THYAO.IS`).

İlgili dosyalar:

- `network/YahooFinanceApi.kt` — Retrofit arayüzü
- `network/RetrofitClient.kt` — OkHttp + Retrofit kurulumu (User-Agent header'lı)
- `network/QuoteModels.kt` — JSON yanıt modelleri
- `data/WatchlistConfig.kt` — takip edilecek hisse listesi + serbest API'de **olmayan** alanlar (Net Borç/FAVÖK, katalizör) burada manuel tutulur
- `data/StockRepository.kt` — canlı veriyi `WatchlistConfig`'teki manuel verilerle birleştirir

### Önemli Notlar

- ⚠️ Yahoo Finance'in bu endpoint'i **resmi/desteklenen bir API değildir**. Herhangi bir zaman çalışmayı durdurabilir veya yanıt formatı değişebilir. Ciddi/ticari kullanım için Foreks, Matriks, Finnet gibi lisanslı bir veri sağlayıcısına geçmen önerilir — sadece `RetrofitClient`, `YahooFinanceApi`, `QuoteModels` ve `StockRepository`'deki mapleme kısmını değiştirmen yeterli.
- Net Borç/FAVÖK ve "katalizör" gibi bilgiler ücretsiz API'lerde genelde yok; bu yüzden `WatchlistConfig.kt` içinde **manuel** olarak tutuluyor. Bilanço dönemlerinde bu değerleri elle güncellemen gerekir.
- Hangi hisselerin takip edileceğini `WatchlistConfig.items` listesinden değiştirebilirsin (ekleme/çıkarma).
- Uygulama açılışta canlı veri çekemezse (internet yok, endpoint hata verirse vb.) ekranda kırmızı bir uyarı gösterir ve **FakeApi**'deki demo verilerle devam eder — yani uygulama hiçbir zaman boş ekran göstermez.
- Aşağı çekerek (pull-to-refresh) listeyi yeniden çekebilirsin.
- `AndroidManifest.xml`'e `INTERNET` izni eklendi.

## APK Nasıl Alınır?

### Yöntem 1 — Android Studio (en kolay)

1. Bu klasörü bilgisayarına indir / GitHub'dan klonla.
2. Android Studio'yu aç → **Open** → `BIST-Screener` klasörünü seç.
3. Android Studio, Gradle wrapper'ı yoksa otomatik oluşturmayı önerecektir — kabul et (ya da **File > Sync Project with Gradle Files**).
4. Üstteki menüden **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. APK, `app/build/outputs/apk/debug/app-debug.apk` içinde oluşur.

### Yöntem 2 — GitHub Actions (otomatik, telefonsuz/Studio'suz)

1. Bu klasörü kendi GitHub reponun içine yükle (repo adı örn. `BIST-Screener`).
2. `main` branşına push yaptığında `.github/workflows/build.yml` otomatik çalışır. Workflow, `gradlew` dosyası repoda yoksa önce onu kendisi oluşturur (`gradle wrapper`), sonra `./gradlew assembleDebug` ile APK'yı derler.
3. `Actions` sekmesinde **bist-screener-debug-apk** adlı bir artifact (APK) oluşur.
4. Artifact'i indir, telefonuna kur (Bilinmeyen kaynaklardan yükleme izni gerekir).

> Not: İlk seferde "Re-run jobs" gerekebilir; bazı runner'larda `gradle wrapper` adımı sonrası ilk derleme biraz daha uzun sürer (Gradle indirme).

## Sonraki Adımlar (Geliştirme Önerileri)

- Lisanslı bir veri sağlayıcısına geçerek Net Borç/FAVÖK'ü de canlı çek (şu an manuel).
- Arama/filtreleme kutusu ekle (örn. sembole göre arama).
- Skor kriterlerini kullanıcı arayüzünden ayarlanabilir hale getir (SharedPreferences).
- Liste üstüne "Sadece AL sinyalleri" filtre anahtarı ekle (`FilterEngine.isBuy`).
- Periyodik arka plan güncellemesi için WorkManager ile zamanlanmış veri çekme ekle.
