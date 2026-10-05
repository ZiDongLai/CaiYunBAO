package com.ssq.predictor

import android.app.Application
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssq.predictor.data.DrawRepository
import com.ssq.predictor.model.*
import com.ssq.predictor.predict.PredictionEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

private val Gold = Color(0xFFD1AA45)
private val BrightGold = Color(0xFFF1D37A)
private val DeepGold = Color(0xFF8A641B)
private val AntiqueGold = Color(0xFFB78B2B)
private val PaleGold = Color(0xFFFFF1C9)
private val CreamGold = Color(0xFFFFFAEC)
private val WarmBackground = Color(0xFFFFF9EC)
private val LuckyRed = Color(0xFF9E2A24)
private val DeepRed = Color(0xFF6F1F1B)
private val SoftRed = Color(0xFFF8E8E4)
private val BallRed = Color(0xFFC83D3D)
private val BallBlue = Color(0xFF2B67B2)
private val Ink = Color(0xFF31281B)
private val Muted = Color(0xFF746B5D)
private val ImperialRed = Color(0xFF781B16)
private val LacquerRed = Color(0xFF4F130F)
private val Vermilion = Color(0xFFB52D22)
private val RoyalGold = Color(0xFFE0B13E)
private val GoldFoil = Color(0xFFFFE59A)
private val DarkGold = Color(0xFF9A6B10)
private val Porcelain = Color(0xFFFFF7DF)
private const val APP_VERSION = "V0.9.0 RC1"
private const val LEGAL_ACCEPT_KEY = "legal_accept_2026_09_30"

private val PRIVACY_POLICY_TEXT = """
《彩运宝隐私政策》

更新日期：2026年9月30日

1. 基本说明
彩运宝是一款彩票开奖信息查询、历史数据参考、号码管理与娱乐选号工具。当前商店候选版本不要求注册账号，不提供彩票销售、代购、充值、投注、兑奖或奖金提现服务。

2. 当前版本处理的信息
（1）本地数据：您主动保存的号码、彩宝库收藏、音效与动画设置等，仅保存在本机应用数据中。
（2）网络访问：为更新公开开奖信息，应用会通过互联网访问公开数据页面。网络连接过程中，目标网站或网络服务提供方可能依法记录常规网络信息，例如IP地址、请求时间和基础请求信息。
（3）剪贴板：仅在您主动点击“复制号码”等按钮时向系统剪贴板写入您选择的号码；当前版本不会后台读取剪贴板内容。

3. 当前版本不申请的敏感权限
当前版本不申请定位、通讯录、短信、通话记录、相机、麦克风、相册或存储读取权限。

4. 第三方SDK
当前商店候选版本未接入广告、统计、支付、登录或推送类第三方SDK。如后续版本增加相关服务，我们会在更新后的隐私政策中说明。

5. 数据保存与删除
本地保存的号码和设置由Android应用数据区保存。您可以在应用内删除相应号码，也可以通过系统“清除应用数据”或卸载应用删除本地数据。

6. 未成年人
彩票具有偶然性。未成年人不得购买彩票和兑奖。请监护人合理引导未成年人使用互联网产品。

7. 联系与更新
本政策如有重大调整，将在新版本中提示。正式上架版本的运营主体和联系信息，以应用商店开发者信息及公开隐私政策页面为准。
""".trimIndent()

private val USER_AGREEMENT_TEXT = """
《彩运宝用户协议》

更新日期：2026年9月30日

1. 服务定位
彩运宝提供彩票开奖公开信息展示、历史数据参考、娱乐性选号、号码保存与收藏管理功能。

2. 非购彩平台声明
彩运宝不销售彩票，不提供网络购彩、代购、合买、充值、投注、兑奖、奖金提现等服务，也不向用户承诺任何中奖结果。

3. 选号与数据说明
智能选号、随机选号、均衡选号及历史数据统计仅供娱乐和信息参考。彩票开奖结果具有随机性，任何选号结果均不代表或保证未来开奖结果、中奖概率或收益。

4. 数据来源与时效
应用展示的开奖信息来自公开开奖信息。网络、上游页面调整或其他技术原因可能造成延迟、暂时无法更新或缓存显示，请以彩票发行机构最终公布的信息为准。

5. 非官方声明
彩运宝为独立第三方工具，与中国福利彩票发行管理中心、中国体育彩票发行管理中心及相关彩票发行、销售机构不存在隶属、授权或合作关系。

6. 理性参与
请理性看待彩票娱乐属性，量力而行。未成年人不得购买彩票和兑奖。

7. 软件使用
请勿利用本应用从事违法违规活动。因设备、网络或第三方公开数据服务变化导致的暂时不可用，我们会尽力修复，但不对不可控因素作绝对可用性承诺。
""".trimIndent()

private val COMPLIANCE_NOTICE_TEXT = """
彩运宝仅提供开奖结果查询、历史数据参考、号码管理与娱乐选号。
不售彩 · 不代购 · 不充值投注 · 不兑奖 · 不承诺中奖。
本应用为独立第三方工具，非彩票发行机构官方应用。
请理性购彩，未成年人不得购买彩票和兑奖。
""".trimIndent()

enum class FortuneSound(
    val key: String,
    val label: String,
    val description: String,
    val resourceName: String,
    val userAdded: Boolean = false,
    val storeVisible: Boolean = true
) {
    COIN_RAIN("coin_rain", "金币暴雨", "大量金币连续洒落的娱乐音效", "fortune_coin_rain"),
    VAULT_POUR("vault_pour", "金库金币", "厚重金币与金属倾倒音效", "fortune_vault_pour"),
    ATM_COUNT("atm_count", "点钞机", "快速机械点钞音效", "fortune_atm_count"),
    CASH_COUNT("cash_count", "纸币点钞", "纸币快速翻动与清点音效", "fortune_cash_count"),
    FORTUNE_BELL("fortune_bell", "福运铃铛", "清脆金铃与吉祥收尾", "fortune_bell"),
    GOLD_INGOT("gold_ingot", "金元宝落下", "短促金属亮音与金币落下音效", "fortune_ingot"),

    // 商店候选版：保留普通自定义娱乐音效；直接暗示巨额资金到账的语音不在设置列表展示。
    ALIPAY_1M("alipay_1m", "自定义语音①", "自定义语音", "alipay_1m", true, false),
    CASH_COUNTER_CUSTOM("cash_counter", "自定义点钞", "播放你添加的点钞机原音", "cash_counter", true),
    COIN_ARRIVAL_1("coin_arrival_1", "金币音效①", "播放你添加的第一组金币音效", "coin_arrival_1", true),
    COIN_ARRIVAL_2("coin_arrival_2", "金币音效②", "播放你添加的第二组金币音效", "coin_arrival_2", true),
    MAGIC_COIN("magic_coin", "魔法钱币", "播放你添加的魔法钱币音效", "magic_coin", true),
    COIN_ARRIVAL_3("coin_arrival_3", "金币音效③", "播放你新增的第三组金币音效", "coin_arrival_3", true),
    ALIPAY_500W("alipay_500w", "自定义语音②", "自定义语音", "alipay_500w", true, false),
    ALIPAY_520W("alipay_520w", "自定义语音③", "自定义语音", "alipay_520w", true, false)
}

private fun fortuneSoundRes(context: Context, sound: FortuneSound): Int =
    context.resources.getIdentifier(sound.resourceName, "raw", context.packageName)

private fun fortuneSoundAvailable(context: Context, sound: FortuneSound): Boolean =
    fortuneSoundRes(context, sound) != 0

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scheme = lightColorScheme(
                primary = Gold,
                onPrimary = Color(0xFF2F2200),
                primaryContainer = PaleGold,
                onPrimaryContainer = Ink,
                secondary = LuckyRed,
                onSecondary = Color.White,
                background = WarmBackground,
                onBackground = Ink,
                surface = CreamGold,
                onSurface = Ink,
                outline = Color(0xFFD7C79D)
            )
            MaterialTheme(colorScheme = scheme) { LegalGate() }
        }
    }
}

private enum class LegalDocument { PRIVACY, AGREEMENT, NOTICE }

private fun legalTitle(doc: LegalDocument): String = when (doc) {
    LegalDocument.PRIVACY -> "隐私政策"
    LegalDocument.AGREEMENT -> "用户协议"
    LegalDocument.NOTICE -> "合规与理性提示"
}

private fun legalContent(doc: LegalDocument): String = when (doc) {
    LegalDocument.PRIVACY -> PRIVACY_POLICY_TEXT
    LegalDocument.AGREEMENT -> USER_AGREEMENT_TEXT
    LegalDocument.NOTICE -> COMPLIANCE_NOTICE_TEXT
}

@Composable
private fun LegalGate() {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("caiyunbao_legal", Context.MODE_PRIVATE)
    }
    var accepted by remember { mutableStateOf(prefs.getBoolean(LEGAL_ACCEPT_KEY, false)) }
    var detail by remember { mutableStateOf<LegalDocument?>(null) }

    if (accepted) {
        App()
        return
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFFFFBED),
            border = BorderStroke(1.5.dp, Gold),
            shadowElevation = 12.dp
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("欢迎使用彩运宝", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DeepRed)
                Text(
                    "使用前请阅读并同意《用户协议》和《隐私政策》。在您同意前，彩运宝不会启动开奖结果查询网络请求。",
                    color = Ink,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
                Surface(
                    color = Color(0xFFFFF1C9),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE1C36D))
                ) {
                    Text(
                        COMPLIANCE_NOTICE_TEXT,
                        Modifier.padding(12.dp),
                        color = DeepGold,
                        fontSize = 11.5.sp,
                        lineHeight = 18.sp
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = { detail = LegalDocument.AGREEMENT }) { Text("用户协议", color = DeepGold) }
                    TextButton(onClick = { detail = LegalDocument.PRIVACY }) { Text("隐私政策", color = DeepGold) }
                }
                Button(
                    onClick = {
                        prefs.edit().putBoolean(LEGAL_ACCEPT_KEY, true).apply()
                        accepted = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LuckyRed)
                ) {
                    Text("同意并继续", color = Color.White, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { (context as? Activity)?.finish() },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Gold)
                ) {
                    Text("不同意并退出", color = Muted)
                }
            }
        }
    }

    detail?.let { doc ->
        LegalTextDialog(doc = doc, onDismiss = { detail = null })
    }
}

@Composable
private fun LegalTextDialog(doc: LegalDocument, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.88f),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFFFFBED),
            border = BorderStroke(1.3.dp, Gold)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(legalTitle(doc), Modifier.weight(1f), fontSize = 20.sp, fontWeight = FontWeight.Black, color = DeepRed)
                    TextButton(onClick = onDismiss) { Text("关闭", color = DeepGold) }
                }
                HorizontalDivider(color = Color(0xFFE4CF91))
                Spacer(Modifier.height(8.dp))
                Text(
                    legalContent(doc),
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    color = Ink,
                    fontSize = 12.sp,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LuckyRed)
                ) {
                    Text("我已阅读", color = Color.White)
                }
            }
        }
    }
}

data class SavedNumber(
    val id: Long,
    val prediction: Prediction,
    val source: TicketSource,
    val savedAt: Long,
    val selected: Boolean = true
)

data class TreasureNumber(
    val id: Long,
    val prediction: Prediction,
    val source: TicketSource,
    val addedAt: Long,
    val selected: Boolean = true,
    val pinned: Boolean = false
)

