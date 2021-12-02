package advent.of.code

import org.junit.Test
import java.io.File

class Day02 {

    @Test
    fun partOne() {
        val commands = File("inputs/day02.txt")
            .readLines()
            .map { it.split(" ").let { commandToValue ->
                commandToValue.first() to commandToValue.last().toInt()
            }}

        val (horizontalPosition, depth) = commands.fold(Pair(0,0)) { (horizontalPosition, depth), (command, value) ->
            when (command) {
                "forward" -> Pair(horizontalPosition + value, depth)
                "down" -> Pair(horizontalPosition, depth + value)
                "up" -> Pair(horizontalPosition, depth - value)
                else -> throw IllegalStateException("Ups! There is an unknown command in input file!")
            }
        }

        println(horizontalPosition * depth) // 1660158
    }

    @Test
    fun partTwo() {
        val commands = File("inputs/day02.txt")
            .readLines()
            .map { it.split(" ").let { commandToValue ->
                commandToValue.first() to commandToValue.last().toInt()
            }}

        val (horizontalPosition, depth, _) = commands.fold(Triple(0,0, 0)) {
                (horizontalPosition, depth, aim), (command, value) ->
                    when (command) {
                        "forward" -> Triple(horizontalPosition + value, depth + (aim * value), aim)
                        "down" -> Triple(horizontalPosition, depth, aim + value)
                        "up" -> Triple(horizontalPosition, depth, aim - value)
                        else -> throw IllegalStateException("Ups! There is an unknown command in input file!")
                }
        }

        println(horizontalPosition * depth) // 1604592846
    }
}