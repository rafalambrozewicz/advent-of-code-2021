package advent.of.code

import org.junit.Test
import java.io.File

class Day08 {

    companion object {
        private const val SIGNAL_LENGTH_ONE = 2
        private const val SIGNAL_LENGTH_FOUR = 4
        private const val SIGNAL_LENGTH_SEVEN = 3
        private const val SIGNAL_LENGTH_EIGHT = 7

        val UNIQUE_SIGNAL_LENGTHS = listOf(SIGNAL_LENGTH_ONE,
            SIGNAL_LENGTH_FOUR,
            SIGNAL_LENGTH_SEVEN,
            SIGNAL_LENGTH_EIGHT)

        private const val ZERO = "abcefg"
        private const val ONE = "cf"
        private const val TWO = "acdeg"
        private const val THREE = "acdfg"
        private const val FOUR = "bcdf"
        private const val FIVE = "abdfg"
        private const val SIX = "abdefg"
        private const val SEVEN = "acf"
        private const val EIGHT = "abcdefg"
        private const val NINE = "abcdfg"

        val VALID_7_SEGMENT_DISPLAY_NUMBERS = listOf(ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE)
    }

    @Test
    fun partOne() {
        val outputsCombined = File("inputs/day08.txt")
            .readLines()
            .map { l ->
                l.substringAfter('|').trim().split(" ")
            }
            .flatten()


        val uniqueLengthSignalCount = outputsCombined.count { it.length in UNIQUE_SIGNAL_LENGTHS }

        println(uniqueLengthSignalCount) // 261
    }


    private fun String.decodeNormalized(key: String): String = this.map { key[it.toInt() - 'a'.toInt()] }.sorted().joinToString(separator =  "")

    private fun String.isValidSegmentDisplayNumber(): Boolean = VALID_7_SEGMENT_DISPLAY_NUMBERS.contains(this)

    private fun String.toNumberOrNull(): Int? {
        return when (this) {
            ZERO ->  0
            ONE -> 1
            TWO -> 2
            THREE -> 3
            FOUR -> 4
            FIVE -> 5
            SIX -> 6
            SEVEN -> 7
            EIGHT -> 8
            NINE -> 9
            else -> null
        }
    }

    private fun allKeys(): List<String> {
        val key = "abcdefg".toSet()
        val keys = allPermutations(key)
        return keys.map { it.joinToString(separator = "") }
    }

    // https://stackoverflow.com/a/63532094 @ down
    fun <T> allPermutations(set: Set<T>): Set<List<T>> {
        if (set.isEmpty()) return emptySet()

        fun <T> _allPermutations(list: List<T>): Set<List<T>> {
            if (list.isEmpty()) return setOf(emptyList())

            val result: MutableSet<List<T>> = mutableSetOf()
            for (i in list.indices) {
                _allPermutations(list - list[i]).forEach{
                        item -> result.add(item + list[i])
                }
            }
            return result
        }

        return _allPermutations(set.toList())
    }
    // https://stackoverflow.com/a/63532094 @ up

    @Test
    fun partTwo() {
        val signalPatternsToOutputs = File("inputs/day08.txt")
            .readLines()
            .map { l ->
                val signalPatterns = l.substringBefore('|').trim().split(" ")
                val outputs = l.substringAfter('|').trim().split(" ")
                signalPatterns to outputs
            }

        val keys = allKeys()

        val sum = signalPatternsToOutputs.map { (signalPatterns, outputs) ->
            val validKey = keys.find { key ->
                signalPatterns.all { sp ->
                    sp.decodeNormalized(key).isValidSegmentDisplayNumber()
                }
            }!!

            val firstDigit = outputs[0].decodeNormalized(validKey).toNumberOrNull()!!
            val secondDigit = outputs[1].decodeNormalized(validKey).toNumberOrNull()!!
            val thirdDigit = outputs[2].decodeNormalized(validKey).toNumberOrNull()!!
            val fourthDigit = outputs[3].decodeNormalized(validKey).toNumberOrNull()!!

            (firstDigit * 1000) + (secondDigit * 100) + (thirdDigit * 10) + fourthDigit
        }.sum()

        println(sum) // 987553
    }
}