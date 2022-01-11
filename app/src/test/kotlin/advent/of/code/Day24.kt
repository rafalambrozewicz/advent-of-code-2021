package advent.of.code

import org.junit.Test
import java.io.File

class Day24 {

    companion object {
        const val INPUT_FILE_NAME = "day24.txt"
    }

    data class Instruction(val d: Int, val a: Int, val b: Int)

    @Test
    fun partOne() {
        val instructions = readInput()

        var zToModelNumber = mapOf(0 to "")

        for (ins in instructions) {
            zToModelNumber = zToModelNumber.mapNotNull { (oldZ, oldModelNumber) ->
                ('1' .. '9').mapNotNull { newModelNumberDigit ->
                    val newModelNumber = oldModelNumber + newModelNumberDigit
                    if (ins.d == 1) {
                        // [div z 1] case: z = oldZ * 26 + input + b
                        (oldZ * 26 + newModelNumberDigit.digitToInt() + ins.b) to newModelNumber
                    } else {
                        // [div z 26] case: z = oldZ / 26 <=> oldZ % 26 + a, condition needs to be met if we want 'z' to be 0 at the last step
                        if (newModelNumberDigit.digitToInt() == (oldZ % 26 + ins.a)) {
                            // add answer if condition is met
                            (oldZ / 26) to newModelNumber
                        } else {
                            // dismiss if we are not able to met aforementioned condition
                            null
                        }
                    }
                }
            }
                .flatten()
                .toMap()  // duplicated 'z''s with smaller model number removed here
        }

        val result = zToModelNumber.values.first()

        println(result) // 92915979999498
    }

    private fun readInput(): List<Instruction> {
        val lines = File("inputs/${INPUT_FILE_NAME}")
            .readLines()

        /*
         * Only lines 5, 6 and 16 differ in values and only these are needed to get the final answer.
         * line 5.  could have [div z 1] or [div z 26]
         * line 6.  could have [add x 12] or [add x 11] or [add x 13] or [add x 14] or [add x -10] or [add x -9] or  [add x -3] or [add x -5] or [add x -4]
         * line 16. could have [add y 4] or [add y 11] or [add y 5] or [add y 14] or [add y 7] or [add y 6] or [add y 9] or [add y 12]
         */
        return (lines.mapIndexedNotNull { index, s -> if (s.startsWith("inp")) index else null } + lines.size)
            .windowed(2, 1)
            .map {
                val ins = lines.subList(it.first(), it.last())
                val d = ins[4].split(" ")[2].toInt()
                val a = ins[5].split(" ")[2].toInt()
                val b = ins[15].split(" ")[2].toInt()
                Instruction(d, a, b)
            }
    }

    @Test
    fun partTwo() {
        val instructions = readInput()

        var zToModelNumber = mapOf(0 to "")

        for (ins in instructions) {
            zToModelNumber = zToModelNumber.mapNotNull { (oldZ, oldModelNumber) ->
                ('9' downTo '1').mapNotNull { newModelNumberDigit ->
                    val newModelNumber = oldModelNumber + newModelNumberDigit
                    if (ins.d == 1) {
                        (oldZ * 26 + newModelNumberDigit.digitToInt() + ins.b) to newModelNumber
                    } else {
                        if (newModelNumberDigit.digitToInt() == (oldZ % 26 + ins.a)) {
                            (oldZ / 26) to newModelNumber
                        } else {
                            null
                        }
                    }
                }
            }
                .flatten()
                .toMap()  // duplicated 'z''s with bigger model number removed here
        }

        val result = zToModelNumber.values.first()

        println(result) // 21611513911181
    }
}
