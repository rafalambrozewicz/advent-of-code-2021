package advent.of.code

import org.junit.Test
import java.io.File

class Day14 {

    companion object {
        private const val NOT_COUNTED_IN_PAIRS_LAST_LETTER_SIZE = 1
    }

    @Test
    fun partOne() {
        val (polymerTemplate, insertionRules) = readInput()

        val resultPolymer = (1..10).fold(polymerTemplate) { polymer, _ ->
            polymer.insertPolymers(insertionRules)
        }

        val charToCount = resultPolymer.toCharArray().groupBy { it }

        val mostCommonElementCount = charToCount.maxByOrNull { it.value.size }!!.value.size
        val leastCommonElementCount = charToCount.minByOrNull { it.value.size }!!.value.size

        println(mostCommonElementCount - leastCommonElementCount) // 2223
    }

    private fun readInput(): Pair<String, List<Pair<String, String>>> {
        return File("inputs/day14.txt")
            .readLines()
            .fold(Pair<String, List<Pair<String, String>>>("", emptyList())) { polymerTempToInsRules, line ->
                when {
                    line.isBlank() -> polymerTempToInsRules
                    !line.contains("->") -> polymerTempToInsRules.copy(first = line)
                    line.contains("->") -> polymerTempToInsRules.copy(second = polymerTempToInsRules.second +
                            (line.split(" -> ")[0] to line.split(" -> ")[1]))
                    else -> throw IllegalArgumentException("Illegal line of '$line' in input file")
                }
            }
    }

    private fun String.insertPolymers(insertionRules: List<Pair<String, String>>): String {
        return this.windowed(2, 1) { p ->
            val maybeInsRule = insertionRules.find { it.first == p }
            when {
                maybeInsRule != null -> p[0] + maybeInsRule.second
                else -> p[0].toString()
            }
        }.joinToString(separator = "") + this.last().toString()
    }

    @Test
    fun partTwo() {
        val (polymerTemplate, insertionRules) = readInput()

        val polymerPairToCountMap = (1..40).fold(createPolymerPairToCountMap(polymerTemplate, insertionRules)) { polymerPairToCountMap, _ ->
            val updatedPolymerPairToCountMap = polymerPairToCountMap.toMutableMap()

            insertionRules.forEach { (p, i) ->
                val pairCount = polymerPairToCountMap[p]!!
                if (pairCount != 0L) {
                    updatedPolymerPairToCountMap[p] = updatedPolymerPairToCountMap[p]!! - pairCount
                    updatedPolymerPairToCountMap[p[0] + i] = updatedPolymerPairToCountMap[p[0] + i]!! + pairCount
                    updatedPolymerPairToCountMap[i + p[1]] = updatedPolymerPairToCountMap[i + p[1]]!! + pairCount
                }
            }

            updatedPolymerPairToCountMap
        }

        val letterToCount = polymerPairToCountMap.map { (k, v) -> (k[0].toString() to v) }
            .groupBy { (k, _) -> k }
            .mapValues { (_, v) -> v.map { (_, c) -> c }.sum() }
            .let {
                val updatedMap = it.toMutableMap()
                updatedMap[polymerTemplate.last().toString()] = updatedMap[polymerTemplate.last().toString()]!! + NOT_COUNTED_IN_PAIRS_LAST_LETTER_SIZE
                updatedMap.toMap()
            }

        val mostCommonElementCount = letterToCount.maxByOrNull { it.value }!!.value
        val leastCommonElementCount = letterToCount.minByOrNull { it.value }!!.value

        println(mostCommonElementCount - leastCommonElementCount) // 2566282754493
    }

    private fun createPolymerPairToCountMap(polymerTemplate: String, insertionRules: List<Pair<String, String>>): Map<String, Long> {
        val inPolyLetters = polymerTemplate.toSet()
        val inInsRulLetters = insertionRules.joinToString("") { (p, i) -> p + i }.toSet()

        val letters = (inPolyLetters + inInsRulLetters).sorted()

        val keys = letters.flatMap { l1 -> letters.map { l2 -> l1 + l2.toString()  } }
        val values = keys.map { 0L }
        val keyToValues = keys.zip(values).toMap().toMutableMap()

        polymerTemplate.windowed(size = 2, step = 1) { p ->
            keyToValues[p.toString()] = keyToValues[p.toString()]!! + 1L
        }

        return keyToValues
    }
}