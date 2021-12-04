package advent.of.code

import org.junit.Test
import java.io.File

class Day04 {

    class BingoBoard(val rowsToMark: List<List<Int>>) {

        fun isWon(): Boolean = rowsToMark.any { row -> row.isEmpty() }

        fun sumOfUnmarkedNumbers(): Int = (rowsToMark.map { it.sum() }.sum() / 2)

        fun withMarked(numberToMark: Int): BingoBoard {
            val newRows = rowsToMark.map { row ->
                row.minus(numberToMark)
            }
            return BingoBoard(newRows)
        }
    }

    @Test
    fun partOne() {
        val bingoSubsystemOutput = File("inputs/day04.txt")
            .readLines()
            .filter { it.isNotEmpty() }

        val drawnNumbers = bingoSubsystemOutput.first().split(",").map { it.toInt() }

        var boards = bingoSubsystemOutput.drop(1).windowed(size = 5, step = 5).map {
            val rows = it.map { row -> row.trim().split("\\s+".toRegex()).map { n -> n.toInt() } }
            val transposedRows = rows.mapIndexed { rowIndex, _ -> List(5) { i -> rows[i][rowIndex] } }

            BingoBoard(rowsToMark = rows + transposedRows)
        }

        for(numberToMark in drawnNumbers.iterator()) {
            boards = boards.map {
                it.withMarked(numberToMark)
            }

            val maybeWonBoard = boards.firstOrNull { it.isWon() }

            if (maybeWonBoard != null) {
                val sum = maybeWonBoard.sumOfUnmarkedNumbers()
                println(numberToMark * sum) // 87456
                break
            }
        }
    }

    @Test
    fun partTwo() {
        val bingoSubsystemOutput = File("inputs/day04.txt")
            .readLines()
            .filter { it.isNotEmpty() }

        val drawnNumbers = bingoSubsystemOutput.first().split(",").map { it.toInt() }

        var boards = bingoSubsystemOutput.drop(1).windowed(size = 5, step = 5).map {
            val rows = it.map { row -> row.trim().split("\\s+".toRegex()).map { n -> n.toInt() } }
            val transposedRows = rows.mapIndexed { rowIndex, _ -> List(5) { i -> rows[i][rowIndex] } }

            BingoBoard(rowsToMark = rows + transposedRows)
        }

        for(numberToMark in drawnNumbers.iterator()) {
            boards = boards.map {
                it.withMarked(numberToMark)
            }

            if (boards.size != 1) {
                boards = boards.filter { !it.isWon() }
            } else {
                val maybeWonBoard = boards.firstOrNull { it.isWon() }

                if (maybeWonBoard != null) {
                    val sum = maybeWonBoard.sumOfUnmarkedNumbers()
                    println(numberToMark * sum) // 15561
                    break
                }
            }
        }
    }
}