data class AppState(
    val draws: List<DrawResult> = emptyList(),
    val tickets: List<PickItem> = emptyList(),
    val dltTickets: List<DltPickItem> = emptyList(),
    val savedNumbers: List<SavedNumber> = emptyList(),
    val treasureNumbers: List<TreasureNumber> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val lastUpdated: Long = 0L,
    val count: Int = 5,
    val mode: PredictMode = PredictMode.BALANCED,
    val game: LotteryGame = LotteryGame.SSQ,
    val soundEnabled: Boolean = true,
    val completionSound: FortuneSound = FortuneSound.COIN_RAIN,
    val animationEnabled: Boolean = true
)

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = DrawRepository(app)
    private val prefs = app.getSharedPreferences("caiyunbao_settings", Context.MODE_PRIVATE)
    private var nextTicketId = 1L
    private var nextDltTicketId = 1L
    private var nextSavedId = 1L
    private var nextTreasureId = 1L

    var state by mutableStateOf(
        AppState(
            draws = repo.loadCache(),
            lastUpdated = repo.lastUpdated(),
            soundEnabled = prefs.getBoolean("sound_enabled", true),
            completionSound = runCatching {
                FortuneSound.valueOf(prefs.getString("completion_sound", FortuneSound.COIN_RAIN.name) ?: FortuneSound.COIN_RAIN.name)
            }.getOrDefault(FortuneSound.COIN_RAIN).let { if (it.storeVisible) it else FortuneSound.COIN_RAIN },
            animationEnabled = prefs.getBoolean("animation_enabled", true)
        )
    )
        private set

    init {
        val saved = loadSavedNumbers()
        val treasure = loadTreasureNumbers()
        nextSavedId = (saved.maxOfOrNull { it.id } ?: 0L) + 1L
        nextTreasureId = (treasure.maxOfOrNull { it.id } ?: 0L) + 1L
        state = state.copy(savedNumbers = saved, treasureNumbers = treasure)
        refresh()
    }

    fun refresh() {
        if (state.loading) return
        state = state.copy(loading = true, error = null)
        viewModelScope.launch {
            val result = runCatching { withContext(Dispatchers.IO) { repo.refresh() } }
            state = result.fold(
                onSuccess = {
                    state.copy(
                        draws = it,
                        loading = false,
                        error = null,
                        lastUpdated = repo.lastUpdated()
                    )
                },
                onFailure = {
                    state.copy(
                        loading = false,
                        error = if (state.draws.isEmpty()) {
                            "暂时无法连接开奖数据源：${it.message ?: "网络错误"}"
                        } else {
                            "更新失败，继续显示本地已验证数据"
                        }
                    )
                }
            )
        }
    }

    fun setCount(n: Int) { state = state.copy(count = n.coerceIn(1, 100)) }
    fun setMode(mode: PredictMode) { state = state.copy(mode = mode) }

    // V0.4.1：双色球与大乐透选号已开放；大乐透开奖数据仍只使用官方规则说明，不伪造开奖数据。
    fun setGame(game: LotteryGame) {
        if (game.available) state = state.copy(game = game)
    }

    fun setSoundEnabled(enabled: Boolean) {
        state = state.copy(soundEnabled = enabled)
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
    }

    fun setCompletionSound(sound: FortuneSound) {
        state = state.copy(completionSound = sound)
        prefs.edit().putString("completion_sound", sound.name).apply()
    }

    fun setAnimationEnabled(enabled: Boolean) {
        state = state.copy(animationEnabled = enabled)
        prefs.edit().putBoolean("animation_enabled", enabled).apply()
    }

    fun prepareMachineSelection(): List<Prediction> = PredictionEngine.generate(
        state.count,
        state.draws,
        state.mode
    )

    fun applyMachineSelection(predictions: List<Prediction>) {
        val manual = state.tickets.filter { it.source == TicketSource.MANUAL }
        val generated = predictions.map {
            PickItem(
                id = nextTicketId++,
                prediction = it,
                source = TicketSource.MACHINE,
                selected = true
            )
        }
        state = state.copy(tickets = generated + manual)
    }

    fun addManual(reds: List<Int>, blue: Int) {
        val prediction = Prediction(reds.sorted(), blue)
        state = state.copy(
            tickets = state.tickets + PickItem(
                id = nextTicketId++,
                prediction = prediction,
                source = TicketSource.MANUAL,
                selected = true
            )
        )
    }

    fun toggleTicket(id: Long) {
        state = state.copy(
            tickets = state.tickets.map { if (it.id == id) it.copy(selected = !it.selected) else it }
        )
    }

    fun removeTicket(id: Long) { state = state.copy(tickets = state.tickets.filterNot { it.id == id }) }
    fun selectAll(selected: Boolean) { state = state.copy(tickets = state.tickets.map { it.copy(selected = selected) }) }
    fun selectedTickets(): List<PickItem> = state.tickets.filter { it.selected }
    fun copySelectedText(): String = formatPredictions(selectedTickets().map { it.prediction })
    fun copySingleText(item: PickItem): String = formatPredictions(listOf(item.prediction))

    fun saveSelectedNumbers(): Int {
        val candidates = selectedTickets()
        if (candidates.isEmpty()) return 0
        val existing = state.savedNumbers.map { predictionKey(it.prediction) }.toMutableSet()
        val now = System.currentTimeMillis()
        val additions = mutableListOf<SavedNumber>()
        candidates.forEachIndexed { index, item ->
            if (existing.add(predictionKey(item.prediction))) {
                additions += SavedNumber(
                    id = nextSavedId++,
                    prediction = item.prediction,
                    source = item.source,
                    savedAt = now + index,
                    selected = true
                )
            }
        }
        if (additions.isNotEmpty()) {
            state = state.copy(savedNumbers = state.savedNumbers + additions)
            persistSavedNumbers()
        }
        return additions.size
    }

    fun toggleSavedNumber(id: Long) {
        state = state.copy(savedNumbers = state.savedNumbers.map { if (it.id == id) it.copy(selected = !it.selected) else it })
    }

    fun selectAllSaved(selected: Boolean) {
        state = state.copy(savedNumbers = state.savedNumbers.map { it.copy(selected = selected) })
    }

    fun deleteSavedNumber(id: Long) {
        state = state.copy(savedNumbers = state.savedNumbers.filterNot { it.id == id })
        persistSavedNumbers()
    }

    fun deleteSelectedSaved(): Int {
        val count = state.savedNumbers.count { it.selected }
        if (count > 0) {
            state = state.copy(savedNumbers = state.savedNumbers.filterNot { it.selected })
            persistSavedNumbers()
        }
        return count
    }

    fun copySavedSingleText(item: SavedNumber): String = formatPredictions(listOf(item.prediction))
    fun copySavedSelectedText(): String = formatPredictions(state.savedNumbers.filter { it.selected }.map { it.prediction })
    fun copyAllSavedText(): String = formatPredictions(state.savedNumbers.map { it.prediction })

    fun addManualMany(predictions: List<Prediction>) {
        if (predictions.isEmpty()) return
        val existing = state.tickets.map { predictionKey(it.prediction) }.toMutableSet()
        val additions = predictions.filter { existing.add(predictionKey(it)) }.map {
            PickItem(
                id = nextTicketId++,
                prediction = Prediction(it.reds.sorted(), it.blue),
                source = TicketSource.MANUAL,
                selected = true
            )
        }
        if (additions.isNotEmpty()) state = state.copy(tickets = state.tickets + additions)
    }

    fun prepareDltMachineSelection(): List<DltPrediction> = List(state.count) {
        DltPrediction(
            fronts = (1..35).shuffled().take(5).sorted(),
            backs = (1..12).shuffled().take(2).sorted()
        )
    }

    fun applyDltMachineSelection(predictions: List<DltPrediction>) {
        val manual = state.dltTickets.filter { it.source == TicketSource.MANUAL }
        val generated = predictions.map {
            DltPickItem(nextDltTicketId++, it, TicketSource.MACHINE, true)
        }
        state = state.copy(dltTickets = generated + manual)
    }

    fun addDltManual(prediction: DltPrediction) {
        val key = prediction.fronts.sorted().joinToString(",") + "|" + prediction.backs.sorted().joinToString(",")
        val existing = state.dltTickets.any {
            it.prediction.fronts.sorted().joinToString(",") + "|" + it.prediction.backs.sorted().joinToString(",") == key
        }
        if (!existing) state = state.copy(dltTickets = state.dltTickets + DltPickItem(nextDltTicketId++, prediction, TicketSource.MANUAL, true))
    }

    fun toggleDltTicket(id: Long) {
        state = state.copy(dltTickets = state.dltTickets.map { if (it.id == id) it.copy(selected = !it.selected) else it })
    }
    fun removeDltTicket(id: Long) { state = state.copy(dltTickets = state.dltTickets.filterNot { it.id == id }) }
    fun selectAllDlt(selected: Boolean) { state = state.copy(dltTickets = state.dltTickets.map { it.copy(selected = selected) }) }
    fun copyDltSelectedText(): String = formatDltPredictions(state.dltTickets.filter { it.selected }.map { it.prediction })
    fun copyDltSingleText(item: DltPickItem): String = formatDltPredictions(listOf(item.prediction))

    fun isTreasure(prediction: Prediction): Boolean =
        state.treasureNumbers.any { predictionKey(it.prediction) == predictionKey(prediction) }

    fun addPredictionToTreasure(prediction: Prediction, source: TicketSource): Boolean {
        if (isTreasure(prediction)) return false
        val item = TreasureNumber(
            id = nextTreasureId++,
            prediction = Prediction(prediction.reds.sorted(), prediction.blue),
            source = source,
            addedAt = System.currentTimeMillis(),
            selected = true
        )
        state = state.copy(treasureNumbers = state.treasureNumbers + item)
        persistTreasureNumbers()
        return true
    }

    fun addSavedToTreasure(item: SavedNumber): Boolean = addPredictionToTreasure(item.prediction, item.source)

    fun addSelectedSavedToTreasure(): Int {
        var added = 0
        state.savedNumbers.filter { it.selected }.forEach { if (addSavedToTreasure(it)) added++ }
        return added
    }

    fun toggleTreasure(id: Long) {
        state = state.copy(treasureNumbers = state.treasureNumbers.map { if (it.id == id) it.copy(selected = !it.selected) else it })
    }

    fun toggleTreasurePinned(id: Long) {
        state = state.copy(treasureNumbers = state.treasureNumbers.map { if (it.id == id) it.copy(pinned = !it.pinned) else it })
        persistTreasureNumbers()
    }

    fun selectAllTreasure(selected: Boolean) {
        state = state.copy(treasureNumbers = state.treasureNumbers.map { it.copy(selected = selected) })
    }

    fun deleteTreasure(id: Long) {
        state = state.copy(treasureNumbers = state.treasureNumbers.filterNot { it.id == id })
        persistTreasureNumbers()
    }

    fun deleteSelectedTreasure(): Int {
        val count = state.treasureNumbers.count { it.selected }
        if (count > 0) {
            state = state.copy(treasureNumbers = state.treasureNumbers.filterNot { it.selected })
            persistTreasureNumbers()
        }
        return count
    }

    fun copyTreasureSingleText(item: TreasureNumber): String = formatPredictions(listOf(item.prediction))
    fun copyTreasureSelectedText(): String = formatPredictions(state.treasureNumbers.filter { it.selected }.map { it.prediction })

    private fun predictionKey(prediction: Prediction): String =
        prediction.reds.sorted().joinToString(",") + "|" + prediction.blue

    private fun persistSavedNumbers() {
        val array = JSONArray()
        state.savedNumbers.forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("reds", JSONArray(item.prediction.reds.sorted()))
                put("blue", item.prediction.blue)
                put("source", item.source.name)
                put("savedAt", item.savedAt)
            })
        }
        prefs.edit().putString("saved_numbers_v1", array.toString()).apply()
    }

    private fun loadSavedNumbers(): List<SavedNumber> {
        val raw = prefs.getString("saved_numbers_v1", null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val redsJson = obj.getJSONArray("reds")
                    val reds = buildList { for (j in 0 until redsJson.length()) add(redsJson.getInt(j)) }
                    val blue = obj.getInt("blue")
                    if (reds.size == 6 && reds.distinct().size == 6 && reds.all { it in 1..33 } && blue in 1..16) {
                        add(
                            SavedNumber(
                                id = obj.optLong("id", i.toLong() + 1L),
                                prediction = Prediction(reds.sorted(), blue),
                                source = runCatching { TicketSource.valueOf(obj.optString("source", TicketSource.MACHINE.name)) }.getOrDefault(TicketSource.MACHINE),
                                savedAt = obj.optLong("savedAt", 0L),
                                selected = true
                            )
                        )
                    }
                }
            }
        }.getOrDefault(emptyList())
    }


    private fun persistTreasureNumbers() {
        val array = JSONArray()
        state.treasureNumbers.forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("reds", JSONArray(item.prediction.reds.sorted()))
                put("blue", item.prediction.blue)
                put("source", item.source.name)
                put("addedAt", item.addedAt)
                put("pinned", item.pinned)
            })
        }
        prefs.edit().putString("treasure_numbers_v1", array.toString()).apply()
    }

    private fun loadTreasureNumbers(): List<TreasureNumber> {
        val raw = prefs.getString("treasure_numbers_v1", null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val redsJson = obj.getJSONArray("reds")
                    val reds = buildList { for (j in 0 until redsJson.length()) add(redsJson.getInt(j)) }
                    val blue = obj.getInt("blue")
                    if (reds.size == 6 && reds.distinct().size == 6 && reds.all { it in 1..33 } && blue in 1..16) {
                        add(
                            TreasureNumber(
                                id = obj.optLong("id", i.toLong() + 1L),
                                prediction = Prediction(reds.sorted(), blue),
                                source = runCatching { TicketSource.valueOf(obj.optString("source", TicketSource.MACHINE.name)) }.getOrDefault(TicketSource.MACHINE),
                                addedAt = obj.optLong("addedAt", 0L),
                                selected = true,
                                pinned = obj.optBoolean("pinned", false)
                            )
                        )
                    }
                }
            }
        }.getOrDefault(emptyList())
    }
}

