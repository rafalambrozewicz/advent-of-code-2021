package advent.of.code

import org.junit.Test
import java.io.File

class Day25 {
    companion object {
        const val INPUT_FILE_NAME = "day25.txt"

        const val EMPTY_SPACE = '.'
        const val EAST_MOVING = '>'
        const val SOUTH_MOVING = 'v'
    }

    @Test
    fun partOne() {
        val state = readInput()

        var step = 0
        var currentState = state
        do {
            step++
            val oldState = currentState
            currentState = move(currentState)

        } while (currentState notSameAs oldState)


        println(step) // 389
    }

    private fun readInput(): Array<CharArray> {
        return File("inputs/${INPUT_FILE_NAME}")
            .readLines()
            .map { l -> l.toCharArray() }
            .toTypedArray()
    }

    private fun move(initial: Array<CharArray>) : Array<CharArray> {
        val moved = Array(initial.size) { BooleanArray(initial.first().size) { false } }

        val result0 = Array(initial.size) { CharArray(initial.first().size) { EMPTY_SPACE } }
        for (y in 0 until initial.size) {
            for (x in 0 until initial.first().size) {
                result0[y][x] = initial[y][x]
            }
        }

        for (y in 0 until result0.size) {
            for (x in 0 until result0.first().size) {
                if (result0[y][x] == EAST_MOVING) {
                    val onRight = if (x == result0.first().size - 1) 0 else x + 1
                    if (!moved[y][x] && initial[y][onRight] == EMPTY_SPACE) {
                        result0[y][x] = EMPTY_SPACE
                        result0[y][onRight] = EAST_MOVING
                        moved[y][onRight] = true
                    }
                }
            }
        }

        val result1 = Array(result0.size) { CharArray(result0.first().size) { EMPTY_SPACE } }
        for (y in 0 until result0.size) {
            for (x in 0 until result0.first().size) {
                result1[y][x] = result0[y][x]
            }
        }

        for (y in 0 until result1.size) {
            for (x in 0 until result1.first().size) {

                if (result0[y][x] == SOUTH_MOVING) {
                    val onDown = if (y == result0.size - 1) 0 else y + 1
                    if (!moved[y][x] && result0[onDown][x] == EMPTY_SPACE) {
                        result1[y][x] = EMPTY_SPACE
                        result1[onDown][x] = SOUTH_MOVING
                        moved[onDown][x] = true
                    }
                }
            }
        }

        return result1
    }

    infix fun  Array<CharArray>.notSameAs(other: Array<CharArray>): Boolean {
        for (y in 0 until this.size) {
            for (x in 0 until this.first().size) {
                if (this[y][x] != other[y][x]) { return true }
            }
        }

        return false
    }

    @Test
    fun partTwo() {
        /* no puzzles for part two */
    }
}