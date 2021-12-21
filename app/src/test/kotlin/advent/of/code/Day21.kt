package advent.of.code

import org.junit.Test
import java.io.File
import kotlin.math.max

class Day21 {

    companion object {
        const val INPUT_FILE_NAME = "day21.txt"
    }

    class DeterministicDice() {
        private val scores = IntArray(100)
        private var roll3TimesCount = 0
        private var position = 0

        init {
            scores[0] = 98 + 99 + 100
            scores[1] = 99+ 100 + 1
            (2 until 100).forEach { i -> scores[i] = ((i-2)..i).sum() }
        }

        fun roll3Times(): Int {
            roll3TimesCount++
            position = (position + 3) % 100
            return scores[position]
        }

        fun roll3TimesCount(): Int = roll3TimesCount
    }

    class Board(
        player1StartingPosition: Int,
        player2StartingPosition: Int
    ) {
        private var player1Position: Int = player1StartingPosition
        private var player2Position: Int = player2StartingPosition

        private var player1Score: Int = 0
        private var player2Score: Int = 0

        fun hasAWinner(): Boolean = (player1Score >= 1000 || player2Score >= 1000)

        fun secondScore(): Int = minOf(player1Score, player2Score)

        fun movePlayer1(steps: Int) {
            player1Position = (player1Position + steps) % 10
            player1Score += if (player1Position == 0) 10 else player1Position
        }

        fun movePlayer2(steps: Int) {
            player2Position = (player2Position + steps) % 10
            player2Score += if (player2Position == 0) 10 else player2Position
        }
    }

    @Test
    fun partOne() {
        val (player1StartingPosition, player2StartingPosition) = readInput()

        val dd = DeterministicDice()
        val board = Board(player1StartingPosition, player2StartingPosition)

        while (true) {
            board.movePlayer1(dd.roll3Times())
            if (board.hasAWinner()) { break }
            board.movePlayer2(dd.roll3Times())
            if (board.hasAWinner()) { break }
        }

        println((dd.roll3TimesCount() * 3) * board.secondScore()) // 900099
    }

    private fun readInput(): Pair<Int, Int> {
        return File("inputs/${INPUT_FILE_NAME}")
            .readLines()
            .map { it.substringAfter(':').trim().toInt() }
            .let {
                it.first() to it. last()
            }
    }

    class QuantumDie() {
        private val a = Array(10) { LongArray(10) }

        init {
            val possibilities = "222".toInt(3)

            val possibleResults = (0..possibilities).map { i ->
                 i.toString(3).padStart(3, '0').map { it.toString().toInt() + 1 }.sum()
            }

            val possibleResultToCount = possibleResults.groupBy { it }.mapValues { (_, values) -> values.size }

            for (initialPosition in a.indices) {
                possibleResultToCount.forEach { steps, count ->
                    val resultPosition = (initialPosition + steps) % 10
                    a[initialPosition][resultPosition] = count.toLong()
                }
            }
        }


        fun statesAfterRoll(input: Triple<Int, Int, Long>): List<Triple<Int, Int, Long>> {
            val (initialPosition, initialScore, initialCount) = input

            val output = a[initialPosition].mapIndexed { nextPosition, count ->
                if (count == 0L) { null }
                else {
                    val score = if (nextPosition == 0) 10 else nextPosition
                    Triple(nextPosition, minOf(initialScore + score, 21), initialCount * count)
                }
            }.mapNotNull { it }

            return output
        }
    }

    @Test
    fun partTwo() {
        val (player1StartingPosition, player2StartingPosition) = readInput()
        val adjustedPlayer1StartingPosition = if (player1StartingPosition == 10) 0 else player1StartingPosition
        val adjustedPlayer2StartingPosition = if (player2StartingPosition == 10) 0 else player2StartingPosition
        val qd = QuantumDie()

        var step = 0
        var countOfStates = Array(10) { Array(22) { Array(10) { LongArray(22)} } }
        countOfStates[adjustedPlayer1StartingPosition][0][adjustedPlayer2StartingPosition][0] = 1

        while (hasNotDeterminedStates(countOfStates)) {
            val updatedCountOfStates = Array(10) { Array(22) { Array(10) { LongArray(22)} } }

            val p1Rolls = (step % 2) == 0

            for (p1Pos in 0 until countOfStates.size) {
                for (p1Score in 0 until countOfStates[p1Pos].size) {
                    for (p2Pos in 0 until countOfStates[p1Pos][p1Score].size) {
                        for (p2Score in 0 until countOfStates[p1Pos][p1Score][p2Pos].size) {
                            val count = countOfStates[p1Pos][p1Score][p2Pos][p2Score]

                            if (count != 0L && p1Score != 21 && p2Score != 21 ) {
                                if (p1Rolls) {
                                    val states = qd.statesAfterRoll(Triple(p1Pos, p1Score, count))

                                    states.forEach { (newP1Pos, newP1Score, newP1Count) ->
                                        updatedCountOfStates[newP1Pos][newP1Score][p2Pos][p2Score] += newP1Count
                                    }
                                } else {
                                    val states = qd.statesAfterRoll(Triple(p2Pos, p2Score, count))

                                    states.forEach { (newP2Pos, newP2Score, newP2Count) ->
                                        updatedCountOfStates[p1Pos][p1Score][newP2Pos][newP2Score] += newP2Count
                                    }
                                }
                            } else if (count != 0L && (p1Score == 21 || p2Score == 21)) {
                                updatedCountOfStates[p1Pos][p1Score][p2Pos][p2Score] += count
                            }
                        }
                    }
                }
            }

            countOfStates = updatedCountOfStates
            step++
        }

        var p1Wins = 0L
        for (p1Pos in 0 until countOfStates.size) {
            for (p1Score in 0 until countOfStates[p1Pos].size) {
                for (p2Pos in 0 until countOfStates[p1Pos][p1Score].size) {
                    for (p2Score in 0 until countOfStates[p1Pos][p1Score][p2Pos].size) {
                        if (p1Score == 21) {
                            p1Wins += countOfStates[p1Pos][p1Score][p2Pos][p2Score]
                        }
                    }
                }
            }
        }

        var p2Wins = 0L
        for (p1Pos in 0 until countOfStates.size) {
            for (p1Score in 0 until countOfStates[p1Pos].size) {
                for (p2Pos in 0 until countOfStates[p1Pos][p1Score].size) {
                    for (p2Score in 0 until countOfStates[p1Pos][p1Score][p2Pos].size) {
                        if (p2Score == 21) {
                            p2Wins += countOfStates[p1Pos][p1Score][p2Pos][p2Score]
                        }
                    }
                }
            }
        }


        val moreWins = maxOf(p1Wins, p2Wins)
        println(moreWins) // 306719685234774
    }

    private fun hasNotDeterminedStates(states: Array<Array<Array<LongArray>>>): Boolean {
        for (p1Pos in 0 until states.size) {
            for (p1Score in 0 until states[p1Pos].size) {
                for (p2Pos in 0 until states[p1Pos][p1Score].size) {
                    for (p2Score in 0 until states[p1Pos][p1Score][p2Pos].size) {
                        val count = states[p1Pos][p1Score][p2Pos][p2Score]

                        if (count != 0L && p1Score != 21 && p2Score != 21 ) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }
}