private fun formatPredictions(items: List<Prediction>): String = buildString {
    appendLine("双色球选取号码(${items.size}注)")
    appendLine()
    items.forEachIndexed { index, item -> appendLine(item.compactLine(index + 1)) }
    appendLine()
    append("恭喜发财，好运到来")
}


private fun formatDltPredictions(items: List<DltPrediction>): String = buildString {
    appendLine("大乐透选取号码(${items.size}注)")
    appendLine()
    items.forEachIndexed { index, item -> appendLine(item.compactLine(index + 1)) }
    appendLine()
    append("恭喜发财，好运到来")
}

@Composable
fun App(vm: MainViewModel = viewModel()) {
    var tab by remember { mutableIntStateOf(0) }
    Scaffold(
        containerColor = WarmBackground,
        bottomBar = { FortuneBottomBar(selected = tab, onSelect = { tab = it }) }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (tab) {
                0 -> HomeScreen(vm, onGoSelect = { tab = 1 })
                1 -> SelectionScreen(vm)
                2 -> HistoryScreen(vm)
                else -> MyScreen(vm)
            }
        }
    }
}

@Composable
private fun LuxuryPage(content: @Composable BoxScope.() -> Unit) {
    // V0.3.11_FULL_THEME_BASE：统一为用户确认的金红东方财富主题，而不是局部换色。
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFD96A),
                        Color(0xFFFFF1C3),
                        Color(0xFFFFFBED)
                    )
                )
            )
    ) {
        Image(
            painter = painterResource(R.drawable.caiyunbao_theme_texture),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = 0.18f
        )
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    val gold = Color(0xFFC58715).copy(alpha = 0.18f)
                    val red = Color(0xFFB6241C).copy(alpha = 0.09f)
                    drawLine(gold, Offset(7.dp.toPx(), 0f), Offset(7.dp.toPx(), size.height), 1.dp.toPx())
                    drawLine(gold, Offset(size.width - 7.dp.toPx(), 0f), Offset(size.width - 7.dp.toPx(), size.height), 1.dp.toPx())
                    repeat(5) { i ->
                        drawCircle(red, (22 + i * 7).dp.toPx(), Offset(size.width * (0.08f + i * 0.22f), size.height * 0.82f), style = Stroke(1.dp.toPx()))
                    }
                },
            content = content
        )
    }
}

