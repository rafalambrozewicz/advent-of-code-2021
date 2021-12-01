package advent.of.code

import org.junit.Test
import java.io.File

class Day01 {

    @Test
    fun partOne() {
        val measurements = File("inputs/day01.txt")
            .readLines()
            .map { it.toInt() }

        val incrementsCount = measurements.windowed(step = 1, size = 2) {
            val (current, next) = it.first() to it.last()
            val isIncrement =  next > current
            isIncrement
        }.count { isIncrement -> isIncrement == true }

        println(incrementsCount) // 1752
    }

    @Test
    fun partTwo() {
        val measurements = File("inputs/day01.txt")
            .readLines()
            .map { it.toInt() }

        val summedMeasurements = measurements.windowed(step = 1, size = 3) { it.sum() }

        val incrementsCount = summedMeasurements.windowed(step = 1, size = 2) {
            val (current, next) = it.first() to it.last()
            val isIncrement =  next > current
            isIncrement
        }.count { isIncrement -> isIncrement == true }

        println(incrementsCount) // 1781
    }
}