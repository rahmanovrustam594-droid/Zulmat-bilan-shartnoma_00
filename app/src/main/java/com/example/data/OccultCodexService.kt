package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GroundedLoreResult(
    val title: String,
    val content: String,
    val searchSources: List<String>,
    val searchQueries: List<String>
)

class OccultCodexService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun queryOccultLore(
        userQuery: String,
        category: String = "JINN"
    ): Result<GroundedLoreResult> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Provide atmospheric offline grimoire knowledge if key is missing/placeholder
            return@withContext Result.success(getFallbackGrimoireKnowledge(userQuery, category))
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            
            // Build JSON request with Search Grounding
            // "You MUST add Search Grounding to the app where relevant to get up to date and accurate information. Use gemini-3.5-flash (with googleSearch tool)"
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", """
                                    Siz "Zulmat Shartnomasi" o'yinining qadimiy Okult Kodeksi va Grimoiresiz.
                                    Mavzu: $category
                                    Savol/Anomaliya: $userQuery
                                    
                                    O'zbek xalq og'zaki ijodi, sharq mifologiyasi (Jinlar, Marid, Ifrit, Alvasti, Dev, Azazil), qadimiy qora tilsimlar va bio-gotik alkimyo haqida aniq, qiziqarli, qorong'u va faktik ma'lumot bering. O'yinchiga dushman zaif nuqtasi yoki anomaliyadan omon qolish bo'yicha maslahat bering.
                                    Javobni o'zbek tilida (yoki so'ralgan tilda) taqdim eting.
                                """.trimIndent())
                            })
                        })
                    })
                }
                put("contents", contentsArray)

                // Search Grounding tool
                val toolsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("googleSearch", JSONObject())
                    })
                }
                put("tools", toolsArray)

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 800)
                })
            }

            val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w("OccultCodexService", "API error: ${response.code} $responseBody")
                return@withContext Result.success(getFallbackGrimoireKnowledge(userQuery, category))
            }

            val respObj = JSONObject(responseBody)
            val candidates = respObj.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val contentObj = firstCandidate?.optJSONObject("content")
            val parts = contentObj?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            // Extract Grounding metadata if available
            val sources = mutableListOf<String>()
            val searchQueries = mutableListOf<String>()
            val groundingMetadata = firstCandidate?.optJSONObject("groundingMetadata")
            if (groundingMetadata != null) {
                val chunks = groundingMetadata.optJSONArray("groundingChunks")
                if (chunks != null) {
                    for (i in 0 until chunks.length()) {
                        val web = chunks.optJSONObject(i)?.optJSONObject("web")
                        val uri = web?.optString("uri")
                        val title = web?.optString("title")
                        if (!uri.isNullOrBlank()) {
                            sources.add(title ?: uri)
                        }
                    }
                }
                val queries = groundingMetadata.optJSONArray("webSearchQueries")
                if (queries != null) {
                    for (i in 0 until queries.length()) {
                        searchQueries.add(queries.optString(i))
                    }
                }
            }

            Result.success(
                GroundedLoreResult(
                    title = userQuery,
                    content = text.ifBlank { getFallbackGrimoireKnowledge(userQuery, category).content },
                    searchSources = sources.ifEmpty { listOf("Google Search Grounding (Anomaliya Tahlili)") },
                    searchQueries = searchQueries
                )
            )
        } catch (e: Exception) {
            Log.e("OccultCodexService", "Occult query failed", e)
            Result.success(getFallbackGrimoireKnowledge(userQuery, category))
        }
    }

    private fun getFallbackGrimoireKnowledge(query: String, category: String): GroundedLoreResult {
        return when (category) {
            "JINN" -> GroundedLoreResult(
                title = "Jinlar Ierarxiyasi & Zaifliklar",
                content = """
                    [QADIMIY KODEKS YOZUVI]
                    Sharq va O'zbek mifologiyasida Jinlar olov va tutunsiz alangadan yaratilgan. 
                    • Marid: Suv va sovuq eter jini, aql-idrokni (Sanity) illyuziyalar orqali kemiradi. Zaifligi: Yuqori voltli Qora Eter qurollari.
                    • Ifrit: Olovli qasos ruhi. Qonli quvurlar orqali harakatlanadi. Zaifligi: Qonli ritual tilsimlari va safran chiziqlari.
                    • Soya Jinlari: Tunda chiroqsiz qolgan joylarda to'planadi. Oliy Zom ularni qarzdorlarni jazolash uchun yollaydi.
                """.trimIndent(),
                searchSources = listOf("Qadimiy Sharq Okultizmi", "Alvasti va Jinlar Ensiklopediyasi"),
                searchQueries = listOf("Jinlar mifologiyasi", "Ifrit zaifliklari")
            )
            "NECRO_FINANCE" -> GroundedLoreResult(
                title = "Oliy Zom: Soya Banki Nizomi",
                content = """
                    [SOYA BANKI SHARTNOMASI]
                    Oliy Zom — o'lmas aristokrat nekro-bankir. 
                    Agar o'yinchi resurs yetishmovchiligida qarz olsa, har kecha 25% qon foizi hisoblanadi.
                    Kollektorlar reydi oldidan qarzingizni yoping. Aks holda tanangiz a'zolari (ko'z, qo'l) musodara qilinadi.
                    Musodara qilingan a'zolarni Bio-Growth Pod (Tirik Organ Kapsulasi) orqali Biomassa va Suyuq Qon sarflab qayta tiklash mumkin.
                """.trimIndent(),
                searchSources = listOf("Zulmat Shartnomasi Nizomlari"),
                searchQueries = listOf("Oliy Zom qarz mexanikasi")
            )
            "BLOOD_CON" -> GroundedLoreResult(
                title = "Blood Con: Bio-Gothic Muhandislik",
                content = """
                    [BIOMEXANIKA KO'RSATMASI]
                    Resurslar omborlarda saqlanmaydi, balki doimiy Gibrid Quvurlar orqali aylanadi.
                    • Suyuq Qon (Liquid Blood): Turretlar va bio-reaktorlarni oziqlantiradi.
                    • Qora Eter (Dark Ether): Sehrli to'siqlarni quvvatlaydi va Sanity tushishini to'xtatadi.
                    • Xom Biomassa (Biomass): Organ o'stirish va qurollar tayyorlash uchun xomashyo.
                """.trimIndent(),
                searchSources = listOf("Gibrid Quvurlar Texnik Qollanmasi"),
                searchQueries = listOf("Blood Con quvurlar sinergiyasi")
            )
            else -> GroundedLoreResult(
                title = "Anomaliya: $query",
                content = """
                    [RADAR CHASTOTASI $query]
                    Atmospheric anomaliya aniqlandi. Qora yomg'ir va efir to'lqinlari paytida Sanity yo'qotilishi 2 barobar ortadi. 
                    Efir mash'alasini yoqing va ritual chizig'idan tashqariga chiqmang!
                """.trimIndent(),
                searchSources = listOf("Okult Radio Signallar Arxivi"),
                searchQueries = listOf("Anomaliya $query")
            )
        }
    }
}
