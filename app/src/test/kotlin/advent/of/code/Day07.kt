package advent.of.code

import org.junit.Test
import java.io.File

class Day07 {

    companion object {
        const val LISTS_START_FROM_ZERO_COMPENSATION = 1
    }

    @Test
    fun partOne() {
        val vPositions = File("inputs/day07.txt")
            .readLines()
            .map { l ->
                l.split(",").map { it.toInt() }
            }
            .flatten()

        val maxVPos = vPositions.max()!!

        val noOfCrabsAtPosition = IntArray((maxVPos + LISTS_START_FROM_ZERO_COMPENSATION)) { 0 }
        vPositions.forEach { p -> noOfCrabsAtPosition[p]++ }

        val sumNoOfCrabsAtPositionUp = noOfCrabsAtPosition.copyOf()
        for (i in 1 until sumNoOfCrabsAtPositionUp.size) {
            sumNoOfCrabsAtPositionUp[i] = sumNoOfCrabsAtPositionUp[i-1] + sumNoOfCrabsAtPositionUp[i]
        }
        val costsOfMovingUp  = IntArray((maxVPos + LISTS_START_FROM_ZERO_COMPENSATION)) { 0 }
        for (p in 1..maxVPos) {
            costsOfMovingUp[p] = costsOfMovingUp[p-1] + sumNoOfCrabsAtPositionUp[p-1]
        }

        val sumNoOfCrabsAtPositionDown = noOfCrabsAtPosition.copyOf()
        for (i in (maxVPos-1) downTo 0 ) {
            sumNoOfCrabsAtPositionDown[i] = sumNoOfCrabsAtPositionDown[i+1] + sumNoOfCrabsAtPositionDown[i]
        }
        val costsOfMovingDown  = IntArray((maxVPos + LISTS_START_FROM_ZERO_COMPENSATION)) { 0 }
        for (p in (maxVPos-1) downTo 0) {
            costsOfMovingDown[p] = costsOfMovingDown[p+1] + sumNoOfCrabsAtPositionDown[p+1]
        }

        val totalCosts = costsOfMovingUp.zip(costsOfMovingDown) { c1, c2 -> c1 + c2 }

        val minCost = totalCosts.min()!!

        println(minCost) // 344297
    }

    @Test
    fun partTwo() {
        val vPositions = File("inputs/day07.txt")
            .readLines()
            .map { l ->
                l.split(",").map { it.toInt() }
            }
            .flatten()

        val maxVPos = vPositions.max()!!

        val noOfCrabsAtPosition = IntArray((maxVPos + LISTS_START_FROM_ZERO_COMPENSATION)) { 0 }
        vPositions.forEach { p -> noOfCrabsAtPosition[p]++ }

        val sumOfCostsOfMovingUp = summedCostsOfMoving(noOfCrabsAtPosition)

        val noOfCrabsAtPositionReversed = noOfCrabsAtPosition.reversed().toIntArray()
        val sumOfCostsOfMovingDown = summedCostsOfMoving(noOfCrabsAtPositionReversed).reversed().toIntArray()

        val totalCosts = sumOfCostsOfMovingUp.zip(sumOfCostsOfMovingDown) { c1, c2 -> c1 + c2 }

        val minCost = totalCosts.min()!!

        println(minCost) // 97164301
    }

    private fun summedCostsOfMoving(noOfCrabsAtPosition: IntArray): IntArray {
        val sumOfCostsOfMoving = IntArray(noOfCrabsAtPosition.size) { 0 }
        for (i in 1 until sumOfCostsOfMoving.size) {
            if (noOfCrabsAtPosition[i-1] != 0) {
                for (j in 0 until (sumOfCostsOfMoving.size-i)) {
                    sumOfCostsOfMoving[i+j] = sumOfCostsOfMoving[i+j] + (noOfCrabsAtPosition[i-1] * (j+1))
                }
            }
        }
        for (i in 1 until sumOfCostsOfMoving.size) {
            sumOfCostsOfMoving[i] = sumOfCostsOfMoving[i-1] + sumOfCostsOfMoving[i]
        }

        return sumOfCostsOfMoving
    }
}