@Composable
private fun BrandHeader(title: String, subtitle: String? = null) {
    val shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF8F1D16), Color(0xFFB92E23), Color(0xFF8C1B15))))
            .border(1.dp, Color(0xFFE5B841), shape)
            .drawBehind {
                val g = Color(0xFFFFD86B).copy(alpha = 0.18f)
                repeat(7) { i -> drawCircle(g, 18.dp.toPx(), Offset(size.width * (0.04f + i * 0.16f), size.height * 0.18f), style = Stroke(1.dp.toPx())) }
            }
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFE19A))
                Spacer(Modifier.weight(1f))
                Surface(color = Color(0xFFFDE7A8), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Color(0xFFC88816))) {
                    Text(APP_VERSION, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), fontSize = 11.sp, color = DeepRed, fontWeight = FontWeight.Black)
                }
            }
            subtitle?.let {
                Spacer(Modifier.height(2.dp))
                Text(it, color = Color(0xFFFFE9B6), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun CompactSelectionHeader() {
    val shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFFA22219), Color(0xFFC43A27), Color(0xFF8C1B15))))
            .border(1.dp, Color(0xFFE3B43A), shape)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("智能选号", fontSize = 23.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFE29C))
                Text("双色球 · 瑞兽选号机 · 自选混合复制", color = Color(0xFFFFE8B2), fontSize = 10.5.sp)
            }
            Surface(color = Color(0xFFFCE7A8), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Color(0xFFC78615))) {
                Text(APP_VERSION, Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 10.5.sp, color = DeepRed, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun FortuneBottomBar(selected: Int, onSelect: (Int) -> Unit) {
    val labels = listOf("首页", "智能选号", "开奖数据", "我的")
    val icons = listOf("⌂", "", "≡", "◎")
    Surface(color = Color(0xFF8D1B16), shadowElevation = 12.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(72.dp)
                .drawBehind {
                    drawLine(Color(0xFFFFD260), Offset(0f, 1.dp.toPx()), Offset(size.width, 1.dp.toPx()), 1.5.dp.toPx())
                    drawLine(Color(0xFF6C120F), Offset(0f, 4.dp.toPx()), Offset(size.width, 4.dp.toPx()), 1.dp.toPx())
                }
                .padding(horizontal = 5.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            labels.forEachIndexed { index, label ->
                val active = selected == index
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().clickable { onSelect(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .width(if (active) 60.dp else 44.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (active) Brush.horizontalGradient(listOf(Color(0xFFFFE897), Color(0xFFC68A17))) else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent)))
                            .border(if (active) 1.dp else 0.dp, if (active) Color(0xFFFFD76A) else Color.Transparent, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index == 1) CoinPickIcon(active)
                        else Text(icons[index], fontSize = 21.sp, color = if (active) DeepRed else Color(0xFFFFE6AE), fontWeight = FontWeight.Black)
                    }
                    Text(label, fontSize = 11.5.sp, color = if (active) Color(0xFFFFE7A8) else Color(0xFFF8D998), fontWeight = if (active) FontWeight.Black else FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun CoinPickIcon(active: Boolean) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(
                if (active) Brush.radialGradient(listOf(Color(0xFFFFF0A8), Color(0xFFD3A22E)))
                else Brush.radialGradient(listOf(Color(0xFFFFF8DF), Color(0xFFE7D199)))
            )
            .border(1.dp, if (active) Color(0xFFB67D12) else Color(0xFFD7C79D), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(11.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (active) DeepRed else Color(0xFF8C8069))
        )
        if (active) {
            Text("✦", color = Color(0xFFFFF3C7), fontSize = 8.sp, modifier = Modifier.align(Alignment.TopEnd).padding(2.dp))
        }
    }
}

@Composable
private fun MetalBrandTitle(modifier: Modifier = Modifier) {
    // V0.3.6：使用真正的金属浮雕品牌图层。字面内部已带祥云雕刻纹，而非普通纯色 Text。
    Image(
        painter = painterResource(R.drawable.caiyunbao_brand_title),
        contentDescription = "彩运宝鎏金祥云雕刻品牌字",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun HeroCover() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1122f / 1402f),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        border = BorderStroke(1.5.dp, Color(0xFFD39A1F)),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            // V0.3.11：首页直接使用用户确认通过的完整「财运亨通 / 彩运宝」主题海报，不再重复叠加品牌文字。
            Image(
                painter = painterResource(R.drawable.caiyunbao_home_cover),
                contentDescription = "彩运宝财运亨通完整首页主题封面",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                color = Color(0xEFFFF0BE),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color(0xFFC78B1B))
            ) {
                Text(APP_VERSION, Modifier.padding(horizontal = 11.dp, vertical = 6.dp), color = DeepRed, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun HomeScreen(vm: MainViewModel, onGoSelect: () -> Unit) {
    // V0.4.0_MULTI_GAME_HOME：在不影响双色球稳定链路的前提下加入彩种中心入口。
    val s = vm.state
    val context = LocalContext.current
    var showGameCenter by remember { mutableStateOf(false) }
    LuxuryPage {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 18.dp)
        ) {
            item { HeroCover() }
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    GoldCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("当前彩种", color = Muted, fontSize = 12.sp)
                                Text(s.game.title, fontWeight = FontWeight.Black, fontSize = 22.sp, color = DeepRed)
                            }
                            Surface(
                                modifier = Modifier.clickable { showGameCenter = true },
                                color = Color(0xFFFFF0C5),
                                shape = RoundedCornerShape(22.dp),
                                border = BorderStroke(1.dp, Color(0xFFE0BC61))
                            ) {
                                Text("彩种中心 · 6种 ›", Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 11.sp, color = DeepGold, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(s.game.subtitle, color = Muted, fontSize = 13.sp)
                    }
                }
            }
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    GoldCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("最新开奖", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF2E2920))
                            Spacer(Modifier.weight(1f))
                            if (s.loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = LuckyRed)
                        }
                        if (s.game == LotteryGame.SSQ) {
                            val latest = s.draws.firstOrNull()
                            if (latest == null) {
                                Text("暂无已验证开奖数据，联网后会自动更新。", color = Muted)
                            } else {
                                Text("第 ${latest.issue} 期  ${latest.date}", color = Muted, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                                BallRow(latest.reds, latest.blue)
                                Text("数据源：中国福彩网公开开奖信息", fontSize = 12.sp, color = Muted)
                                Surface(color = if (s.error == null) Color(0xFFEAF4E4) else Color(0xFFFFF0D0), shape = RoundedCornerShape(8.dp)) {
                                    Text(if (s.error == null) "数据状态：在线/最新缓存可用" else "数据状态：当前使用本地已验证缓存", Modifier.padding(horizontal = 7.dp, vertical = 4.dp), color = if (s.error == null) Color(0xFF4D7744) else DeepGold, fontSize = 9.5.sp)
                                }
                            }
                        } else {
                            Text("超级大乐透选号已开放", color = DeepRed, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("前区 01–35 选5个｜后区 01–12 选2个", color = DeepGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Surface(color = Color(0xFFFFF0D0), shape = RoundedCornerShape(8.dp)) {
                                Text("开奖数据：正在接入中国体彩网公开开奖数据源，当前不展示未经核验的开奖信息", Modifier.padding(8.dp), color = Color(0xFF755514), fontSize = 10.sp)
                            }
                        }
                        s.error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp) }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("最近同步：${formatTime(s.lastUpdated)}", fontSize = 12.sp, color = Muted)
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = vm::refresh, enabled = !s.loading) { Text("更新", color = DeepGold, fontWeight = FontWeight.Black) }
                        }
                    }
                }
            }
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable(onClick = onGoSelect),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.5.dp, Color(0xFFD29216)),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                    ) {
                        Box(
                            Modifier.fillMaxWidth().background(
                                Brush.horizontalGradient(listOf(Color(0xFFFFE9A5), Color(0xFFFFF5D6), Color(0xFFFFD96C)))
                            )
                        ) {
                            Image(
                                painter = painterResource(R.drawable.caiyunbao_beast_left),
                                contentDescription = "招财瑞兽",
                                modifier = Modifier.align(Alignment.CenterStart).offset(x = (-14).dp, y = 10.dp).size(92.dp),
                                contentScale = ContentScale.Fit
                            )
                            Row(Modifier.padding(start = 78.dp, end = 12.dp, top = 14.dp, bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("彩运宝智能选号机", fontWeight = FontWeight.Black, fontSize = 19.sp, color = DeepRed)
                                    Text("瑞兽守财 · 滚动选号 · 自选号码", color = Color(0xFF7C5215), fontSize = 12.sp)
                                }
                                Button(
                                    onClick = onGoSelect,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB5261D), contentColor = Color(0xFFFFE59A)),
                                    border = BorderStroke(1.3.dp, Color(0xFFFFD25C)),
                                    shape = RoundedCornerShape(19.dp)
                                ) { Text("去选号", fontWeight = FontWeight.Black) }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showGameCenter) {
        LotteryCenterDialog(
            current = s.game,
            onDismiss = { showGameCenter = false },
            onSelect = { game ->
                if (game.available) {
                    vm.setGame(game)
                    showGameCenter = false
                    Toast.makeText(context, "已切换到${game.title}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "${game.title}正在接入真实开奖数据与选号规则，暂未开放", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
private fun LotteryCenterDialog(
    current: LotteryGame,
    onDismiss: () -> Unit,
    onSelect: (LotteryGame) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(max = 650.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E6)),
            border = BorderStroke(1.7.dp, Color(0xFFD39A26)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("彩种中心", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DeepRed)
                        Text("V0.4.1 大乐透选号第一阶段", color = DeepGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Surface(
                        modifier = Modifier.clickable(onClick = onDismiss),
                        color = Color(0xFFFFE8A5),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, Color(0xFFD2A13B))
                    ) { Text("×", Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = DeepRed, fontSize = 18.sp, fontWeight = FontWeight.Black) }
                }

                Surface(color = Color(0xFFFFEFC0), shape = RoundedCornerShape(12.dp)) {
                    Text(
                        "双色球保持完整功能；大乐透已开放智能选号、手动选号与复制。大乐透公开开奖数据正在单独接入和核验，当前不会展示未经核验的开奖结果。",
                        Modifier.padding(10.dp),
                        color = Color(0xFF755514),
                        fontSize = 10.5.sp,
                        lineHeight = 15.sp
                    )
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 470.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(LotteryGame.entries.toList(), key = { it.name }) { game ->
                        val active = game == current
                        val open = game.available
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(game) },
                            colors = CardDefaults.cardColors(containerColor = if (active) Color(0xFFFFEAB0) else Color(0xFFFFFDF6)),
                            border = BorderStroke(if (active) 1.6.dp else 1.dp, if (active) Color(0xFFC78317) else Color(0xFFE3D2A3)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (open) Color(0xFFA8241D) else Color(0xFFE9DFC4),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            if (open) "已开放" else "开发中",
                                            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = if (open) Color(0xFFFFE9A2) else Color(0xFF796F5C),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(game.title, fontSize = 17.sp, fontWeight = FontWeight.Black, color = if (open) DeepRed else Ink)
                                    Spacer(Modifier.weight(1f))
                                    Text(game.category, color = Muted, fontSize = 10.sp)
                                }
                                Text(game.subtitle, color = DeepGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(game.ruleText, color = Muted, fontSize = 10.5.sp)
                                if (active) Text("当前使用", color = Color(0xFF4E7B3E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoldCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = shape,
        border = BorderStroke(1.4.dp, Color(0xFFD59A20)),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            Modifier
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF8E5), Color(0xFFFFE7A6), Color(0xFFFFF5DB))))
                .drawBehind {
                    drawLine(Color(0xFFAA2C22).copy(alpha = 0.65f), Offset(16.dp.toPx(), 5.dp.toPx()), Offset(size.width - 16.dp.toPx(), 5.dp.toPx()), 1.dp.toPx())
                    drawLine(Color(0xFFFFD45E).copy(alpha = 0.90f), Offset(28.dp.toPx(), 8.dp.toPx()), Offset(size.width - 28.dp.toPx(), 8.dp.toPx()), 1.dp.toPx())
                }
                .padding(horizontal = 16.dp, vertical = 17.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionScreen(vm: MainViewModel) {
    if (vm.state.game == LotteryGame.DLT) {
        DltSelectionScreen(vm)
        return
    }
    // V0.3.9_REFERENCE_SELECTION_SCREEN: 参考主题整页落地，浅金标题、瑞兽选号机、白金控件与号码卡片。
    val s = vm.state
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var countText by remember(s.count) { mutableStateOf(s.count.toString()) }
    var showManual by remember { mutableStateOf(false) }
    var rolling by remember { mutableStateOf(false) }
    var staged by remember { mutableStateOf<List<Prediction>>(emptyList()) }
    var rollingReds by remember { mutableStateOf(listOf(1, 6, 11, 18, 25, 31)) }
    var rollingBlue by remember { mutableIntStateOf(8) }
    var lockedCount by remember { mutableIntStateOf(0) }

    val rollPlayer = remember {
        MediaPlayer.create(context, R.raw.number_roll)?.apply { isLooping = true; setVolume(0.42f, 0.42f) }
    }
    val requestedCompletionSoundRes = fortuneSoundRes(context, s.completionSound)
    val completionSoundRes = if (requestedCompletionSoundRes != 0) requestedCompletionSoundRes else R.raw.fortune_coin_rain
    val coinPlayer = remember(completionSoundRes) {
        MediaPlayer.create(context, completionSoundRes)?.apply { isLooping = false; setVolume(0.96f, 0.96f) }
    }
    DisposableEffect(rollPlayer, coinPlayer) {
        onDispose { runCatching { rollPlayer?.release() }; runCatching { coinPlayer?.release() } }
    }

    LuxuryPage {
        Column(Modifier.fillMaxSize()) {
            CompactSelectionHeader()
            Column(Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
                SelectionMachine(rolling, staged.firstOrNull(), rollingReds, rollingBlue, lockedCount)
                Spacer(Modifier.height(7.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.weight(1.18f)) {
                        Box(
                            Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(14.dp))
                                .background(Brush.verticalGradient(listOf(Color(0xFFFFF9E8), Color(0xFFFFE9A8))))
                                .border(1.5.dp, Color(0xFFD3971B), RoundedCornerShape(14.dp)).clickable { expanded = true }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("选号方式", fontSize = 9.5.sp, color = Muted)
                                    Text(s.mode.label, fontSize = 15.5.sp, color = Ink, fontWeight = FontWeight.Black)
                                }
                                Text("⌄", color = DeepGold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            PredictMode.entries.forEach { mode -> DropdownMenuItem(text = { Text(mode.label) }, onClick = { vm.setMode(mode); expanded = false }) }
                        }
                    }
                    Box(
                        Modifier.weight(0.82f).height(52.dp).clip(RoundedCornerShape(14.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFFFFF9E8), Color(0xFFFFE9A8))))
                            .border(1.5.dp, Color(0xFFD3971B), RoundedCornerShape(14.dp)).padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("注数", fontSize = 9.5.sp, color = Muted)
                                BasicTextField(
                                    value = countText,
                                    onValueChange = { raw -> countText = raw.filter(Char::isDigit).take(3); countText.toIntOrNull()?.let(vm::setCount) },
                                    singleLine = true,
                                    textStyle = TextStyle(fontSize = 15.5.sp, fontWeight = FontWeight.Black, color = Ink),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                            Text("注", color = DeepGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                Spacer(Modifier.height(7.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        enabled = !rolling,
                        onClick = {
                            val prepared = vm.prepareMachineSelection()
                            if (prepared.isNotEmpty()) {
                                staged = prepared
                                if (!s.animationEnabled) {
                                    lockedCount = 7; rollingReds = prepared.first().reds; rollingBlue = prepared.first().blue; vm.applyMachineSelection(prepared)
                                    if (s.soundEnabled) safePlay(coinPlayer)
                                } else {
                                    rolling = true; lockedCount = 0; if (s.soundEnabled) safePlay(rollPlayer)
                                    scope.launch {
                                        repeat(46) { tick ->
                                            val final = prepared.first()
                                            rollingReds = (0 until 6).map { index -> if (index < lockedCount) final.reds[index] else Random.nextInt(1, 34) }
                                            rollingBlue = if (lockedCount >= 7) final.blue else Random.nextInt(1, 17)
                                            lockedCount = when { tick >= 42 -> 7; tick >= 38 -> 6; tick >= 34 -> 5; tick >= 30 -> 4; tick >= 26 -> 3; tick >= 22 -> 2; tick >= 17 -> 1; else -> 0 }
                                            delay(58)
                                        }
                                        lockedCount = 7
                                        runCatching { rollPlayer?.pause(); rollPlayer?.seekTo(0) }
                                        vm.applyMachineSelection(prepared)
                                        if (s.soundEnabled) safePlay(coinPlayer)
                                        delay(220); rolling = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB9271E), contentColor = Color(0xFFFFE9A1)),
                        border = BorderStroke(1.5.dp, Color(0xFFFFD157)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) { Text(if (rolling) "正在选号…" else "开始选号", fontWeight = FontWeight.Black) }
                    OutlinedButton(
                        onClick = { showManual = true }, modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepGold),
                        border = BorderStroke(1.5.dp, Color(0xFFD09118)),
                        shape = RoundedCornerShape(18.dp)
                    ) { Text("手动添加", fontWeight = FontWeight.Black) }
                }

                Spacer(Modifier.height(8.dp))
                val selectedCount = s.tickets.count { it.selected }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("号码列表", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF2D2923))
                    Spacer(Modifier.width(8.dp))
                    Text("共 ${s.tickets.size} 注 · 已选 $selectedCount 注", color = Muted, fontSize = 11.5.sp)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { vm.selectAll(selectedCount != s.tickets.size) }) {
                        Text(if (selectedCount == s.tickets.size && s.tickets.isNotEmpty()) "取消全选" else "全选", color = DeepGold, fontWeight = FontWeight.Bold)
                    }
                }

                if (s.tickets.isEmpty()) {
                    GoldCard { Text("点击“开始选号”观看瑞兽选号机滚动，也可以用固定分格输入自己的号码。", color = Muted, fontWeight = FontWeight.Medium) }
                    Spacer(Modifier.height(8.dp))
                }

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    itemsIndexed(s.tickets, key = { _, item -> item.id }) { index, item ->
                        TicketCard(index + 1, item, { vm.toggleTicket(item.id) }, { vm.removeTicket(item.id) }, { copyToClipboard(context, "双色球选取号码", vm.copySingleText(item)) })
                    }
                    item { Spacer(Modifier.height(6.dp)) }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        enabled = selectedCount > 0,
                        onClick = {
                            val added = vm.saveSelectedNumbers()
                            val message = when { added > 0 -> "已保存 $added 注到我的号码"; selectedCount > 0 -> "所选号码已经保存过了"; else -> "请先选择号码" }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB9271E), contentColor = Color(0xFFFFE59A)),
                        border = BorderStroke(1.4.dp, Color(0xFFFFD25D)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) { Text("保存号码", fontWeight = FontWeight.Black) }
                    OutlinedButton(
                        enabled = selectedCount > 0,
                        onClick = { copyToClipboard(context, "双色球选取号码", vm.copySelectedText()) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepGold),
                        border = BorderStroke(1.5.dp, Color(0xFFD09118)),
                        shape = RoundedCornerShape(18.dp)
                    ) { Text("复制号码", fontWeight = FontWeight.Black) }
                }
                Text("选号用于随机与历史数据参考，不代表未来开奖结果。", Modifier.fillMaxWidth().padding(top = 4.dp), color = Muted, fontSize = 10.5.sp, textAlign = TextAlign.Center)
            }
        }
    }

    if (showManual) {
        ManualEntryDialog(
            onDismiss = { showManual = false },
            treasureNumbers = s.treasureNumbers,
            onAddMany = { predictions ->
                vm.addManualMany(predictions)
                showManual = false
                Toast.makeText(context, "已加入 ${predictions.size} 注到号码列表", Toast.LENGTH_SHORT).show()
            }
        )
    }
}


@Composable
private fun DltSelectionScreen(vm: MainViewModel) {
    val s = vm.state
    val context = LocalContext.current
    var showManual by remember { mutableStateOf(false) }
    var preview by remember { mutableStateOf<DltPrediction?>(null) }
    LuxuryPage {
        Column(Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF8C2019),
                border = BorderStroke(1.dp, Color(0xFFE0B13E)),
                shape = RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp)
            ) {
                Column(Modifier.padding(horizontal = 18.dp, vertical = 13.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("智能选号 · 大乐透", color = Color(0xFFFFE7A1), fontSize = 23.sp, fontWeight = FontWeight.Black)
                            Text("前区 01–35 选5个 · 后区 01–12 选2个", color = Color(0xFFFFF4D4), fontSize = 11.sp)
                        }
                        Surface(color = Color(0xFFFFE8A5), shape = RoundedCornerShape(18.dp)) {
                            Text(APP_VERSION, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = DeepRed, fontWeight = FontWeight.Black, fontSize = 10.sp)
                        }
                    }
                }
            }
            Column(Modifier.padding(12.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CF)),
                    border = BorderStroke(1.5.dp, Color(0xFFD39A26)),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("彩运宝 · 大乐透选号机", color = DeepRed, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text("规则参考：5个前区 + 2个后区", color = DeepGold, fontSize = 11.sp)
                        Spacer(Modifier.height(10.dp))
                        val p = preview ?: DltPrediction(listOf(3, 8, 12, 25, 31), listOf(4, 9))
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                            p.fronts.forEach { MachineBall(it, BallRed, false) }
                            Spacer(Modifier.width(4.dp))
                            p.backs.forEach { MachineBall(it, BallBlue, false) }
                        }
                    }
                }
                Spacer(Modifier.height(9.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val prepared = vm.prepareDltMachineSelection()
                            preview = prepared.firstOrNull()
                            vm.applyDltMachineSelection(prepared)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB9271E), contentColor = Color(0xFFFFE9A1)),
                        shape = RoundedCornerShape(18.dp)
                    ) { Text("开始选号", fontWeight = FontWeight.Black) }
                    OutlinedButton(
                        onClick = { showManual = true },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.5.dp, Color(0xFFD09118)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepGold),
                        shape = RoundedCornerShape(18.dp)
                    ) { Text("手动添加", fontWeight = FontWeight.Black) }
                }
                Spacer(Modifier.height(8.dp))
                val selectedCount = s.dltTickets.count { it.selected }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("大乐透号码列表", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("共 ${s.dltTickets.size} 注 · 已选 $selectedCount 注", color = Muted, fontSize = 11.sp)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { vm.selectAllDlt(selectedCount != s.dltTickets.size) }) { Text(if (selectedCount == s.dltTickets.size && s.dltTickets.isNotEmpty()) "取消全选" else "全选", color = DeepGold) }
                }
                if (s.dltTickets.isEmpty()) {
                    GoldCard { Text("点击“开始选号”生成大乐透号码，或手动选择5个前区和2个后区号码。", color = Muted) }
                }
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    itemsIndexed(s.dltTickets, key = { _, item -> item.id }) { index, item ->
                        DltTicketCard(index + 1, item, { vm.toggleDltTicket(item.id) }, { vm.removeDltTicket(item.id) }, { copyToClipboard(context, "大乐透选取号码", vm.copyDltSingleText(item)) })
                    }
                }
                Button(
                    enabled = selectedCount > 0,
                    onClick = { copyToClipboard(context, "大乐透选取号码", vm.copyDltSelectedText()) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AA3A), contentColor = Ink),
                    shape = RoundedCornerShape(18.dp)
                ) { Text("复制已选号码（${selectedCount}注）", fontWeight = FontWeight.Black) }
                Text("大乐透开奖数据将在公开数据源核验完成后开放；当前选号仅用于随机参考。", Modifier.fillMaxWidth().padding(top = 5.dp), color = Muted, fontSize = 10.sp, textAlign = TextAlign.Center)
            }
        }
    }
    if (showManual) {
        DltManualDialog(onDismiss = { showManual = false }, onAdd = { vm.addDltManual(it); showManual = false; Toast.makeText(context, "已加入1注大乐透号码", Toast.LENGTH_SHORT).show() })
    }
}

