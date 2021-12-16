package advent.of.code

import org.junit.Test
import java.io.File


sealed class Packet(val version: Int, val type: Int)

class LiteralValuePacket(version: Int, type: Int, val value: Long): Packet(version, type)

class OperatorPacket(version: Int, type: Int, val packets: List<Packet>): Packet(version, type)

class Day16 {

    companion object {
        const val INPUT_FILE_NAME = "day16.txt"
        const val LITERAL_TYPE_PACKET_ID = 4

        const val OPERATION_SUM_TYPE_PACKET_ID = 0
        const val OPERATION_PRODUCT_TYPE_PACKET_ID = 1
        const val OPERATION_MINIMUM_TYPE_PACKET_ID = 2
        const val OPERATION_MAXIMUM_TYPE_PACKET_ID = 3
        const val OPERATION_GREATER_THAN_TYPE_PACKET_ID = 5
        const val OPERATION_LESS_THAN_TYPE_PACKET_ID = 6
        const val OPERATION_EQUAL_TYPE_PACKET_ID = 7
    }

    @Test
    fun partOne() {
        val transmissionInBinary = readInput()

        val packets = readPackets(transmissionInBinary)

        val packetVersionSum = sumPacketVersionNumbers(packets)

        println(packetVersionSum) // 901
    }

    private fun readInput(): String {
        return File("inputs/$INPUT_FILE_NAME")
            .readLines().joinToString(separator = "") { l ->
                l.toCharArray().joinToString(separator = "") { c ->
                    when {
                        c.isDigit() -> (c - '0').toString(radix = 2).padStart(4, '0')
                        c.isLetter() -> ((c - 'A') + 10).toString(radix = 2).padStart(4, '0')
                        else -> throw IllegalArgumentException("Illegal char of '$c' in input")
                    }
                }
            }
    }

    private fun readPackets(transmissionInBinary: String): List<Packet> {

        fun readPacketsRecursive(start: Int): Pair<Packet, Int> {
            val version = transmissionInBinary.substring(start, start + 3).toInt(radix = 2)
            val type = transmissionInBinary.substring(start + 3, start + 6).toInt(radix = 2)

            when (type) {
                LITERAL_TYPE_PACKET_ID -> {
                    var literalContentStart = start + 6
                    var number = ""
                    do {
                        val chunk = transmissionInBinary.substring(literalContentStart, literalContentStart + 5)
                        number += chunk.drop(1)
                        literalContentStart += 5

                    } while (chunk.startsWith('1'))

                    return Pair(LiteralValuePacket(version, type, number.toLong(radix = 2)), literalContentStart)
                }
                else -> {
                    val lengthTypeId = transmissionInBinary.substring(start + 6, start + 7)

                    when (lengthTypeId) {
                        "0" -> {
                            val totalLengthOfSubPackets = transmissionInBinary.substring(start + 7, start + 7 + 15)
                                .toInt(radix = 2)

                            val subPacketTransmissionString = transmissionInBinary.substring(start + 7 + 15,
                                start + 7 + 15 + totalLengthOfSubPackets)

                            val subPackets = readPackets(subPacketTransmissionString)

                            return Pair(OperatorPacket(version, type, subPackets), start + 7 + 15 + totalLengthOfSubPackets)

                        }
                        "1" -> {
                            val totalCountOfSubPackets = transmissionInBinary.substring(start + 7, start + 7 + 11)
                                .toInt(radix = 2)

                            val subPackets = mutableListOf<Packet>()
                            var subPacketsStartPosition = start + 7 + 11
                            for (s in 0 until totalCountOfSubPackets) {
                                val (packet, endPosition) = readPacketsRecursive(subPacketsStartPosition)
                                subPackets.add(packet)
                                subPacketsStartPosition = endPosition
                            }

                            return Pair(OperatorPacket(version, type, subPackets), subPacketsStartPosition)
                        }
                        else -> throw IllegalArgumentException("Illegal length type id of '$lengthTypeId")
                    }
                }
            }
        }

        var startPosition = 0
        val packets = mutableListOf<Packet>()
        do {
            val (packet, endPosition) = readPacketsRecursive(startPosition)
            packets.add(packet)
            startPosition = endPosition

            val remainingString = transmissionInBinary.substring(startPosition, transmissionInBinary.length)
        } while (remainingString.isNotEmpty() && remainingString.any { c -> c != '0' })

        return packets
    }

    private fun sumPacketVersionNumbers(packets: List<Packet>): Int {
        fun sumPacketVersionNumbersRecursive(p: Packet): Int {
            return when(p) {
                is LiteralValuePacket -> p.version
                is OperatorPacket -> (p.version + p.packets.map { ip -> sumPacketVersionNumbersRecursive(ip) }.sum())
            }
        }

        return packets.map { p -> sumPacketVersionNumbersRecursive(p) }.sum()
    }

    @Test
    fun partTwo() {
        val transmissionInBinary = readInput()

        val packets = readPackets(transmissionInBinary)

        val valueOfTransmission = valueOfTransmission(packets.first())

        println(valueOfTransmission) // 110434737925
    }

    private fun valueOfTransmission(packet: Packet): Long {

        fun valueOfPacketRecursive(p: Packet): Long {
            return when(p) {
                is LiteralValuePacket -> p.value
                is OperatorPacket -> when (p.type) {
                    OPERATION_SUM_TYPE_PACKET_ID -> {
                        p.packets.map { valueOfPacketRecursive(it) }.sum()
                    }
                    OPERATION_PRODUCT_TYPE_PACKET_ID -> {
                        p.packets.map { valueOfPacketRecursive(it) }.fold(1L) { acc, v -> acc * v}
                    }
                    OPERATION_MINIMUM_TYPE_PACKET_ID -> {
                        p.packets.map { valueOfPacketRecursive(it) }.min()!!
                    }
                    OPERATION_MAXIMUM_TYPE_PACKET_ID -> {
                        p.packets.map { valueOfPacketRecursive(it) }.max()!!
                    }
                    OPERATION_GREATER_THAN_TYPE_PACKET_ID -> {
                        val (p1, p2) = p.packets.map { valueOfPacketRecursive(it) }
                            .let { it.first() to it.last() }

                        if (p1 > p2) 1L else 0L
                    }
                    OPERATION_LESS_THAN_TYPE_PACKET_ID -> {
                        val (p1, p2) = p.packets.map { valueOfPacketRecursive(it) }
                            .let { it.first() to it.last() }

                        if (p1 < p2) 1L else 0L
                    }
                    OPERATION_EQUAL_TYPE_PACKET_ID -> {
                        val (p1, p2) = p.packets.map { valueOfPacketRecursive(it) }
                            .let { it.first() to it.last() }

                        if (p1 == p2) 1L else 0L
                    }

                    else -> { throw IllegalArgumentException("Unknown operation of '${p.type}")}
                }
            }
        }

        return valueOfPacketRecursive(packet)
    }
}