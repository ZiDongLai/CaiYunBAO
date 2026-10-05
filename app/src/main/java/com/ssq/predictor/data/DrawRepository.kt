package com.ssq.predictor.data

import android.content.Context
import com.ssq.predictor.model.DrawResult
import org.json.JSONArray
import org.json.JSONObject

class DrawRepository(context: Context) {
    private val prefs = context.getSharedPreferences("ssq_cache", Context.MODE_PRIVATE)
    private val api = CwlApi()

    fun loadCache(): List<DrawResult> = runCatching {
        val raw = prefs.getString(KEY_DATA, null) ?: return emptyList()
        val a = JSONArray(raw)
        buildList {
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                val redsArray = o.getJSONArray("reds")
                val reds = (0 until redsArray.length()).map { redsArray.getInt(it) }
                add(
                    DrawResult(
                        issue = o.getString("issue"),
                        date = o.getString("date"),
                        reds = reds,
                        blue = o.getInt("blue"),
                        sales = o.optString("sales").takeIf { it.isNotBlank() },
                        poolMoney = o.optString("poolMoney").takeIf { it.isNotBlank() }
                    )
                )
            }
        }
    }.getOrDefault(emptyList())

    fun lastUpdated(): Long = prefs.getLong(KEY_UPDATED, 0L)

    fun refresh(): List<DrawResult> {
        val draws = api.fetchLatest()
        save(draws)
        return draws
    }

    private fun save(draws: List<DrawResult>) {
        val a = JSONArray()
        draws.forEach { d ->
            a.put(JSONObject().apply {
                put("issue", d.issue)
                put("date", d.date)
                put("reds", JSONArray(d.reds))
                put("blue", d.blue)
                put("sales", d.sales ?: "")
                put("poolMoney", d.poolMoney ?: "")
            })
        }
        prefs.edit()
            .putString(KEY_DATA, a.toString())
            .putLong(KEY_UPDATED, System.currentTimeMillis())
            .apply()
    }

    companion object {
        private const val KEY_DATA = "draws"
        private const val KEY_UPDATED = "updated"
    }
}
