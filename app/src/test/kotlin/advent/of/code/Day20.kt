package advent.of.code

import org.junit.Test
import java.io.File

class Day20 {

    companion object {
        const val INPUT_FILE_NAME = "day20.txt"
    }

    @JvmInline
    value class ImageEnhancementAlgorithm(val s: String) {
        init {
            check(s.all { c -> (c == '.' || c == '#') }) { "String of '.'s and ''#'s is expected" }
        }

        fun valueFor(n: Int): Char {
            require(n < 512) { "Maximum value cannot exceed 512" }
            return s[n]
        }

    }

    @JvmInline
    value class Image(val i: Array<CharArray>) {
        init {
            check(i.all { il -> il.all { c -> (c == '.' || c == '#') }}) { "Arrays of '.'s and ''#'s are expected" }
        }

        fun enhance(iea: ImageEnhancementAlgorithm, enhanceCount: Int): Image {
            val background = if (enhanceCount % 2 == 0) '.' else '#'
            val nia = Array(i.size + 4) { CharArray(i.first().size + 4) { background } }
            val output = Array(i.size + 2) { CharArray(i.first().size + 2) { background } }

            for (y in i.indices) {
                for (x in i[y].indices) {
                    nia[y+2][x+2] = i[y][x]
                }
            }

            for (y in output.indices) {
                for (x in output[y].indices) {
                    val nfp = numberForPixel(x+1, y+1, nia)
                    val vfp = iea.valueFor(nfp)
                    output[y][x] = vfp
                }
            }

            return Image(output)
        }

        private fun numberForPixel(x: Int, y: Int, b: Array<CharArray>): Int {
            var binaryNumber = ""
            for (ly in (y-1)..(y+1)) {
                for (lx in (x-1)..(x+1)) {
                    binaryNumber += b[ly][lx]
                }
            }

            return binaryNumber.map { c ->
                when (c) {
                    '.' -> '0'
                    '#' -> '1'
                    else -> throw IllegalArgumentException("Illegal char of '$c' found!")
                } }
                .joinToString("")
                .toInt(2)
        }

        fun litPixelsCount(): Int {
            var count = 0
            for (y in i.indices) {
                for (x in i[y].indices) {
                    if (i[y][x] == '#') count++
                }
            }
            return count
        }
    }

    @Test
    fun partOne() {
        val (iea, i) = readInput()

        val result = (1..2).fold(i) { img, count -> img.enhance(iea, count - 1) }

        val litPixelsCount = result.litPixelsCount()

        println(litPixelsCount) // 5268
    }

    private fun readInput(): Pair<ImageEnhancementAlgorithm, Image> {
        val lines = File("inputs/${INPUT_FILE_NAME}")
            .readLines()

       val iea = ImageEnhancementAlgorithm(lines.first())

       val i = Image(lines.drop(2).map { it.toCharArray() }.toTypedArray())

        return Pair(iea, i)
    }

    @Test
    fun partTwo() {
        val (iea, i) = readInput()

        val result = (1..50).fold(i) { img, count -> img.enhance(iea, count - 1) }

        val litPixelsCount = result.litPixelsCount()

        println(litPixelsCount) // 16875
    }
}