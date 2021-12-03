package advent.of.code

import org.junit.Test
import java.io.File

class Day03 {

    @Test
    fun partOne() {
        val binaryNumbers = File("inputs/day03.txt")
            .readLines()
            .map { bits -> IntArray(12) { i ->
                (bits[i].toInt() - '0'.toInt())
            } }

        val binaryNumbersCount = binaryNumbers.size

        val counts = binaryNumbers.fold(IntArray(12) { _ -> 0 }) { counts, binaryNumber ->
            for (i in counts.indices) {
                counts[i] += binaryNumber[i]
            }
            counts
        }

        val gammaRateDec = counts
            .joinToString(separator = "") { count -> if (count > (binaryNumbersCount / 2)) "1" else "0" }
            .toInt(2)

        val epsilonRate = counts
            .joinToString(separator = "") { count -> if (count > (binaryNumbersCount / 2)) "0" else "1" }
            .toInt(2)

        val powerConsumed = gammaRateDec * epsilonRate
        println(powerConsumed) // 3687446
    }

    @Test
    fun partTwo() {
        val binaryNumbers = File("inputs/day03.txt")
            .readLines()
            .map { bits -> IntArray(12) { i ->
                (bits[i].toInt() - '0'.toInt())
            } }

        var filteredBinaryNumbers = binaryNumbers
        var bitPosition = 0
        do {
            filteredBinaryNumbers = filterBinaryNumbers(FilteringType.MOST_COMMON, filteredBinaryNumbers, bitPosition)
            bitPosition++

        } while (filteredBinaryNumbers.size > 1)

        val oxygenGeneratorRating = filteredBinaryNumbers.first().joinToString(separator = "").toInt(2)

        filteredBinaryNumbers = binaryNumbers
        bitPosition = 0
        do {
            filteredBinaryNumbers = filterBinaryNumbers(FilteringType.LEAST_COMMON, filteredBinaryNumbers, bitPosition)
            bitPosition++

        } while (filteredBinaryNumbers.size > 1)

        val co2ScrubberRating = filteredBinaryNumbers.first().joinToString(separator = "").toInt(2)

        val lifeSupportRating = oxygenGeneratorRating * co2ScrubberRating
        println(lifeSupportRating) // 4406844
    }

    enum class FilteringType {
        MOST_COMMON,
        LEAST_COMMON
    }

    private fun filterBinaryNumbers(ft: FilteringType, numbers: List<IntArray>, bitPosition: Int): List<IntArray> {
        val totalNumbersCount = numbers.size

        val bitsCount = numbers.fold(0) { count, number ->
            count + number[bitPosition]
        }

        val expectedValue = when(ft) {
            FilteringType.MOST_COMMON -> {
                if ((bitsCount*2) >= totalNumbersCount) 1 else 0
            }
            FilteringType.LEAST_COMMON -> {
                if ((bitsCount*2) >= totalNumbersCount) 0 else 1
            }
        }

        return numbers.filter { it[bitPosition] == expectedValue }
    }
}