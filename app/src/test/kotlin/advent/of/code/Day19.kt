package advent.of.code

import org.junit.Test
import java.io.File

class Day19 {

    companion object {
        const val INPUT_FILE_NAME = "day19.txt"

        val X_DIMENSION_TRANSFORMATIONS = listOf(
            DimensionTransformation(xAxis, xAxis, normal),
            DimensionTransformation(xAxis, xAxis, inversed),
            DimensionTransformation(xAxis, yAxis, normal),
            DimensionTransformation(xAxis, yAxis, inversed),
            DimensionTransformation(xAxis, zAxis, normal),
            DimensionTransformation(xAxis, zAxis, inversed)
        )
        val Y_DIMENSION_TRANSFORMATIONS = listOf(
            DimensionTransformation(yAxis, xAxis, normal),
            DimensionTransformation(yAxis, xAxis, inversed),
            DimensionTransformation(yAxis, yAxis, normal),
            DimensionTransformation(yAxis, yAxis, inversed),
            DimensionTransformation(yAxis, zAxis, normal),
            DimensionTransformation(yAxis, zAxis, inversed)
        )

        val Z_DIMENSION_TRANSFORMATIONS = listOf(
            DimensionTransformation(zAxis, xAxis, normal),
            DimensionTransformation(zAxis, xAxis, inversed),
            DimensionTransformation(zAxis, yAxis, normal),
            DimensionTransformation(zAxis, yAxis, inversed),
            DimensionTransformation(zAxis, zAxis, normal),
            DimensionTransformation(zAxis, zAxis, inversed),
        )

        fun remainingTransformations(selected: List<DimensionTransformation>,
                                     transformations: List<DimensionTransformation>): List<DimensionTransformation> {
            return transformations.filter { t ->
                selected.all { s -> (s.inputDimension != t.inputDimension) && (s.outputDimension != t.outputDimension) }
            }
        }
    }

    sealed class Dimension
    object xAxis: Dimension()
    object yAxis: Dimension()
    object zAxis: Dimension()

    sealed class Orientation
    object normal: Orientation()
    object inversed: Orientation()

    data class DimensionTransformation(
        val inputDimension: Dimension,
        val outputDimension: Dimension,
        val outputOrientation: Orientation,
    ) {
        fun calculateChange(p1: Point, p2: Point): Int {
            val inputValue = when (inputDimension) {
                xAxis -> p1.x
                yAxis -> p1.y
                zAxis -> p1.z
            }

            val outputValue = when (outputDimension) {
                xAxis -> p2.x
                yAxis -> p2.y
                zAxis -> p2.z
            }.let { ov ->
                when (outputOrientation) {
                    inversed -> ov * -1
                    normal -> ov
                }
            }

            return outputValue - inputValue
        }
    }

    data class CoordinateTransformation(val dt: DimensionTransformation, val change: Int)

    data class PointTransformation(
        val dt1: CoordinateTransformation,
        val dt2: CoordinateTransformation,
        val dt3: CoordinateTransformation
    )

    data class Point(
        val x: Int,
        val y: Int,
        val z: Int) {

        fun transform(pt: PointTransformation): Point {
            val dimensionsToValue = listOf(transform(pt.dt1), transform(pt.dt2), transform(pt.dt3))
            val x = dimensionsToValue.find { (orientation, _) -> orientation == xAxis }!!.second
            val y = dimensionsToValue.find { (orientation, _) -> orientation == yAxis }!!.second
            val z = dimensionsToValue.find { (orientation, _) -> orientation == zAxis }!!.second

            return Point(x, y, z)
        }

        fun transform(ct: CoordinateTransformation): Pair<Dimension, Int> {
            return when (ct.dt.inputDimension) {
                xAxis -> when (ct.dt.outputOrientation) {
                    inversed -> Pair(ct.dt.outputDimension, (x + ct.change) * -1)
                    normal -> Pair(ct.dt.outputDimension, (x + ct.change))
                }
                yAxis -> when (ct.dt.outputOrientation) {
                    inversed -> Pair(ct.dt.outputDimension, (y + ct.change) * -1)
                    normal -> Pair(ct.dt.outputDimension, (y + ct.change))
                }
                zAxis -> when (ct.dt.outputOrientation) {
                    inversed -> Pair(ct.dt.outputDimension, (z + ct.change) * -1)
                    normal -> Pair(ct.dt.outputDimension, (z + ct.change))
                }
            }
        }

        fun distanceTo(p: Point): Int {
            return (x - p.x) + (y - p.y) + (z - p.z)
        }
    }

    private fun Pair<Dimension, Int>.matches(p: Point): Boolean {
        return when(this.first) {
            xAxis -> p.x == this.second
            yAxis -> p.y == this.second
            zAxis -> p.z == this.second
        }
    }

    data class ScannerData(
        val name: String,
        val points: List<Point>,
        val scannerNameToPointTransformation: Pair<String, List<PointTransformation>>? = null)

    @Test
    fun partOne() {
        val scannerData = readInput()

        val updatedScannerData = updateScannerDataAddingTransformations(scannerData)
        val scannerDataWithPointsRelativeToScanner0 = makeAllPointsRelativeToScanner0(updatedScannerData)

        val allPoints = scannerDataWithPointsRelativeToScanner0.flatMap { it.points }.toSet()

        println(allPoints.size) // 459
    }

