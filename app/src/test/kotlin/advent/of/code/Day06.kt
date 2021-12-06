package advent.of.code

import org.junit.Test
import java.io.File

class Day06 {

    @Test
    fun partOne() {
        val agesOfFish = File("inputs/day06.txt")
            .readLines()
            .map { l ->
                l.split(",").map { it.toInt() }
            }
            .flatten()

        val agesOfFishAfter80Days = agesOfFish.let {
            var ages = it
            for (day in 1..80) {
                var newFishCount = 0
                ages = ages.map { age ->
                    when {
                        age > 0 -> {
                            age - 1
                        }
                        age == 0 -> {
                            newFishCount++
                            6
                        }
                        else -> throw IllegalStateException("Invalid internal timer value!")
                    }
                }
                ages += List(newFishCount) { 8 }
            }
            ages
        }

        println(agesOfFishAfter80Days.count()) // 345793
    }

    @Test
    fun partTwo() {
        val agesOfFish = File("inputs/day06.txt")
            .readLines()
            .map { l ->
                l.split(",").map { it.toInt() }
            }
            .flatten()


        var fishWithGivenAge = LongArray(9) { 0 }
        agesOfFish.forEach { age ->
            fishWithGivenAge[age]++
        }

        repeat((1..256).count()) {
            val newFishWithGivenAge = LongArray(9) { 0 }
            fishWithGivenAge.forEachIndexed { index, value ->
                if (value > 0) {
                    when (index) {
                        0 -> {
                            newFishWithGivenAge[6] = value
                            newFishWithGivenAge[8] = value
                        }
                        else -> {
                            newFishWithGivenAge[index - 1] += value
                        }
                    }
                }
            }
            fishWithGivenAge = newFishWithGivenAge
        }

        println(fishWithGivenAge.sum()) // 1572643095893
    }
}