@Composable
private fun DltTicketCard(index: Int, item: DltPickItem, onToggle: () -> Unit, onDelete: () -> Unit, onCopy: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E9)), border = BorderStroke(1.2.dp, Color(0xFFE0C77F)), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = item.selected, onCheckedChange = { onToggle() })
                Text("第${index}注", fontWeight = FontWeight.Black, color = DeepGold)
                Spacer(Modifier.width(7.dp))
                Text(item.source.label, color = if (item.source == TicketSource.MANUAL) BallBlue else LuckyRed, fontSize = 10.sp)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onCopy) { Text("复制", color = DeepGold) }
                TextButton(onClick = onDelete) { Text("删除", color = LuckyRed) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                item.prediction.fronts.forEach { MachineBall(it, BallRed, false) }
                Spacer(Modifier.width(3.dp))
                item.prediction.backs.forEach { MachineBall(it, BallBlue, false) }
            }
        }
    }
}

@Composable
private fun DltManualDialog(onDismiss: () -> Unit, onAdd: (DltPrediction) -> Unit) {
    var fronts by remember { mutableStateOf(setOf<Int>()) }
    var backs by remember { mutableStateOf(setOf<Int>()) }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.fillMaxWidth(0.94f).heightIn(max = 720.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E6)),
            border = BorderStroke(1.5.dp, Color(0xFFD39A26)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("手动添加大乐透号码", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DeepRed)
                        Text("选择5个前区号码和2个后区号码", color = Muted, fontSize = 11.sp)
                    }
                    TextButton(onClick = onDismiss) { Text("关闭", color = DeepGold) }
                }
                Text("前区 01–35（${fronts.size}/5）", color = DeepRed, fontWeight = FontWeight.Black)
                NumberChoiceGrid(35, fronts, 5, BallRed) { n -> fronts = if (n in fronts) fronts - n else if (fronts.size < 5) fronts + n else fronts }
                Text("后区 01–12（${backs.size}/2）", color = BallBlue, fontWeight = FontWeight.Black)
                NumberChoiceGrid(12, backs, 2, BallBlue) { n -> backs = if (n in backs) backs - n else if (backs.size < 2) backs + n else backs }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("取消") }
                    Button(enabled = fronts.size == 5 && backs.size == 2, onClick = { onAdd(DltPrediction(fronts.sorted(), backs.sorted())) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = LuckyRed)) { Text("加入列表") }
                }
            }
        }
    }
}

@Composable
private fun NumberChoiceGrid(max: Int, selected: Set<Int>, limit: Int, color: Color, onToggle: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (start in 1..max step 7) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                for (n in start..minOf(start + 6, max)) {
                    val active = n in selected
                    Box(
                        Modifier.weight(1f).aspectRatio(1f).clip(CircleShape)
                            .background(if (active) color else Color(0xFFFFFDF5))
                            .border(1.dp, if (active) color else Color(0xFFE2C98A), CircleShape)
                            .clickable { onToggle(n) },
                        contentAlignment = Alignment.Center
                    ) { Text("%02d".format(n), color = if (active) Color.White else Ink, fontWeight = if (active) FontWeight.Black else FontWeight.Medium, fontSize = 11.sp) }
                }
                repeat(7 - minOf(7, max - start + 1)) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun SelectionMachine(
    rolling: Boolean,
    preview: Prediction?,
    rollingReds: List<Int>,
    rollingBlue: Int,
    lockedCount: Int
) {
    // V0.3.11_APPROVED_SELECTION_MACHINE：使用独立透明瑞兽图，左右夹护牌匾并与号码框上角轻微重叠。
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.8.dp, Color(0xFFD38E13)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(205.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFFFFE99B), Color(0xFFFFF2C2), Color(0xFFF2C95F))))
        ) {
            Image(
                painter = painterResource(R.drawable.caiyunbao_beast_left),
                contentDescription = "左侧招财瑞兽",
                modifier = Modifier.align(Alignment.TopStart).offset(x = (-12).dp, y = 2.dp).size(118.dp),
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(R.drawable.caiyunbao_beast_right),
                contentDescription = "右侧招财瑞兽",
                modifier = Modifier.align(Alignment.TopEnd).offset(x = 12.dp, y = 2.dp).size(118.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color(0xFF9A1F18),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(2.dp, Color(0xFFFFD15A)),
                    shadowElevation = 5.dp
                ) {
                    Column(Modifier.padding(horizontal = 28.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("彩运宝选号机", fontWeight = FontWeight.Black, color = Color(0xFFFFE58E), fontSize = 20.sp)
                        Text("瑞兽守财 · 福运启号", color = Color(0xFFFFF1C8), fontSize = 10.5.sp)
                    }
                }
            }

            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 10.dp, end = 10.dp, bottom = 28.dp)
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF64100E), Color(0xFFB52920), Color(0xFF64100E))))
                    .border(2.dp, Color(0xFFFFD059), RoundedCornerShape(18.dp))
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    repeat(6) { index ->
                        val n = when {
                            rolling && preview != null && index < lockedCount -> preview.reds[index]
                            rolling -> rollingReds.getOrElse(index) { Random.nextInt(1, 34) }
                            preview != null -> preview.reds[index]
                            else -> listOf(3, 8, 12, 19, 25, 31)[index]
                        }
                        MachineBall(n, BallRed, rolling && index >= lockedCount)
                    }
                    Spacer(Modifier.width(2.dp))
                    val blue = when {
                        rolling && preview != null && lockedCount >= 7 -> preview.blue
                        rolling -> rollingBlue
                        preview != null -> preview.blue
                        else -> 9
                    }
                    MachineBall(blue, BallBlue, rolling && lockedCount < 7)
                }
            }

            Text(
                if (rolling) "福运号码正在滚动…" else "点击开始选号，第一注将通过滚动窗口揭晓",
                color = Color(0xFF7B4E13),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun BeastMedallion(mirrored: Boolean, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(if (mirrored) R.drawable.caiyunbao_beast_right else R.drawable.caiyunbao_beast_left),
        contentDescription = "招财瑞兽",
        modifier = modifier.size(92.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun MachineBall(number: Int, color: Color, spinning: Boolean) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(BrightGold, AntiqueGold)))
            .padding(2.dp)
            .clip(CircleShape)
            .background(if (spinning) color.copy(alpha = 0.78f) else color)
            .border(1.dp, Color.White.copy(alpha = 0.72f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("%02d".format(number), color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
    }
}

@Composable
private fun TicketCard(
    index: Int,
    item: PickItem,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
    onCopy: () -> Unit
) {
    val shape = RoundedCornerShape(17.dp)
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.4.dp, Color(0xFFD29A24)),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            Modifier
                .background(Brush.horizontalGradient(listOf(Color(0xFFFFFAEA), Color(0xFFFFEAB0), Color(0xFFFFF9E7))))
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = item.selected, onCheckedChange = { onToggle() }, modifier = Modifier.size(34.dp))
                Surface(color = Color(0xFFFFD965), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFC68B17))) {
                    Text("第${index}注", Modifier.padding(horizontal = 7.dp, vertical = 4.dp), color = DeepRed, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(6.dp))
                Surface(color = if (item.source == TicketSource.MANUAL) Color(0xFFE5F0FF) else Color(0xFFFFDDD6), shape = RoundedCornerShape(8.dp)) {
                    Text(item.source.label, Modifier.padding(horizontal = 7.dp, vertical = 4.dp), color = if (item.source == TicketSource.MANUAL) BallBlue else LuckyRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 9.dp),
                    border = BorderStroke(1.dp, Color(0xFFD09B2E)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepRed),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("复制号码", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = onRemove, contentPadding = PaddingValues(horizontal = 4.dp)) { Text("删除", color = Color(0xFF8A4A42), fontWeight = FontWeight.Bold, fontSize = 10.sp) }
            }
            Row(Modifier.padding(start = 4.dp, top = 3.dp, bottom = 1.dp), verticalAlignment = Alignment.CenterVertically) {
                BallRow(item.prediction.reds, item.prediction.blue, compact = true)
            }
        }
    }
}