    private fun readInput(): List<ScannerData> {
        val mScaners = mutableListOf<ScannerData>()

        File("inputs/${INPUT_FILE_NAME}")
            .readLines()
            .forEach { l ->
                when {
                    l.isBlank() -> { /* do nothing */ }
                    l.startsWith("---") -> mScaners.add(ScannerData(l, emptyList()))
                    else -> {
                        val numbers = l.split(",").map { it.toInt() }
                        val scannerData = mScaners.last()
                            .copy(points = mScaners.last().points + Point(numbers[0], numbers[1], numbers[2]))
                        mScaners.removeLast()
                        mScaners.add(scannerData)
                    }

                }
            }
        return mScaners
    }

    private fun updateScannerDataAddingTransformations(sdl: List<ScannerData>): List<ScannerData> {
        val root = sdl.first()
        val updatedScannerData = sdl.toMutableList()
        updatedScannerData.replaceAll { sd ->
            if (sd == root) {
                root.copy(scannerNameToPointTransformation = Pair("--- scanner 0 ---", listOf(PointTransformation(
                    CoordinateTransformation(DimensionTransformation(xAxis, xAxis, normal), 0),
                    CoordinateTransformation(DimensionTransformation(yAxis, yAxis, normal), 0),
                    CoordinateTransformation(DimensionTransformation(zAxis, zAxis, normal), 0)
                ))))
            } else {
                sd
            }
        }

        val processed = mutableSetOf<ScannerData>()
        do {
            val found = updatedScannerData.filter { it.scannerNameToPointTransformation != null } - processed
            val notFound = updatedScannerData.filter { it.scannerNameToPointTransformation == null }

            for(f in found) {
                for (nf in notFound) {
                    val maybePointTransformation = findPointTransformationOrNull(nf, f)
                    if (maybePointTransformation != null) {
                        updatedScannerData.replaceAll { sd ->
                            if (sd == nf) {
                                nf.copy(scannerNameToPointTransformation = Pair(f.name, listOf(maybePointTransformation) + f.scannerNameToPointTransformation!!.second ))
                            } else {
                                sd
                            }
                        }
                    }
                }
                processed.add(f)
            }
        } while (updatedScannerData.any { sd -> (sd.scannerNameToPointTransformation == null) })

        return updatedScannerData
    }

    private fun findPointTransformationOrNull(sd1: ScannerData, sd2: ScannerData): PointTransformation? {
        val points = sd1.points.map { p1 -> sd2.points.map { p2 -> Pair(p1,p2) } }.flatten()

        val stepX = possibleCoordinateTransformations(xAxis, emptyList(), points)

        val stepY = stepX.map { (selectedTransformations, pointsMatching) ->
            possibleCoordinateTransformations(yAxis, selectedTransformations, pointsMatching)
        }.fold(mutableMapOf<List<CoordinateTransformation>, List<Pair<Point,Point>>>()) { acc, e ->
            e.forEach { cts, pts -> acc.put(cts, pts) }
            acc
        }

        val stepZ = stepY.map { (selectedTransformations, pointsMatching) ->
            possibleCoordinateTransformations(zAxis, selectedTransformations, pointsMatching)
        }.fold(mutableMapOf<List<CoordinateTransformation>, List<Pair<Point,Point>>>()) { acc, e ->
            e.forEach { cts, pts -> acc.put(cts, pts) }
            acc
        }

        val coordinateTransformations = stepZ.keys.firstOrNull() ?: emptyList()

        return if (coordinateTransformations.size == 3) {
            PointTransformation(
                coordinateTransformations[0],
                coordinateTransformations[1],
                coordinateTransformations[2])
        } else {
            null
        }
    }

    private fun possibleCoordinateTransformations(
        axis: Dimension,
        selectedCoordinateTransformations: List<CoordinateTransformation>,
        points: List<Pair<Point,Point>>): Map<List<CoordinateTransformation>, List<Pair<Point,Point>>> {

        val dts = when(axis) {
            xAxis -> X_DIMENSION_TRANSFORMATIONS
            yAxis -> Y_DIMENSION_TRANSFORMATIONS
            zAxis -> Z_DIMENSION_TRANSFORMATIONS
        }

        val rdt = remainingTransformations(selectedCoordinateTransformations.map { it.dt }, dts)

        val pct = rdt.map { dt ->
            points.map { (p1, p2) ->
                val change = dt.calculateChange(p1, p2)
                CoordinateTransformation(dt, change)
            }
        }.flatten().toSet()

        return pct.mapNotNull { ct ->
            val matchingPoints = points.filter { (p1, p2) -> p1.transform(ct).matches(p2) }
            if (matchingPoints.size >= 12) {
                (selectedCoordinateTransformations + ct) to matchingPoints
            } else {
                null
            }
        }.toMap()
    }

    private fun makeAllPointsRelativeToScanner0(sdl: List<ScannerData>): List<ScannerData> {
        return sdl.map { sd ->
            val updatedPoints = sd.points.map { point ->
                sd.scannerNameToPointTransformation!!.second.fold(point) { p, pt -> p.transform(pt) }
            }

            sd.copy(points = updatedPoints)
        }
    }

    @Test
    fun partTwo() {
        val scannerData = readInput()

        val updatedScannerData = updateScannerDataAddingTransformations(scannerData)
        val scannerPoints = updatedScannerData.map {
            it.scannerNameToPointTransformation!!.second.fold(Point(0,0,0)) { p, pt -> p.transform(pt)}
        }

        val biggestDistance = calculateBiggestDistance(scannerPoints)

        println(biggestDistance) // 19130
    }

    private fun calculateBiggestDistance(pl: List<Point>): Int {
        var result = Int.MIN_VALUE

        for(p1 in pl) {
            for (p2 in pl) {
                val distance = p1.distanceTo(p2)
                if (distance > result) { result = distance }
            }
        }
        return result
    }
}