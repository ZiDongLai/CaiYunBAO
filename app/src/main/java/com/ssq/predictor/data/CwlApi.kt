package com.ssq.predictor.data

import com.ssq.predictor.model.DrawResult
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CwlApi {
    private val landing = "https://www.cwl.gov.cn/ygkj/wqkjgg/ssq/"
    private val endpoint = "https://www.cwl.gov.cn/cwl_admin/front/cwlkj/search/kjxx/findDrawNotice" +
        "?name=ssq&issueCount=&issueStart=&issueEnd=&dayStart=&dayEnd=" +
        "&pageNo=1&pageSize=200&week=&systemType=PC"

    fun fetchLatest(): List<DrawResult> {
        val cookie = bootstrapCookie()
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12_000
            readTimeout = 18_000
            instanceFollowRedirects = true
            setRequestProperty("User-Agent", USER_AGENT)
            setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01")
            setRequestProperty("Referer", landing)
            setRequestProperty("X-Requested-With", "XMLHttpRequest")
            if (cookie.isNotBlank()) setRequestProperty("Cookie", cookie)
        }
        try {
            val code = conn.responseCode
            if (code !in 200..299) error("福彩网接口 HTTP $code")
            val text = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            return parse(text)
        } finally {
            conn.disconnect()
        }
    }

    private fun bootstrapCookie(): String {
        val conn = (URL(landing).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 12_000
            instanceFollowRedirects = true
            setRequestProperty("User-Agent", USER_AGENT)
            setRequestProperty("Accept", "text/html,application/xhtml+xml")
        }
        return try {
            conn.responseCode
            conn.headerFields
                .filterKeys { it?.equals("Set-Cookie", ignoreCase = true) == true }
                .values.flatten()
                .map { it.substringBefore(';') }
                .distinct()
                .joinToString("; ")
        } finally {
            conn.disconnect()
        }
    }

    private fun parse(text: String): List<DrawResult> {
        val root = JSONObject(text)
        val array: JSONArray = root.optJSONArray("result")
            ?: root.optJSONObject("data")?.optJSONArray("result")
            ?: error("接口没有返回开奖列表")

        return buildList {
            for (i in 0 until array.length()) {
                val o = array.optJSONObject(i) ?: continue
                val issue = o.optString("code").trim()
                val reds = o.optString("red")
                    .split(',')
                    .mapNotNull { it.trim().toIntOrNull() }
                    .sorted()
                val blue = o.optString("blue").trim().toIntOrNull() ?: continue

                // Only accept structurally valid SSQ results as official draw data.
                if (issue.isBlank() || reds.size != 6 || reds.distinct().size != 6 ||
                    reds.any { it !in 1..33 } || blue !in 1..16) continue

                add(
                    DrawResult(
                        issue = issue,
                        date = o.optString("date").trim(),
                        reds = reds,
                        blue = blue,
                        sales = o.optString("sales").takeIf { it.isNotBlank() },
                        poolMoney = o.optString("poolmoney").takeIf { it.isNotBlank() }
                    )
                )
            }
        }.sortedByDescending { it.issue }
            .also { if (it.isEmpty()) error("未获得通过校验的双色球开奖数据") }
    }

    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/123.0 Mobile Safari/537.36"
    }
}
