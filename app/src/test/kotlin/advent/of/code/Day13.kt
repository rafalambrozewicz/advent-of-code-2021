package advent.of.code

import org.junit.Test
import java.io.File

class Day13 {

    companion object {
        const val ARRAYS_START_FROM_ZERO_COMPENSATION = 1
    }

    data class Input(val points: List<Pair<Int, Int>>, val folds: List<Fold>) {
        enum class FoldType {
            X, Y
        }

        class Fold(val type: FoldType, val value: Int)
    }

    @Test
    fun partOne() {
        val input = File("inputs/day13.txt")
            .readLines()
            .fold(Input(emptyList(), emptyList())) { oldInput, line ->
                when {
                    line.startsWith("fold along x=") ->
                        oldInput.copy(folds = oldInput.folds +
                                Input.Fold(Input.FoldType.X, line.removePrefix("fold along x=").toInt()))
                    line.startsWith("fold along y=") ->
                        oldInput.copy(folds = oldInput.folds +
                                Input.Fold(Input.FoldType.Y, line.removePrefix("fold along y=").toInt()))
                    line.isBlank() -> oldInput
                    else -> oldInput.copy(points = oldInput.points +
                            Pair(line.split(",")[0].toInt(), line.split(",")[1].toInt()))
                }
            }

        val transparency = populateTransparency(input)

        val transparencyAfterOneFold = input.folds.take(1).fold(transparency) { t, fi -> myFold(t,fi) }

        val visibleDotsCount = transparencyAfterOneFold.flatMap { it.toList() }.count { it == 1 }

        println(visibleDotsCount) // 610
    }

    private fun populateTransparency(i: Input): Array<IntArray> {
        val width = i.points.map { it.first }.max()!! + ARRAYS_START_FROM_ZERO_COMPENSATION
        val height = i.points.map { it.second }.max()!!  + ARRAYS_START_FROM_ZERO_COMPENSATION

        val transparency = Array(height) { IntArray(width) { 0 } }
        i.points.forEach { (x, y) ->
            transparency[y][x] = 1
        }
        return transparency
    }

    private fun myFold(transparency: Array<IntArray>, fi: Input.Fold): Array<IntArray> {
        val width = transparency.first().size
        val height = transparency.size

        return when (fi.type) {
            Input.FoldType.X -> {
                val nt = Array(height) { IntArray(fi.value) { 0 } }
                val r1 = fi.value downTo 0
                val r2 = fi.value .. width
                val r = r1.zip(r2).drop(1)

                r.forEach { (x1, x2) ->
                    for (y in 0 until height) {
                        nt[y][x1] = maxOf(transparency[y][x1], transparency[y][x2])
                    }
                }

                nt
            }
            Input.FoldType.Y -> {
                val nt = Array(fi.value) { IntArray(width) { 0 } }
                val r1 = fi.value downTo 0
                val r2 = fi.value .. height
                val r = r1.zip(r2).drop(1)

                r.forEach { (y1, y2) ->
                    for (x in 0 until width) {
                        nt[y1][x] = maxOf(transparency[y1][x], transparency[y2][x])
                    }
                }

                nt
            }
        }
    }

    @Test
    fun partTwo() {
        val input = File("inputs/day13.txt")
            .readLines()
            .fold(Input(emptyList(), emptyList())) { oldInput, line ->
                when {
                    line.startsWith("fold along x=") ->
                        oldInput.copy(folds = oldInput.folds +
                                Input.Fold(Input.FoldType.X, line.removePrefix("fold along x=").toInt()))
                    line.startsWith("fold along y=") ->
                        oldInput.copy(folds = oldInput.folds +
                                Input.Fold(Input.FoldType.Y, line.removePrefix("fold along y=").toInt()))
                    line.isBlank() -> oldInput
                    else -> oldInput.copy(points = oldInput.points +
                            Pair(line.split(",")[0].toInt(), line.split(",")[1].toInt()))
                }
            }

        val transparency = populateTransparency(input)

        val transparencyAfterFolds = input.folds.fold(transparency) { t, fi -> myFold(t,fi) }

        val code = transparencyAfterFolds.joinToString("\n") {
                ia -> ia.joinToString("") { i -> if (i == 1) "#" else " " }
        }

        println(code) // @down
        /*
         *
         * ###  #### ####   ## #  # ###  #### ####
         * #  #    # #       # #  # #  # #       #
         * #  #   #  ###     # #### #  # ###    #
         * ###   #   #       # #  # ###  #     #
         * #    #    #    #  # #  # # #  #    #
         * #    #### #     ##  #  # #  # #    ####
         *
         */
    }
}