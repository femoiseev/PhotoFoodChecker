package com.abu.photofoodchecker

import android.content.Context
import java.io.File

import me.xdrop.fuzzywuzzy.FuzzySearch
import java.lang.Math.round
import java.lang.Math.sqrt
import java.util.*
import kotlin.coroutines.experimental.EmptyCoroutineContext.plus

data class Additive(val eCode: String,
               val names: List<String>, val dangers: String,
               val description: String, val rank: Int)


fun sortWords(text: String) = text.split(" ").sorted().joinToString(" ")

fun getAdditions(context: Context): Map<String, Additive> {
    val eNames = mutableMapOf<String, Additive>()
    val scanner = Scanner(context.assets.open("additions.csv"))

    while (scanner.hasNextLine()) {
        val line = scanner.nextLine()
        val cols = line.split(";")
        eNames[cols[0].toUpperCase()] = Additive(
                cols[0], cols[1].split(", ").map { sortWords(it) },
                cols[2], cols[3], cols[4].toInt()
        )
    }

    return eNames
}

fun getComponents(text: String): List<String> {
    return text.split(",", "(", ")", ".", ":", "-", ";", "!")
            .map { it.trim() }
            .map { it.toUpperCase() }
            .filter { !it.isEmpty() }
            .map { sortWords(it) }
}

fun getECode(eNames: Map<String, Additive>, component: String): Pair<String, Int> {
    var bestScore = 0
    var bestE = ""

    for ((e, info) in eNames) {
        val score = maxOf(
                info.names.map{ FuzzySearch.ratio(it, component) }.max() ?: 0,
                FuzzySearch.partialRatio(e, component)
        )

        if (score > bestScore) {
            bestE = e
            bestScore = score
        }
    }

    return Pair(bestE, bestScore)
}

fun mark(elems: List<Additive>) : Int {
    val marks = (elems.map { it.rank * it.rank } + listOf(0, 0, 0, 0, 0)).sortedDescending().take(5)
    val squaredRes = marks.sum()

    return 5 - round(sqrt(squaredRes.toDouble() / 5)).toInt()
}

fun match(components: List<String>, eNames: Map<String, Additive>) : List<Additive> {
    return components.map { getECode(eNames, it) }.filter { it.second > 90 } .map { eNames[it.first]!! }.distinctBy { it.eCode }
}
