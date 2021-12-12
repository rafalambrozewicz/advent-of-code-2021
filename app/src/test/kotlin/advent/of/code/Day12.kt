package advent.of.code

import org.junit.Test
import java.io.File

class Day12 {

    class Graph {
        private val nodes = mutableListOf<Node>()

        private class Node(val name: String) {

            companion object {
                const val START_CAVE_NAME = "start"
                const val END_CAVE_NAME = "end"

                val RESERVED_NAMES = listOf(START_CAVE_NAME, END_CAVE_NAME)
            }

            val connections = mutableListOf<Node>()

            fun isStartCave() = name == START_CAVE_NAME
            fun isEndCave() = name == END_CAVE_NAME

            fun isBigCave() = (name.toUpperCase() == name) && (!RESERVED_NAMES.contains(name))
            fun isSmallCave() = (name.toLowerCase() == name) && (!RESERVED_NAMES.contains(name))

            fun addConnection(n: Node) {
                connections.removeIf { it.name == n.name }
                connections.add(n)
            }
        }

        fun addConnection(n1n: String, n2n: String) {
            val n1 = nodes.find { it.name == n1n } ?: Node(n1n)
            val n2 = nodes.find { it.name == n2n } ?: Node(n2n)

            n1.addConnection(n2)
            n2.addConnection(n1)

            nodes.removeIf { it.name == n1n }
            nodes.removeIf { it.name == n2n }
            nodes.add(n1)
            nodes.add(n2)
        }

        fun findAllPaths(): List<String> {
            val paths = mutableListOf<String>()
            fun findAllPathsRecursive(n: Node, path: String, visited: List<String>) {
                val newPath = if(path.isEmpty()) n.name else path + "-${n.name}"
                val newVisited = visited + n.name

                val cavesToVisit = n.connections.filter {
                    it.isBigCave() || (it.isSmallCave() && !visited.contains(it.name)) || it.isEndCave()
                }

                if (n.isEndCave()) {
                    paths.add(newPath)
                } else {
                    cavesToVisit.forEach { nn ->
                        findAllPathsRecursive(nn, newPath, newVisited)
                    }
                }
            }

            val startNode = nodes.find { it.isStartCave() }!!
            findAllPathsRecursive(startNode, "", emptyList())

            return paths
        }

        fun findAllPathsV2(): List<String> {
            val paths = mutableListOf<String>()
            fun findAllPathsRecursive(n: Node, path: String, visited: List<String>) {
                val newPath = if(path.isEmpty()) n.name else path + "-${n.name}"
                val newVisited = visited + n.name
                val maybeSmallCaveVisitedTwice = newVisited.filter { it.toLowerCase() == it }
                    .groupBy { it }
                    .filterValues { it.size == 2 }
                    .values
                    .firstOrNull()
                    ?.firstOrNull()

                val cavesToVisit = n.connections.filter {
                    it.isBigCave() ||
                    (it.isSmallCave() && !visited.contains(it.name)) ||
                    (it.isSmallCave() && visited.contains(it.name) && maybeSmallCaveVisitedTwice == null) ||
                    it.isEndCave()
                }

                if (n.isEndCave()) {
                    paths.add(newPath)
                } else {
                    cavesToVisit.forEach { nn ->
                        findAllPathsRecursive(nn, newPath, newVisited)
                    }
                }
            }

            val startNode = nodes.find { it.isStartCave() }!!
            findAllPathsRecursive(startNode, "", emptyList())

            return paths
        }
    }

    @Test
    fun partOne() {
        val caveConnections = File("inputs/day12.txt")
            .readLines()
            .map { l ->
                l.split("-")[0] to l.split("-")[1]
            }

        val cave = Graph().apply {
            caveConnections.forEach { (n1n, n2n) ->
                this.addConnection(n1n, n2n)
            }
        }

        val paths = cave.findAllPaths()

        println(paths.size) // 5958
    }

    @Test
    fun partTwo() {
        val caveConnections = File("inputs/day12.txt")
            .readLines()
            .map { l ->
                l.split("-")[0] to l.split("-")[1]
            }

        val cave = Graph().apply {
            caveConnections.forEach { (n1n, n2n) ->
                this.addConnection(n1n, n2n)
            }
        }

        val paths = cave.findAllPathsV2()

        println(paths.size) // 150426
    }
}