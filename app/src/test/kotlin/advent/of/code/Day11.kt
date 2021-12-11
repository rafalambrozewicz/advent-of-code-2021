package advent.of.code

import org.junit.Test
import java.io.File

class Day11 {

    @Test
    fun partOne() {
        val energyLevels = File("inputs/day11.txt")
            .readLines()
            .map { l ->
                l.map { c -> c.toInt() - '0'.toInt() }.toIntArray()
            }.toTypedArray()

        val width = energyLevels.size
        val height = energyLevels.first().size

        var flashesCount = 0
        for (step in 0 until 100) {
            val fired = Array(height) { IntArray(width) { 0 } }

            for (y in 0 until height) {
                for (x in 0 until width) {
                    energyLevels[y][x]++
                    if (energyLevels[y][x] > 9 && fired[y][x] == 0) { incAround(x, y, energyLevels, fired) }
                }
            }

            flashesCount += fired.flatMap { it.toList() }.count { it == 1 }

            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (fired[y][x] == 1) { energyLevels[y][x] = 0 }
                }
            }
        }

        println(flashesCount) // 1652
    }

    private fun incAround(x: Int, y: Int, energyLevels: Array<IntArray>, fired: Array<IntArray>) {
        fired[y][x] = 1

        val width = energyLevels.size
        val height = energyLevels.first().size

        val xRange = 0 until width
        val yRange = 0 until height

        fun doIfInRange(x: Int, y: Int) {
            if ((x in xRange) && (y in yRange)) {
                energyLevels[y][x]++
                if (energyLevels[y][x] > 9 && fired[y][x] == 0) { incAround(x, y, energyLevels, fired) }
            }
        }

        doIfInRange(x-1, y-1)
        doIfInRange(x, y-1)
        doIfInRange(x+1, y-1)
        doIfInRange(x-1, y)
        doIfInRange(x+1, y)
        doIfInRange(x-1, y+1)
        doIfInRange(x, y+1)
        doIfInRange(x+1, y+1)
    }

    @Test
    fun partTwo() {
        val energyLevels = File("inputs/day11.txt")
            .readLines()
            .map { l ->
                l.map { c -> c.toInt() - '0'.toInt() }.toIntArray()
            }.toTypedArray()

        val width = energyLevels.size
        val height = energyLevels.first().size

        var step = 0
        while (true) {
            step++
            val fired = Array(height) { IntArray(width) { 0 } }

            for (y in 0 until height) {
                for (x in 0 until width) {
                    energyLevels[y][x]++
                    if (energyLevels[y][x] > 9 && fired[y][x] == 0) { incAround(x, y, energyLevels, fired) }
                }
            }

            if (fired.flatMap { it.toList() }.count { it == 1 } == (width * height)) {
                break
            }

            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (fired[y][x] == 1) { energyLevels[y][x] = 0 }
                }
            }
        }

        println(step) // 220
    }
}