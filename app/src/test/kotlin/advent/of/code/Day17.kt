package advent.of.code

import org.junit.Test
import java.io.File

class Day17 {

    companion object {
        const val INPUT_FILE_NAME = "day17.txt"
    }

    @Test
    fun partOne() {
        val (xRange, yRange) = readInput()

        val highestPoint = bruteForceLookingForHighestDistance(xRange, yRange)

        println(highestPoint)
    }

    private fun readInput(): Pair<IntRange, IntRange> {
        return File("inputs/${INPUT_FILE_NAME}")
            .readLines()
            .map { l ->
                val temp = l.removePrefix("target area:")
                    .split(",")
                    .map {
                        val temp2 = it.substringAfter("=").split("..")
                        temp2.first().toInt() .. temp2.last().toInt()
                    }
                temp.first() to temp.last()
            }
            .first()
    }

    private fun bruteForceLookingForHighestDistance(targetXRange: IntRange, targetYRange: IntRange): Int {
        val initXRange = 0..1000
        val initYRange = -1000..1000

        var highestPoint = Int.MIN_VALUE

        for (initX in initXRange) {
            for (initY in initYRange) {
                val maybeHighestPoint = highestPositionIfInTargetOrNull(initX, initY, targetXRange, targetYRange)
                maybeHighestPoint?.let {
                    if (it > highestPoint) highestPoint = it
                }
            }
        }

        return highestPoint
    }

    private fun highestPositionIfInTargetOrNull(
        initX: Int,
        initY: Int,
        xTargetRange: IntRange,
        yTargetRange: IntRange
    ): Int? {
        var highestPoint = Int.MIN_VALUE
        var inRange = false

        var x = initX
        var y = initY

        var xPos = 0
        var yPos = 0

        for (step in 0 .. 1000) {
            xPos += x
            yPos += y

            if (highestPoint < yPos) { highestPoint = yPos }
            if (xPos in xTargetRange && yPos in yTargetRange) { inRange = true }

            x = if (x>0) { x-1 } else if (x<0) { x+1 } else { x }
            y -= 1
        }

        if (inRange) {
            return highestPoint
        } else {
            return null
        }
    }


    @Test
    fun partTwo() {
        val (xRange, yRange) = readInput()

        val inTargetCount = bruteForceLookingForInTargetCount(xRange, yRange)

        println(inTargetCount) // 2270
    }

    private fun bruteForceLookingForInTargetCount(targetXRange: IntRange, targetYRange: IntRange): Int {
        val initXRange = 0..1000
        val initYRange = -1000..1000

        var inTargetCount = 0

        for (initX in initXRange) {
            for (initY in initYRange) {
                val maybeHighestPoint = highestPositionIfInTargetOrNull(initX, initY, targetXRange, targetYRange)
                maybeHighestPoint?.let { inTargetCount++ }
            }
        }

        return inTargetCount
    }
}