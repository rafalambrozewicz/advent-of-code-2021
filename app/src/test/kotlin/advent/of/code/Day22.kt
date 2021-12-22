package advent.of.code

import org.junit.Test
import java.io.File


class Day22 {

    companion object {
        const val INPUT_FILE_NAME = "day22.txt"

        const val ON = 1
        const val OFF = 0
    }

    data class Cube(
        val action: ActionType,
        val xRange: IntRange,
        val yRange: IntRange,
        val zRange: IntRange,
    ) {
        enum class ActionType {
            ON,
            OFF
        }

        fun volume(): Long {
            return xRange.length() * yRange.length() * zRange.length() * (if (action == ActionType.ON) 1L else -1L)
        }

        private fun IntRange.length(): Long  = this.last - this.first + 1L

        fun intersect(c: Cube, action: ActionType): Cube? {
            val maybeX = this.xRange.union(c.xRange)
            val maybeY = this.yRange.union(c.yRange)
            val maybeZ = this.zRange.union(c.zRange)

            if (maybeX != null && maybeY != null && maybeZ !=null) {
                return Cube(action, maybeX, maybeY, maybeZ)
            }

            return null
        }

        private fun IntRange.union(ir: IntRange): IntRange? {
            val start = maxOf(this.first, ir.first)
            val end = minOf(this.last, ir.last)
            if (start < end) {
                return start..end
            }

            return null
        }
    }

    @Test
    fun partOne() {
        val cubes = readInput()

        val simpleCube = Array(101) { Array(101) { IntArray(101) { OFF } } }
        val sizeRange = -50..50

        cubes.filter { c ->
            sizeRange.contains(c.xRange) && sizeRange.contains(c.yRange) && sizeRange.contains(c.zRange)
        }.forEach { c ->
            val action = when (c.action) {
                Cube.ActionType.ON -> ON
                Cube.ActionType.OFF -> OFF
            }

            for(x in c.xRange.offset(50)) {
                for (y in c.yRange.offset(50)) {
                    for (z in c.zRange.offset(50)) {
                        simpleCube[x][y][z] = action
                    }
                }
            }
        }

        var litCubesCount = 0

        for(x in sizeRange.offset(50)) {
            for (y in sizeRange.offset(50)) {
                for (z in sizeRange.offset(50)) {
                    if (simpleCube[x][y][z] == ON) litCubesCount++
                }
            }
        }

        println(litCubesCount) // 591365
    }

    private fun readInput(): List<Cube> {
        return File("inputs/${INPUT_FILE_NAME}")
            .readLines()
            .map { l ->
                val (actionString, rest) = l.split(" ").let { it.first() to it.last() }
                val action = when (actionString) {
                    "on" -> Cube.ActionType.ON
                    "off" -> Cube.ActionType.OFF
                    else -> throw IllegalArgumentException("Unknown action of '$actionString'")
                }
                val (xRange, yRange, zRange) = rest.split(",")
                    .map {
                        it.substringAfter('=')
                            .split("..")
                            .let { (it.first().toInt())..(it.last().toInt()) }
                    }.let { Triple(it[0], it[1], it[2]) }

                Cube(action, xRange, yRange, zRange)
            }
    }

    private fun IntRange.contains(ir: IntRange) = this.contains(ir.first) && this.contains(ir.last)

    private fun IntRange.offset(o: Int) = (this.first + o)..(this.last + o)

    @Test
    fun partTwo() {
        val cubes = readInput()

        val resultingCubes = mutableListOf<Cube>()
        for (c in cubes) {
            val intersections = resultingCubes.mapNotNull { rc ->
                val invAction = if (rc.action == Cube.ActionType.ON) Cube.ActionType.OFF else Cube.ActionType.ON

                rc.intersect(c, invAction)
            }

            resultingCubes.addAll(intersections)

            if (c.action == Cube.ActionType.ON) resultingCubes.add(c)
        }

        val result = resultingCubes.sumOf { it.volume() }

        println(result) // 1211172281877240
    }
}