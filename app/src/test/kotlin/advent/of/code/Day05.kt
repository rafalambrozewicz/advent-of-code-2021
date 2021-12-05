package advent.of.code

import org.junit.Test
import java.io.File

class Day05 {

    companion object {
        const val LISTS_START_FROM_ZERO_COMPENSATION = 1
    }

    class Line(val x1: Int,
               val y1: Int,
               val x2: Int,
               val y2: Int)

    @Test
    fun partOne() {
        val horizontalAndVerticalLines = File("inputs/day05.txt")
            .readLines()
            .map { l ->
                val values = l.replace(" -> ", ",").split(",").map { it.toInt() }
                Line(values[0], values[1], values[2], values[3])
            }
            .filter { (it.x1 == it.x2) || (it.y1 == it.y2) }

        val sizeX = horizontalAndVerticalLines.maxBy { maxOf(it.x1, it.x2) }!!.let { maxOf(it.x1, it.x2) } + LISTS_START_FROM_ZERO_COMPENSATION
        val sizeY = horizontalAndVerticalLines.maxBy { maxOf(it.y1, it.y2) }!!.let { maxOf(it.y1, it.y2) } + LISTS_START_FROM_ZERO_COMPENSATION

        val board = Array(sizeY) { _ -> IntArray(sizeX) { _ -> 0}}

        horizontalAndVerticalLines.forEach { l ->
            val xRange = if (l.x1 < l.x2) l.x1 .. l.x2 else l.x1 downTo l.x2
            val yRange = if (l.y1 < l.y2) l.y1 .. l.y2 else l.y1 downTo l.y2

            for (x in xRange) {
                for (y in yRange) {
                    board[y][x]++
                }
            }
        }

        val overlappingPoints = board
            .flatMap { it.asList() }
            .count { it >= 2 }

        println(overlappingPoints) // 7473
    }

    @Test
    fun partTwo() {
        val lines = File("inputs/day05.txt")
            .readLines()
            .map { l ->
                val values = l.replace(" -> ", ",").split(",").map { it.toInt() }
                Line(values[0], values[1], values[2], values[3])
            }

        val sizeX = lines.maxBy { maxOf(it.x1, it.x2) }!!.let { maxOf(it.x1, it.x2) } + LISTS_START_FROM_ZERO_COMPENSATION
        val sizeY = lines.maxBy { maxOf(it.y1, it.y2) }!!.let { maxOf(it.y1, it.y2) } + LISTS_START_FROM_ZERO_COMPENSATION

        val board = Array(sizeY) { _ -> IntArray(sizeX) { _ -> 0}}

        val horizontalAndVerticalLines = lines.filter { (it.x1 == it.x2) || (it.y1 == it.y2) }
        val diagonalLines = lines.filter { (it.x1 != it.x2) && (it.y1 != it.y2) }

        horizontalAndVerticalLines.forEach { l ->
            val xRange = if (l.x1 < l.x2) l.x1 .. l.x2 else l.x1 downTo l.x2
            val yRange = if (l.y1 < l.y2) l.y1 .. l.y2 else l.y1 downTo l.y2

            for (x in xRange) {
                for (y in yRange) {
                    board[y][x]++
                }
            }
        }

        diagonalLines.forEach { l ->
            val xRange = if (l.x1 < l.x2) l.x1 .. l.x2 else l.x1 downTo l.x2
            val yRange = if (l.y1 < l.y2) l.y1 .. l.y2 else l.y1 downTo l.y2

            val xYRange = xRange.zip(yRange)

            xYRange.forEach { (x, y) ->
                board[y][x]++
            }
        }

        val overlappingPoints = board
            .flatMap { it.asList() }
            .count { it >= 2 }

        println(overlappingPoints) // 24164
    }
}