@Composable
private fun ManualEntryDialog(
    onDismiss: () -> Unit,
    treasureNumbers: List<TreasureNumber>,
    onAddMany: (List<Prediction>) -> Unit
) {
    val context = LocalContext.current
    val reds = remember { mutableStateListOf("", "", "", "", "", "") }
    var blue by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var showTreasurePicker by remember { mutableStateOf(false) }
    val pending = remember { mutableStateListOf<Prediction>() }
    val focusers = remember { List(7) { FocusRequester() } }
    val scrollState = rememberScrollState()

    fun predictionKeyLocal(prediction: Prediction): String =
        prediction.reds.sorted().joinToString(",") + "|" + prediction.blue

    fun clearCells() {
        for (i in 0..5) reds[i] = ""
        blue = ""
        error = null
    }

    fun formatCell(value: String): String {
        val n = value.toIntOrNull() ?: return value
        return "%02d".format(n)
    }

    fun applyPasted(text: String) {
        val parsed = parsePastedTicket(text)
        if (parsed.first != null) {
            error = parsed.first
            return
        }
        parsed.second!!.forEachIndexed { index, n -> reds[index] = "%02d".format(n) }
        blue = "%02d".format(parsed.third!!)
        error = null
    }

    fun appendPending(predictions: List<Prediction>): Int {
        val existing = pending.map(::predictionKeyLocal).toMutableSet()
        var added = 0
        predictions.forEach { p ->
            val normalized = Prediction(p.reds.sorted(), p.blue)
            if (existing.add(predictionKeyLocal(normalized))) {
                pending.add(normalized)
                added++
            }
        }
        return added
    }

    fun addCurrentToPending(): Boolean {
        val parsed = parseManualCells(reds.toList(), blue)
        if (parsed.first != null) {
            error = parsed.first
            return false
        }
        val added = appendPending(listOf(Prediction(parsed.second!!, parsed.third!!)))
        if (added == 0) {
            error = "这一注已经在待加入列表中"
            return false
        }
        clearCells()
        return true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 650.dp),
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6D9)),
                border = BorderStroke(1.6.dp, Color(0xFFD08D16)),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Box(
                        Modifier
                            .width(42.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFD9C38B))
                            .align(Alignment.CenterHorizontally)
                    )
                    Column {
                        Text("手动添加双色球号码", fontWeight = FontWeight.Black, fontSize = 19.sp, color = DeepRed)
                        Text("支持连续添加多注，也可从彩宝库直接取用常用号码", color = Muted, fontSize = 11.sp)
                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                if (treasureNumbers.isEmpty()) {
                                    Toast.makeText(context, "彩宝库还没有收藏号码", Toast.LENGTH_SHORT).show()
                                } else showTreasurePicker = true
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Gold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepGold),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp)
                        ) { Text("从彩宝库选取", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) }
                        OutlinedButton(
                            onClick = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val text = cm.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(context)?.toString()
                                if (text.isNullOrBlank()) error = "剪贴板里没有可用号码" else applyPasted(text)
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Gold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepGold),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp)
                        ) { Text("粘贴整注", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) }
                    }

                    Text("红球 01–33", color = DeepGold, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(6) { index ->
                            NumberCell(
                                value = reds[index],
                                color = BallRed,
                                modifier = Modifier.weight(1f),
                                focusRequester = focusers[index],
                                imeAction = ImeAction.Next,
                                onValueChange = { raw ->
                                    if (raw.any { !it.isDigit() } || raw.length > 2) {
                                        applyPasted(raw)
                                    } else {
                                        reds[index] = raw.filter(Char::isDigit).take(2)
                                        error = null
                                        if (reds[index].length == 2) {
                                            if (index < 5) focusers[index + 1].requestFocus() else focusers[6].requestFocus()
                                        }
                                    }
                                },
                                onFocusLost = { if (reds[index].length == 1) reds[index] = formatCell(reds[index]) },
                                onNext = {
                                    if (reds[index].length == 1) reds[index] = formatCell(reds[index])
                                    if (index < 5) focusers[index + 1].requestFocus() else focusers[6].requestFocus()
                                }
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("蓝球 01–16", color = DeepGold, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            Spacer(Modifier.height(4.dp))
                            NumberCell(
                                value = blue,
                                color = BallBlue,
                                focusRequester = focusers[6],
                                imeAction = ImeAction.Done,
                                onValueChange = { raw ->
                                    if (raw.any { !it.isDigit() } || raw.length > 2) applyPasted(raw)
                                    else { blue = raw.filter(Char::isDigit).take(2); error = null }
                                },
                                onFocusLost = { if (blue.length == 1) blue = formatCell(blue) },
                                onNext = { if (blue.length == 1) blue = formatCell(blue) }
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = { addCurrentToPending() },
                            modifier = Modifier.height(42.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB72D27), contentColor = Color.White),
                            shape = RoundedCornerShape(20.dp)
                        ) { Text("添加本注", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    }

                    error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 11.5.sp) }

                    if (pending.isNotEmpty()) {
                        Surface(
                            color = Color(0xFFFFFDF7),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2CF94))
                        ) {
                            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("待加入号码 ${pending.size} 注", fontWeight = FontWeight.Black, color = DeepRed, fontSize = 13.sp)
                                pending.forEachIndexed { index, prediction ->
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("第${index + 1}注", color = DeepGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                            Spacer(Modifier.weight(1f))
                                            TextButton(
                                                onClick = { pending.removeAt(index) },
                                                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)
                                            ) { Text("移除", color = LuckyRed, fontSize = 10.sp) }
                                        }
                                        // 号码独占一整行，避免右侧“移除”按钮挤压蓝球造成裁切。
                                        Box(Modifier.fillMaxWidth()) {
                                            BallRow(prediction.reds, prediction.blue, compact = true)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(44.dp)) { Text("取消") }
                        Button(
                            onClick = {
                                val hasCurrent = reds.any { it.isNotBlank() } || blue.isNotBlank()
                                if (hasCurrent && !addCurrentToPending()) return@Button
                                if (pending.isEmpty()) {
                                    error = "请先添加至少1注号码"
                                } else {
                                    onAddMany(pending.toList())
                                }
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB72D27), contentColor = Color.White),
                            shape = RoundedCornerShape(22.dp)
                        ) { Text("加入列表（${pending.size}注）", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    }
                }
            }
        }
    }

    if (showTreasurePicker) {
        TreasurePickerDialog(
            items = treasureNumbers.sortedWith(compareByDescending<TreasureNumber> { it.pinned }.thenByDescending { it.addedAt }),
            onDismiss = { showTreasurePicker = false },
            onConfirm = { selected ->
                val added = appendPending(selected)
                showTreasurePicker = false
                Toast.makeText(context, if (added > 0) "已从彩宝库加入 $added 注" else "所选号码已在待加入列表", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun TreasurePickerDialog(
    items: List<TreasureNumber>,
    onDismiss: () -> Unit,
    onConfirm: (List<Prediction>) -> Unit
) {
    val selectedIds = remember { mutableStateListOf<Long>() }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E6)),
            border = BorderStroke(1.5.dp, Gold)
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("从彩宝库选取", fontWeight = FontWeight.Black, fontSize = 19.sp, color = DeepRed)
                Text("可一次选择多注，确认后会加入手动添加的待加入列表。", color = Muted, fontSize = 11.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("已选 ${selectedIds.size} 注", color = DeepGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = {
                        selectedIds.clear()
                        selectedIds.addAll(items.map { it.id })
                    }) { Text("全选", color = DeepGold) }
                }
                LazyColumn(Modifier.heightIn(max = 360.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                        val checked = selectedIds.contains(item.id)
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                if (checked) selectedIds.remove(item.id) else selectedIds.add(item.id)
                            },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF8)),
                            border = BorderStroke(1.dp, Color(0xFFE6D39A)),
                            shape = RoundedCornerShape(13.dp)
                        ) {
                            Column(Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = checked, onCheckedChange = null)
                                    Text("收藏第${index + 1}注", fontWeight = FontWeight.Bold, color = DeepGold, fontSize = 11.sp)
                                    Spacer(Modifier.weight(1f))
                                    Text(formatTime(item.addedAt), color = Muted, fontSize = 9.sp)
                                }
                                BallRow(item.prediction.reds, item.prediction.blue, compact = true)
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("取消") }
                    Button(
                        enabled = selectedIds.isNotEmpty(),
                        onClick = { onConfirm(items.filter { selectedIds.contains(it.id) }.map { it.prediction }) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = LuckyRed, contentColor = Color.White)
                    ) { Text("选取 ${selectedIds.size} 注", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
private fun NumberCell(
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    imeAction: ImeAction,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit,
    onNext: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(54.dp)
            .widthIn(min = 38.dp, max = 52.dp)
            .focusRequester(focusRequester)
            .onFocusChanged {
                focused = it.isFocused
                if (!it.isFocused) onFocusLost()
            },
        singleLine = true,
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = Ink
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
        keyboardActions = KeyboardActions(onNext = { onNext() }, onDone = { onNext() }),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFEFA))
                    .border(
                        width = if (focused) 2.dp else 1.dp,
                        color = if (focused) color else color.copy(alpha = 0.50f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        }
    )
}

private fun parseManualCells(redsRaw: List<String>, blueRaw: String): Triple<String?, List<Int>?, Int?> {
    if (redsRaw.any { it.isBlank() }) return Triple("请填写完整6个红球", null, null)
    val reds = redsRaw.mapNotNull { it.toIntOrNull() }
    if (reds.size != 6) return Triple("红球必须正好输入6个有效数字", null, null)
    if (reds.distinct().size != 6) return Triple("红球不能有重复号码", null, null)
    if (reds.any { it !in 1..33 }) return Triple("红球范围必须是01–33", null, null)
    val blue = blueRaw.toIntOrNull() ?: return Triple("请输入有效蓝球号码", null, null)
    if (blue !in 1..16) return Triple("蓝球范围必须是01–16", null, null)
    return Triple(null, reds.sorted(), blue)
}

