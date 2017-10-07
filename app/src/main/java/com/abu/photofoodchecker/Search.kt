package com.abu.photofoodchecker

import android.content.Context
import java.io.File

import me.xdrop.fuzzywuzzy.FuzzySearch
import java.util.*


fun getAdditions(context: Context): Map<String, List<String>> {
    val eNames = mutableMapOf<String, List<String>>()
    val scanner = Scanner(context.assets.open("additions.csv"))

    while (scanner.hasNextLine()) {
        val line = scanner.nextLine()
        val cols = line.split(";")
        eNames[cols[1]] = cols[2].split(", ")
    }

    return eNames
}

fun getComponents(text: String): List<String> {
    return text.split(",", "(", ")", ".", ":")
               .map { it.trim() }
               .map { it.toUpperCase() }
               .filter { !it.isEmpty() }
}

fun getECode(eNames: Map<String, List<String>>, component: String): Pair<String, Int> {
    var bestScore = 0
    var bestE = ""

    for ((e, names) in eNames) {
        val score = maxOf(
            names.map{ FuzzySearch.tokenSortRatio(it, component) }.max() ?: 0,
            FuzzySearch.tokenSortRatio(e, component)
        )

        if (score > bestScore) {
            bestE = e
            bestScore = score
        }
    }

    return Pair(bestE, bestScore)
}

fun match(components: Array<String>, eNames: Map<String, List<String>>) : List<Pair<String, Int>> {

    return components.map { getECode(eNames, it) }
}
