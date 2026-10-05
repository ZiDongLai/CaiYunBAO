package com.ssq.predictor.predict

import com.ssq.predictor.model.DrawResult
import com.ssq.predictor.model.PredictMode
import com.ssq.predictor.model.Prediction
import kotlin.math.abs
import kotlin.random.Random

object PredictionEngine {
    fun generate(
        count: Int,
        history: List<DrawResult>,
        mode: PredictMode,
        random: Random = Random.Default
    ): List<Prediction> {
        val safeCount = count.coerceIn(1, 100)
        val redWeights = DoubleArray(34) { 1.0 }
        val blueWeights = DoubleArray(17) { 1.0 }

        history.take(100).forEachIndexed { index, draw ->
            val recency = 1.0 + (100 - index.coerceAtMost(99)) / 100.0
            draw.reds.forEach { redWeights[it] += recency }
            blueWeights[draw.blue] += recency
        }

        val result = linkedSetOf<Prediction>()
        var guard = 0
        while (result.size < safeCount && guard < safeCount * 100) {
            guard++
            val p = when (mode) {
                PredictMode.RANDOM -> randomTicket(random)
                PredictMode.FREQUENCY -> weightedTicket(redWeights, blueWeights, random)
                PredictMode.BALANCED -> balancedTicket(redWeights, blueWeights, random)
            }
            result += p
        }
        return result.toList()
    }

    private fun randomTicket(random: Random): Prediction {
        val reds = (1..33).shuffled(random).take(6).sorted()
        return Prediction(reds, random.nextInt(1, 17))
    }

    private fun weightedTicket(
        redWeights: DoubleArray,
        blueWeights: DoubleArray,
        random: Random
    ): Prediction {
        val reds = weightedDistinct(1..33, redWeights, 6, random).sorted()
        val blue = weightedOne(1..16, blueWeights, random)
        return Prediction(reds, blue)
    }

    private fun balancedTicket(
        redWeights: DoubleArray,
        blueWeights: DoubleArray,
        random: Random
    ): Prediction {
        repeat(80) {
            val p = weightedTicket(redWeights, blueWeights, random)
            val odd = p.reds.count { n -> n % 2 == 1 }
            val low = p.reds.count { n -> n <= 16 }
            val sum = p.reds.sum()
            val zones = intArrayOf(
                p.reds.count { n -> n in 1..11 },
                p.reds.count { n -> n in 12..22 },
                p.reds.count { n -> n in 23..33 }
            )
            val maxZoneGap = zones.max() - zones.min()
            if (odd in 2..4 && low in 2..4 && sum in 70..140 && maxZoneGap <= 3) return p
        }
        return weightedTicket(redWeights, blueWeights, random)
    }

    private fun weightedDistinct(
        range: IntRange,
        weights: DoubleArray,
        count: Int,
        random: Random
    ): List<Int> {
        val available = range.toMutableList()
        val out = mutableListOf<Int>()
        repeat(count) {
            val choice = weightedOne(available, weights, random)
            out += choice
            available.remove(choice)
        }
        return out
    }

    private fun weightedOne(
        values: Iterable<Int>,
        weights: DoubleArray,
        random: Random
    ): Int {
        val list = values.toList()
        val total = list.sumOf { weights[it].coerceAtLeast(0.01) }
        var r = random.nextDouble() * total
        for (v in list) {
            r -= weights[v].coerceAtLeast(0.01)
            if (r <= 0.0) return v
        }
        return list.last()
    }
}