private fun parsePastedTicket(text: String): Triple<String?, List<Int>?, Int?> {
    val redBlue = Regex("红球\\s*([0-9\\s,，、]+).*?蓝球\\s*(\\d{1,2})", setOf(RegexOption.DOT_MATCHES_ALL)).find(text)
    if (redBlue != null) {
        val reds = redBlue.groupValues[1]
            .split(Regex("[\\s,，、]+"))
            .filter { it.isNotBlank() }
            .take(6)
            .mapNotNull { it.toIntOrNull() }
        val blue = redBlue.groupValues[2].toIntOrNull()
        if (reds.size == 6 && blue != null) return validateTicket(reds, blue)
    }

    val nums = Regex("\\d{1,2}").findAll(text).mapNotNull { it.value.toIntOrNull() }.toList()
    val candidate = when {
        nums.size == 7 -> nums
        nums.size >= 8 -> nums.takeLast(7)
        else -> return Triple("未识别到完整的一注号码", null, null)
    }
    return validateTicket(candidate.take(6), candidate.last())
}

private fun validateTicket(reds: List<Int>, blue: Int): Triple<String?, List<Int>?, Int?> {
    if (reds.size != 6) return Triple("红球必须正好6个", null, null)
    if (reds.distinct().size != 6) return Triple("红球不能重复", null, null)
    if (reds.any { it !in 1..33 }) return Triple("红球范围必须是01–33", null, null)
    if (blue !in 1..16) return Triple("蓝球范围必须是01–16", null, null)
    return Triple(null, reds.sorted(), blue)
}

@Composable
fun HistoryScreen(vm: MainViewModel) {
    val s = vm.state
    LuxuryPage {
        Column(Modifier.fillMaxSize()) {
            if (s.game == LotteryGame.DLT) {
                BrandHeader("开奖数据 · 大乐透", "中国体彩网公开开奖数据接入中")
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GoldCard {
                        Text("大乐透公开开奖数据正在接入和核验", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DeepRed)
                        Text("当前版本不会把双色球数据当作大乐透数据展示，也不会使用未经验证的第三方开奖号码。", color = Muted, fontSize = 12.sp)
                        Text("规则参考：前区 01–35 选5个｜后区 01–12 选2个", color = DeepGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("开奖日：每周一、三、六（休市日除外）", color = Muted, fontSize = 11.sp)
                    }
                    GoldCard {
                        Text("本版已开放", fontWeight = FontWeight.Black, color = Ink)
                        Text("• 智能批量选号\n• 手动选号\n• 多注列表管理\n• 一键复制", color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
                    }
                }
            } else {
                BrandHeader("开奖数据", "双色球历史开奖 · 公开开奖数据核验")
                Row(Modifier.padding(horizontal = 16.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("已缓存 ${s.draws.size} 期", color = Muted)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = vm::refresh, enabled = !s.loading) { Text("刷新", color = DeepGold, fontWeight = FontWeight.Bold) }
                }
                LazyColumn(modifier = Modifier.padding(horizontal = 14.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(s.draws) { _, d ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF8)), border = BorderStroke(1.2.dp, Color(0xFFE0CB8B)), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("第 ${d.issue} 期", fontWeight = FontWeight.Black, color = Ink)
                                Text(d.date, fontSize = 12.sp, color = Muted)
                                Spacer(Modifier.height(6.dp))
                                BallRow(d.reds, d.blue, compact = true)
                            }
                        }
                    }
                    item { Spacer(Modifier.height(18.dp)) }
                }
            }
        }
    }
}

