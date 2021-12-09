package advent.of.code

import org.junit.Test
import java.io.File

class Day09 {

    @Test
    fun partOne() {
        val heightmap = File("inputs/day09.txt")
            .readLines()
            .map { l ->
                l.toCharArray().map { it.toInt() - '0'.toInt() }.toIntArray()
            }.toTypedArray()

        val width = heightmap.first().size
        val height = heightmap.size

        val xRange = 0 until width
        val yRange = 0 until height

        var sumOfRiskLevels = 0
        for (x in xRange) {
            for (y in yRange) {
                val current = heightmap[y][x]

                val left = if (x-1 in xRange) heightmap[y][x-1] else 10
                val right = if (x+1 in xRange) heightmap[y][x+1] else 10
                val up = if (y-1 in yRange) heightmap[y-1][x] else 10
                val down = if (y+1 in yRange) heightmap[y+1][x] else 10

                if ( current < left && current < right && current < up && current < down) {
                    sumOfRiskLevels += (current + 1)
                }
            }
        }

        println(sumOfRiskLevels) // 548
    }

    @Test
    fun partTwo() {
        val heightmap = File("inputs/day09.txt")
            .readLines()
            .map { l ->
                l.toCharArray().map { it.toInt() - '0'.toInt() }.toIntArray()
            }.toTypedArray()

        val width = heightmap.first().size
        val height = heightmap.size

        val xRange = 0 until width
        val yRange = 0 until height

        val basinSizes = mutableListOf<Int>()
        for (x in xRange) {
            for (y in yRange) {
                val current = heightmap[y][x]

                val left = if (x-1 in xRange) heightmap[y][x-1] else 10
                val right = if (x+1 in xRange) heightmap[y][x+1] else 10
                val up = if (y-1 in yRange) heightmap[y-1][x] else 10
                val down = if (y+1 in yRange) heightmap[y+1][x] else 10

                if ( current < left && current < right && current < up && current < down) {
                    basinSizes.add(findBasinSize(Pair(x, y), heightmap))
                }
            }
        }

        val multipliedSizesOfThreeLargestBasins = basinSizes.sortedDescending()
            .take(3)
            .fold(1) { acc, value -> acc * value }

        println(multipliedSizesOfThreeLargestBasins) // 786048
    }

    private fun findBasinSize(startPoint: Pair<Int, Int>, heightmap: Array<IntArray>): Int {
        val width = heightmap.first().size
        val height = heightmap.size

        val xRange = 0 until width
        val yRange = 0 until height

        val visited = Array(heightmap.size) { IntArray(heightmap.first().size) { 0 } }
        var size = 0

        fun findBasinSizeRec(start: Pair<Int, Int>) {
            val (x, y) = start
            visited[y][x] = 1
            size++

            if ((x-1 in xRange) && (heightmap[y][x-1] < 9) && (visited[y][x-1] == 0)) findBasinSizeRec(Pair(x-1, y))
            if ((x+1 in xRange) && (heightmap[y][x+1] < 9) && (visited[y][x+1] == 0)) findBasinSizeRec(Pair(x+1, y))
            if ((y-1 in yRange) && (heightmap[y-1][x] < 9) && (visited[y-1][x] == 0)) findBasinSizeRec(Pair(x, y-1))
            if ((y+1 in yRange) && (heightmap[y+1][x] < 9) && (visited[y+1][x] == 0)) findBasinSizeRec(Pair(x, y+1))
        }

        findBasinSizeRec(startPoint)

        return size
    }
}