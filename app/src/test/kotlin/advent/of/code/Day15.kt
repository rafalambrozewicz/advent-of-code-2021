package advent.of.code

import org.junit.Test
import java.io.File

class Day15 {

    @Test
    fun partOne() {
        val caveRiskLevels = readInput()

        val costsOfMoving = findCostsForEveryPosition(caveRiskLevels)

        val costOfTopLeftPosition  = costsOfMoving[0][0]
        val costOfBottomRightPosition = costsOfMoving[costsOfMoving.size - 1][costsOfMoving.first().size - 1]

        println(costOfBottomRightPosition - costOfTopLeftPosition) // 442, incorrect
    }

    private fun readInput(): Array<IntArray> {
        return File("inputs/day15.txt")
            .readLines()
            .map { l ->
                l.toCharArray().map { it.toInt() - '0'.toInt() }.toIntArray()
            }
            .toTypedArray()
    }

    private fun findCostsForEveryPosition(caveRiskLevels: Array<IntArray>): Array<IntArray> {
        val allCosts = Array(caveRiskLevels.size) { IntArray(caveRiskLevels.first().size) { 0 } }
        allCosts.indices.forEach { y ->
            allCosts[y].indices.forEach { x ->
                allCosts[y][x] = caveRiskLevels[y][x]
            }
        }

        for (y in 0 until allCosts.size) {
            for (x in 0 until allCosts.first().size) {
                when {
                    (y == 0 && x == 0) -> { /* do nothing */ }
                    (y == 0) -> { allCosts[y][x] = allCosts[y][x-1] + allCosts[y][x] }
                    (x == 0) -> { allCosts[y][x] = allCosts[y-1][x] + allCosts[y][x] }
                    else -> {
                        allCosts[y][x] = minOf(
                            (allCosts[y-1][x] + allCosts[y][x]),
                            (allCosts[y][x-1] + allCosts[y][x])
                        )
                    }

                }
            }
        }

        return allCosts
    }

    @Test
    fun partTwo() {
        val caveRiskLevels = readInput()
        val fullCaveRiskLevels = createFullMap(caveRiskLevels)

        val costsOfMoving = findCostsForEveryPosition(fullCaveRiskLevels)

        val costOfTopLeftPosition  = costsOfMoving[0][0]
        val costOfBottomRightPosition = costsOfMoving[costsOfMoving.size - 1][costsOfMoving.first().size - 1]

        println(costOfBottomRightPosition - costOfTopLeftPosition) // 2853, incorrect
    }

    private fun createFullMap(caveRiskLevels: Array<IntArray>): Array<IntArray> {
        val a = caveRiskLevels

        return ((a inc 0) addRight (a inc 1) addRight (a inc 2) addRight (a inc 3) addRight (a inc 4)) addBottom
               ((a inc 1) addRight (a inc 2) addRight (a inc 3) addRight (a inc 4) addRight (a inc 5)) addBottom
               ((a inc 2) addRight (a inc 3) addRight (a inc 4) addRight (a inc 5) addRight (a inc 6)) addBottom
               ((a inc 3) addRight (a inc 4) addRight (a inc 5) addRight (a inc 6) addRight (a inc 7)) addBottom
               ((a inc 4) addRight (a inc 5) addRight (a inc 6) addRight (a inc 7) addRight (a inc 8))
    }

    private infix fun Array<IntArray>.inc(v: Int): Array<IntArray> {
        val result = Array(this.size) { IntArray(this.first().size) { 0 } }
        result.indices.forEach { y ->
            result[y].indices.forEach { x ->
                result[y][x] =  if ((this[y][x] + v) > 9) { (this[y][x] + v) - 9 }  else { (this[y][x] + v) }
            }
        }
        return  result
    }

    private infix fun Array<IntArray>.addRight(s: Array<IntArray>): Array<IntArray> {
        val result = Array(this.size) { IntArray(this.first().size + s.first().size) { 0 } }
        result.indices.forEach { y ->
            for(x in 0 until (this.first().size + s.first().size)) {
                if (x < this.first().size) {
                    result[y][x] = this[y][x]
                } else {
                    result[y][x] = s[y][x-this.first().size]
                }

            }
        }
        return  result
    }

    private infix fun Array<IntArray>.addBottom(s: Array<IntArray>): Array<IntArray> {
        val result = Array(this.size + s.size) { IntArray(this.first().size) { 0 } }
        for (y in 0 until (this.size + s.size)) {
            result.first().indices.forEach { x ->
                if (y < this.size) {
                    result[y][x] = this[y][x]
                } else {
                    result[y][x] = s[y-this.size][x]
                }
            }
        }

        return  result
    }
}