@Composable
fun MyScreen(vm: MainViewModel) {
    var showSaved by remember { mutableStateOf(false) }
    var showTreasure by remember { mutableStateOf(false) }
    var legalDoc by remember { mutableStateOf<LegalDocument?>(null) }
    if (showSaved) {
        MyNumbersScreen(vm, onBack = { showSaved = false })
        return
    }
    if (showTreasure) {
        TreasureLibraryScreen(vm, onBack = { showTreasure = false })
        return
    }
    legalDoc?.let { doc ->
        LegalTextDialog(doc = doc, onDismiss = { legalDoc = null })
    }

    val s = vm.state
    LuxuryPage {
        Column(Modifier.fillMaxSize()) {
            BrandHeader("我的")
            LazyColumn(
                Modifier.padding(horizontal = 14.dp, vertical = 10.dp).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.caiyunbao_app_icon),
                            contentDescription = "彩运宝",
                            modifier = Modifier.size(54.dp).clip(CircleShape).border(1.5.dp, Color(0xFFD5B653), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("彩运宝", fontWeight = FontWeight.Black, fontSize = 20.sp, color = DeepRed)
                            Text("好运相伴 · 财运亨通", color = Muted, fontSize = 12.sp)
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { showSaved = true },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF8)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.2.dp, Color(0xFFE2CF94))
                    ) {
                        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(Color(0xFFFFE9A7)), contentAlignment = Alignment.Center) {
                                Text("号", color = LuckyRed, fontWeight = FontWeight.Black, fontSize = 17.sp)
                            }
                            Spacer(Modifier.width(11.dp))
                            Column(Modifier.weight(1f)) {
                                Text("我的号码", fontWeight = FontWeight.Black, fontSize = 17.sp, color = Ink)
                                Text("已保存 ${s.savedNumbers.size} 注选号", color = Muted, fontSize = 11.5.sp)
                            }
                            Text("›", color = DeepGold, fontSize = 26.sp)
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { showTreasure = true },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5D7)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.4.dp, Color(0xFFD4A435))
                    ) {
                        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(Color(0xFFB72D27)), contentAlignment = Alignment.Center) {
                                Text("宝", color = Color(0xFFFFE59A), fontWeight = FontWeight.Black, fontSize = 17.sp)
                            }
                            Spacer(Modifier.width(11.dp))
                            Column(Modifier.weight(1f)) {
                                Text("彩宝库", fontWeight = FontWeight.Black, fontSize = 17.sp, color = DeepRed)
                                Text("收藏 ${s.treasureNumbers.size} 注常用号码 · 手动添加可直接取用", color = Muted, fontSize = 11.5.sp)
                            }
                            Text("›", color = DeepGold, fontSize = 26.sp)
                        }
                    }
                }
                item {
                    GoldCard {
                        Text("体验设置", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Ink)
                        SettingSwitch("选号音效", s.soundEnabled, vm::setSoundEnabled)
                        FortuneSoundSelector(selected = s.completionSound, enabled = s.soundEnabled, onSelect = vm::setCompletionSound)
                        SettingSwitch("动画效果", s.animationEnabled, vm::setAnimationEnabled)
                    }
                }
                item {
                    GoldCard {
                        Text("合规与理性提示", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Ink)
                        Text(
                            "本应用不售彩、不代购、不充值投注、不兑奖，不承诺中奖。选号仅供娱乐和历史数据参考。",
                            color = Muted,
                            fontSize = 11.5.sp,
                            lineHeight = 18.sp
                        )
                        Text(
                            "彩运宝为独立第三方工具，非彩票发行机构官方应用。请理性购彩，未成年人不得购买彩票和兑奖。",
                            color = DeepGold,
                            fontSize = 11.5.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
                item {
                    GoldCard {
                        Text("关于彩运宝", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Ink)
                        Text("版本号 $APP_VERSION", color = Muted, fontSize = 12.sp)
                        Text("彩票开奖查询 · 数据参考 · 娱乐选号", color = DeepGold, fontSize = 12.sp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(onClick = { legalDoc = LegalDocument.PRIVACY }, modifier = Modifier.weight(1f)) {
                                Text("隐私政策", color = DeepGold, fontSize = 11.5.sp)
                            }
                            TextButton(onClick = { legalDoc = LegalDocument.AGREEMENT }, modifier = Modifier.weight(1f)) {
                                Text("用户协议", color = DeepGold, fontSize = 11.5.sp)
                            }
                            TextButton(onClick = { legalDoc = LegalDocument.NOTICE }, modifier = Modifier.weight(1f)) {
                                Text("合规说明", color = DeepGold, fontSize = 11.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun latestMatchSummary(prediction: Prediction, draw: DrawResult?): String? {
    draw ?: return null
    val redHits = prediction.reds.count { it in draw.reds }
    val blueHit = prediction.blue == draw.blue
    return "对照最新第${draw.issue}期：红球命中 ${redHits} 个 · 蓝球${if (blueHit) "命中" else "未中"}"
}

@Composable
private fun MyNumbersScreen(vm: MainViewModel, onBack: () -> Unit) {
    val s = vm.state
    val context = LocalContext.current
    val selectedCount = s.savedNumbers.count { it.selected }

    LuxuryPage {
        Column(Modifier.fillMaxSize()) {
            Surface(color = Color(0xFFF2D47C), shadowElevation = 3.dp) {
                Row(
                    Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBack, contentPadding = PaddingValues(horizontal = 6.dp)) {
                        Text("‹ 返回", color = DeepRed, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("我的号码", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Ink)
                        Text("已保存 ${s.savedNumbers.size} 注", color = DeepGold, fontSize = 11.sp)
                    }
                    Surface(color = Color.White.copy(alpha = 0.55f), shape = RoundedCornerShape(16.dp)) {
                        Text(APP_VERSION, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, color = DeepGold, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("号码列表", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Ink)
                Spacer(Modifier.width(7.dp))
                Text("已选 $selectedCount 注", color = Muted, fontSize = 11.5.sp)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { vm.selectAllSaved(selectedCount != s.savedNumbers.size) }) {
                    Text(if (selectedCount == s.savedNumbers.size && s.savedNumbers.isNotEmpty()) "取消全选" else "全选", color = DeepGold)
                }
            }

            if (s.savedNumbers.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GoldCard {
                        Text("还没有保存号码", fontWeight = FontWeight.Bold, color = DeepRed, fontSize = 17.sp)
                        Text("在智能选号页勾选号码后点击“保存号码”，这里就能长期查看。", color = Muted, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(s.savedNumbers, key = { _, item -> item.id }) { index, item ->
                        SavedNumberCard(
                            index = index + 1,
                            item = item,
                            latestDraw = s.draws.firstOrNull(),
                            isFavorite = vm.isTreasure(item.prediction),
                            onToggle = { vm.toggleSavedNumber(item.id) },
                            onFavorite = {
                                val added = vm.addSavedToTreasure(item)
                                Toast.makeText(context, if (added) "已收藏到彩宝库" else "彩宝库中已有这注号码", Toast.LENGTH_SHORT).show()
                            },
                            onCopy = { copyToClipboard(context, "双色球选取号码", vm.copySavedSingleText(item)) },
                            onDelete = { vm.deleteSavedNumber(item.id) }
                        )
                    }
                    item { Spacer(Modifier.height(5.dp)) }
                }
            }

            OutlinedButton(
                enabled = selectedCount > 0,
                onClick = {
                    val added = vm.addSelectedSavedToTreasure()
                    Toast.makeText(context, if (added > 0) "已收藏 $added 注到彩宝库" else "所选号码已经收藏过了", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                border = BorderStroke(1.2.dp, Gold),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepGold),
                shape = RoundedCornerShape(18.dp)
            ) { Text("收藏选中到彩宝库", fontWeight = FontWeight.Bold) }

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    enabled = selectedCount > 0,
                    onClick = {
                        val deleted = vm.deleteSelectedSaved()
                        if (deleted > 0) Toast.makeText(context, "已删除 $deleted 注", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, LuckyRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LuckyRed)
                ) { Text("删除号码") }

                Button(
                    enabled = selectedCount > 0,
                    onClick = { copyToClipboard(context, "双色球选取号码", vm.copySavedSelectedText()) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color(0xFF312100))
                ) { Text("复制号码", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun TreasureLibraryScreen(vm: MainViewModel, onBack: () -> Unit) {
    val s = vm.state
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val ordered = remember(s.treasureNumbers) {
        s.treasureNumbers.sortedWith(compareByDescending<TreasureNumber> { it.pinned }.thenByDescending { it.addedAt })
    }
    val filtered = remember(ordered, query) {
        val q = query.trim().replace(" ", "")
        if (q.isBlank()) ordered else ordered.filter { item ->
            val compact = item.prediction.reds.sorted().joinToString("") { "%02d".format(it) } + "%02d".format(item.prediction.blue)
            val spaced = item.prediction.reds.sorted().joinToString(" ") { "%02d".format(it) } + " %02d".format(item.prediction.blue)
            compact.contains(q) || spaced.contains(query.trim()) || item.source.label.contains(query.trim(), ignoreCase = true)
        }
    }
    val selectedCount = s.treasureNumbers.count { it.selected }

    LuxuryPage {
        Column(Modifier.fillMaxSize()) {
            Surface(color = Color(0xFFF2D47C), shadowElevation = 3.dp) {
                Row(
                    Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBack, contentPadding = PaddingValues(horizontal = 6.dp)) {
                        Text("‹ 返回", color = DeepRed, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("彩宝库", fontWeight = FontWeight.Black, fontSize = 20.sp, color = DeepRed)
                        Text("收藏常用号码 · 支持搜索与置顶", color = DeepGold, fontSize = 11.sp)
                    }
                    Surface(color = Color.White.copy(alpha = 0.55f), shape = RoundedCornerShape(16.dp)) {
                        Text("${s.treasureNumbers.size}注", Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, color = DeepGold, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 7.dp),
                color = Color(0xFFFFFDF7),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Color(0xFFE1C980))
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = { query = it.take(40) },
                    singleLine = true,
                    textStyle = TextStyle(color = Ink, fontSize = 13.sp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                    decorationBox = { inner ->
                        if (query.isBlank()) Text("搜索号码，例如：08 12 15 或 081215", color = Muted, fontSize = 12.sp)
                        inner()
                    }
                )
            }

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("常用收藏", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Ink)
                Spacer(Modifier.width(7.dp))
                Text("显示 ${filtered.size} 注 · 已选 $selectedCount 注", color = Muted, fontSize = 11.sp)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { vm.selectAllTreasure(selectedCount != s.treasureNumbers.size) }) {
                    Text(if (selectedCount == s.treasureNumbers.size && s.treasureNumbers.isNotEmpty()) "取消全选" else "全选", color = DeepGold)
                }
            }

            if (s.treasureNumbers.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GoldCard {
                        Text("彩宝库还是空的", fontWeight = FontWeight.Bold, color = DeepRed, fontSize = 17.sp)
                        Text("进入“我的号码”，把经常使用的号码收藏到这里。", color = Muted, fontSize = 12.sp)
                    }
                }
            } else if (filtered.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("没有找到匹配号码", color = Muted, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(filtered, key = { _, item -> item.id }) { index, item ->
                        TreasureNumberCard(
                            index = index + 1,
                            item = item,
                            onToggle = { vm.toggleTreasure(item.id) },
                            onPin = { vm.toggleTreasurePinned(item.id) },
                            onCopy = { copyToClipboard(context, "双色球选取号码", vm.copyTreasureSingleText(item)) },
                            onDelete = { vm.deleteTreasure(item.id) }
                        )
                    }
                    item { Spacer(Modifier.height(5.dp)) }
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    enabled = selectedCount > 0,
                    onClick = {
                        val deleted = vm.deleteSelectedTreasure()
                        if (deleted > 0) Toast.makeText(context, "已从彩宝库移除 $deleted 注", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, LuckyRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LuckyRed)
                ) { Text("移除收藏") }
                Button(
                    enabled = selectedCount > 0,
                    onClick = { copyToClipboard(context, "双色球选取号码", vm.copyTreasureSelectedText()) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color(0xFF312100))
                ) { Text("复制号码", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun SavedNumberCard(
    index: Int,
    item: SavedNumber,
    latestDraw: DrawResult?,
    isFavorite: Boolean,
    onToggle: () -> Unit,
    onFavorite: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF8)),
        border = BorderStroke(1.dp, Color(0xFFE9D8AA)),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(Modifier.padding(horizontal = 8.dp, vertical = 7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = item.selected, onCheckedChange = { onToggle() }, modifier = Modifier.size(34.dp))
                Surface(color = PaleGold, shape = RoundedCornerShape(8.dp)) {
                    Text("第${index}注", Modifier.padding(horizontal = 7.dp, vertical = 4.dp), color = DeepGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(6.dp))
                Surface(
                    color = if (item.source == TicketSource.MANUAL) Color(0xFFE7F0FA) else Color(0xFFF7E6E1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        item.source.label,
                        Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        color = if (item.source == TicketSource.MANUAL) BallBlue else LuckyRed,
                        fontSize = 10.5.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(formatTime(item.savedAt), color = Muted, fontSize = 9.5.sp)
            }
            Spacer(Modifier.height(5.dp))
            BallRow(item.prediction.reds, item.prediction.blue, compact = true)
            latestMatchSummary(item.prediction, latestDraw)?.let { summary ->
                Spacer(Modifier.height(4.dp))
                Surface(color = Color(0xFFFFF3CC), shape = RoundedCornerShape(8.dp)) {
                    Text(summary, Modifier.padding(horizontal = 7.dp, vertical = 4.dp), color = DeepGold, fontSize = 9.5.sp, fontWeight = FontWeight.Medium)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onFavorite, contentPadding = PaddingValues(horizontal = 7.dp)) {
                    Text(if (isFavorite) "已收藏" else "收藏", color = if (isFavorite) Color(0xFF9A6B10) else DeepGold, fontSize = 11.sp)
                }
                TextButton(onClick = onCopy, contentPadding = PaddingValues(horizontal = 7.dp)) { Text("复制号码", color = DeepGold, fontSize = 11.sp) }
                TextButton(onClick = onDelete, contentPadding = PaddingValues(horizontal = 7.dp)) { Text("删除", color = LuckyRed, fontSize = 11.sp) }
            }
        }
    }
}

@Composable
private fun TreasureNumberCard(
    index: Int,
    item: TreasureNumber,
    onToggle: () -> Unit,
    onPin: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBED)),
        border = BorderStroke(1.2.dp, Color(0xFFDDBD65)),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(Modifier.padding(horizontal = 8.dp, vertical = 7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = item.selected, onCheckedChange = { onToggle() }, modifier = Modifier.size(34.dp))
                Surface(color = Color(0xFFFFE6A2), shape = RoundedCornerShape(8.dp)) {
                    Text("彩宝第${index}注", Modifier.padding(horizontal = 7.dp, vertical = 4.dp), color = DeepRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(6.dp))
                Text(item.source.label, color = if (item.source == TicketSource.MANUAL) BallBlue else LuckyRed, fontSize = 10.sp)
                if (item.pinned) {
                    Spacer(Modifier.width(5.dp))
                    Surface(color = Color(0xFFFFE6A2), shape = RoundedCornerShape(7.dp)) {
                        Text("已置顶", Modifier.padding(horizontal = 5.dp, vertical = 2.dp), color = DeepRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(formatTime(item.addedAt), color = Muted, fontSize = 9.5.sp)
            }
            Spacer(Modifier.height(5.dp))
            BallRow(item.prediction.reds, item.prediction.blue, compact = true)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onPin, contentPadding = PaddingValues(horizontal = 8.dp)) { Text(if (item.pinned) "取消置顶" else "置顶", color = DeepGold, fontSize = 11.sp) }
                TextButton(onClick = onCopy, contentPadding = PaddingValues(horizontal = 8.dp)) { Text("复制号码", color = DeepGold, fontSize = 11.sp) }
                TextButton(onClick = onDelete, contentPadding = PaddingValues(horizontal = 8.dp)) { Text("移除", color = LuckyRed, fontSize = 11.sp) }
            }
        }
    }
}

@Composable
private fun FortuneSoundSelector(
    selected: FortuneSound,
    enabled: Boolean,
    onSelect: (FortuneSound) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text("选号完成音效", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { expanded = true },
                shape = RoundedCornerShape(14.dp),
                color = if (enabled) Color(0xFFFFF7DE) else Color(0xFFF2EFE8),
                border = BorderStroke(1.dp, if (enabled) Gold else Color(0xFFD8D2C5))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(selected.label, fontWeight = FontWeight.Black, color = if (enabled) DeepRed else Muted, fontSize = 14.sp)
                        Text(selected.description, color = Muted, fontSize = 10.5.sp, maxLines = 1)
                    }
                    Text("⌄", color = DeepGold, fontSize = 18.sp)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                FortuneSound.entries.filter { it.storeVisible }.forEach { sound ->
                    val available = fortuneSoundAvailable(context, sound)
                    DropdownMenuItem(
                        text = {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(sound.label, fontWeight = if (sound == selected) FontWeight.Black else FontWeight.Medium)
                                    if (sound.userAdded) {
                                        Spacer(Modifier.width(6.dp))
                                        Surface(
                                            color = if (available) Color(0xFFE8F3E7) else Color(0xFFF2ECE4),
                                            shape = RoundedCornerShape(7.dp)
                                        ) {
                                            Text(
                                                if (available) "已检测" else "未检测",
                                                Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                                fontSize = 8.5.sp,
                                                color = if (available) Color(0xFF4E7C45) else Muted
                                            )
                                        }
                                    }
                                }
                                Text(
                                    if (sound.userAdded && !available) "需放入 res/raw/${sound.resourceName}.mp3（或 ogg/wav）" else sound.description,
                                    fontSize = 10.sp,
                                    color = Muted
                                )
                            }
                        },
                        leadingIcon = { RadioButton(selected = sound == selected, onClick = null) },
                        onClick = { onSelect(sound); expanded = false }
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                enabled = enabled,
                onClick = { previewFortuneSound(context, selected) },
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, Gold),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepGold)
            ) { Text("试听 ${selected.label}", fontSize = 12.sp) }
            Surface(
                modifier = Modifier.weight(1f),
                color = PaleGold.copy(alpha = 0.68f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    "默认推荐：金币暴雨",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                    color = DeepGold,
                    fontSize = 10.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun previewFortuneSound(context: Context, sound: FortuneSound) {
    val resId = fortuneSoundRes(context, sound)
    if (resId == 0) {
        Toast.makeText(
            context,
            "未检测到 ${sound.resourceName}.mp3 / .ogg / .wav，请放入 app/src/main/res/raw/",
            Toast.LENGTH_LONG
        ).show()
        return
    }
    runCatching {
        // MediaPlayer 会从头播放到文件自然结束。商店候选版不展示直接暗示巨额资金到账的自定义语音。
        val player = MediaPlayer.create(context, resId) ?: return
        player.setVolume(0.96f, 0.96f)
        player.setOnCompletionListener { it.release() }
        player.setOnErrorListener { mp, _, _ -> mp.release(); true }
        player.start()
    }
}

@Composable
private fun SettingSwitch(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(title, Modifier.weight(1f), fontSize = 13.sp)
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Gold)
        )
    }
}

@Composable
fun BallRow(reds: List<Int>, blue: Int, compact: Boolean = false) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val preferred = if (compact) 30.dp else 36.dp
        val spacing = if (compact) 4.dp else 6.dp
        val availablePerBall = (maxWidth - (spacing * 6f) - 2.dp) / 7f
        val size = minOf(preferred, maxOf(24.dp, availablePerBall))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            reds.forEach { Ball(it, BallRed, size) }
            Ball(blue, BallBlue, size)
        }
    }
}

@Composable
fun Ball(number: Int, color: Color, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(color.copy(alpha = 0.78f), color)))
            .border(1.dp, Color.White.copy(alpha = 0.65f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "%02d".format(number),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (size < 34.dp) 12.sp else 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun safePlay(player: MediaPlayer?) {
    if (player == null) return
    runCatching {
        if (player.isPlaying) player.pause()
        player.seekTo(0)
        player.start()
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText(label, text))
    Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show()
}

private fun formatTime(ts: Long): String {
    if (ts <= 0L) return "尚未同步"
    return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(ts))
}
