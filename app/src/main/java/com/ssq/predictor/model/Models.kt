package com.ssq.predictor.model

data class DrawResult(
    val issue: String,
    val date: String,
    val reds: List<Int>,
    val blue: Int,
    val sales: String? = null,
    val poolMoney: String? = null
) {
    init {
        require(reds.size == 6)
        require(reds.distinct().size == 6)
        require(reds.all { it in 1..33 })
        require(blue in 1..16)
    }
}

data class Prediction(
    val reds: List<Int>,
    val blue: Int
) {
    fun compactLine(index: Int): String {
        val redText = reds.sorted().joinToString(" ") { "%02d".format(it) }
        return "【第${index}注】红球 $redText｜蓝球 %02d".format(blue)
    }
}


data class DltPrediction(
    val fronts: List<Int>,
    val backs: List<Int>
) {
    init {
        require(fronts.size == 5 && fronts.distinct().size == 5 && fronts.all { it in 1..35 })
        require(backs.size == 2 && backs.distinct().size == 2 && backs.all { it in 1..12 })
    }

    fun compactLine(index: Int): String {
        val frontText = fronts.sorted().joinToString(" ") { "%02d".format(it) }
        val backText = backs.sorted().joinToString(" ") { "%02d".format(it) }
        return "【第${index}注】前区 $frontText｜后区 $backText"
    }
}

data class DltPickItem(
    val id: Long,
    val prediction: DltPrediction,
    val source: TicketSource,
    val selected: Boolean = true
)

enum class PredictMode(val label: String) {
    BALANCED("均衡选号"),
    FREQUENCY("热度参考"),
    RANDOM("随机选号")
}

enum class TicketSource(val label: String) {
    MACHINE("选号机"),
    MANUAL("自选")
}

data class PickItem(
    val id: Long,
    val prediction: Prediction,
    val source: TicketSource,
    val selected: Boolean = true
)

enum class LotteryGame(
    val title: String,
    val subtitle: String,
    val category: String,
    val ruleText: String,
    val available: Boolean
) {
    SSQ(
        "双色球",
        "6个红球 + 1个蓝球",
        "福利彩票",
        "红球 01–33 选6个｜蓝球 01–16 选1个",
        true
    ),
    DLT(
        "大乐透",
        "5个前区 + 2个后区",
        "体育彩票",
        "前区 01–35 选5个｜后区 01–12 选2个",
        true
    ),
    FC3D(
        "福彩3D",
        "3位数字",
        "福利彩票",
        "百位、十位、个位各选 0–9",
        false
    ),
    PL3(
        "排列3",
        "3位数字",
        "体育彩票",
        "百位、十位、个位各选 0–9",
        false
    ),
    PL5(
        "排列5",
        "5位数字",
        "体育彩票",
        "5个位置分别选择 0–9",
        false
    ),
    QLC(
        "七乐彩",
        "7个基本号 + 1个特别号",
        "福利彩票",
        "01–30 开出7个基本号及1个特别号",
        false
    )
}
