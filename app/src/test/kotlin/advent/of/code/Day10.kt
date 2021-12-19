package advent.of.code

import org.junit.Test
import java.io.File

class Day10 {

    companion object {
        val OPENING_CHARS = listOf('(', '[', '{', '<')
        val CLOSING_CHARS = listOf(')', ']', '}', '>')
    }

    @Test
    fun partOne() {
        val navigationSubsystemTexts = File("inputs/day10.txt")
            .readLines()
            .map { l ->
                l.toCharArray()
            }

        val score = navigationSubsystemTexts.map { t ->
            firstIllegalClosingCharOrNull(t)?.let {
                scoreForIllegalClosingChar(it)
            } ?: 0
        }.sum()

        println(score) // 311949
    }

    private fun firstIllegalClosingCharOrNull(t: CharArray): Char? {
        val stack = ArrayDeque<Char>()

        for (c in t) {
            val maybeTop = stack.lastOrNull()

            when (c) {
                in OPENING_CHARS -> {
                    stack.addLast(c)
                }
                in CLOSING_CHARS -> {
                    if (c == maybeTop?.expectedClosingChar()) {
                        stack.removeLast()
                    } else {
                        return c
                    }
                }
                else -> throw IllegalArgumentException("Illegal character of '$c'")
            }
        }

        return null
    }

    private fun Char.expectedClosingChar(): Char {
        return when(this) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '<' -> '>'
            else -> throw IllegalArgumentException("Illegal character of '$this'")
        }
    }

    private fun scoreForIllegalClosingChar(c: Char): Int {
        return when (c) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> throw IllegalArgumentException("Illegal closing character of '$c'")
        }
    }

    @Test
    fun partTwo() {
        val validNavigationSubsystemTexts = File("inputs/day10.txt")
            .readLines()
            .map { l ->
                l.toCharArray()
            }.filter { t ->
                firstIllegalClosingCharOrNull(t) == null
            }

        val scores = validNavigationSubsystemTexts.map { t ->
            closingText(t).fold(0L) { totalScore, c ->
                (totalScore * 5) + c.scoreForClosingChar()
            }
        }.sorted()

        println(scores[(scores.size/2)]) // 3042730309
    }

    private fun closingText(t: CharArray): CharArray {
        val stack = ArrayDeque<Char>()

        for (c in t) {
            val maybeTop = stack.lastOrNull()

            when (c) {
                in OPENING_CHARS -> {
                    stack.addLast(c)
                }
                in CLOSING_CHARS -> {
                    if (c == maybeTop?.expectedClosingChar()) {
                        stack.removeLast()
                    } else {
                        throw IllegalStateException("Invalid text of '$t'")
                    }
                }
                else -> throw IllegalArgumentException("Illegal character of '$c'")
            }
        }

        return stack.reversed().map {
            it.expectedClosingChar()
        }.toCharArray()
    }

    private fun Char.scoreForClosingChar(): Int {
        return when (this) {
            ')' -> 1
            ']' -> 2
            '}' -> 3
            '>' -> 4
            else -> throw IllegalArgumentException("Illegal closing character of '$this'")
        }
    }
}