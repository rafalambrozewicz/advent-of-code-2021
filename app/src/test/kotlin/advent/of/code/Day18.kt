package advent.of.code

import org.junit.Test
import java.io.File

class Day18 {

    companion object {
        private const val UNKNOWN = -1
        const val INPUT_FILE_NAME = "day18.txt"
    }

    sealed class Element() {
        fun copy(): Element {
            return when (this) {
                is Node -> this.copy(value = this.value.copy())
                is Leaf -> this.copy(value = this.value, order = this.order)
            }
        }
    }

    data class Node(val value: SnailNumber): Element()
    data class Leaf(val value: Int, var order: Int = UNKNOWN): Element()


    data class SnailNumber(
        val left: Element,
        val right: Element) {

        init {
            var order = 0

            fun setOrder(sn: SnailNumber) {
                if (sn.left is Leaf) {
                    sn.left.order = order++
                } else {
                    setOrder((sn.left as Node).value)
                }

                if (sn.right is Leaf) {
                    sn.right.order = order++
                } else {
                    setOrder((sn.right as Node).value)
                }
            }

            setOrder(this)
        }

        companion object {

            fun parse(s: String): SnailNumber {
                var order = 0

                fun parseInternal(s: String): SnailNumber {
                    val unpacked = s.removePrefix("[").removeSuffix("]")

                    val secondNumberStart: Int
                    val left = if (unpacked.first().isDigit()) {
                        val firstNumberEndIndex = unpacked.indexOfFirst { !it.isDigit() }
                        val firstNumber = if (firstNumberEndIndex != -1) unpacked.substring(0, firstNumberEndIndex).toInt() else unpacked.toInt()
                        secondNumberStart = firstNumberEndIndex + 1
                        Leaf(firstNumber, order++)
                    } else {
                        var index = 0
                        val stack = ArrayDeque<Char>()
                        do {
                            if (unpacked[index] == '[') {
                                stack.addLast(unpacked[index])
                            } else if (unpacked[index] == ']') {
                                stack.removeLast()
                            }
                            index++
                        } while (stack.isNotEmpty())

                        val firstSnailNumber = unpacked.substring(0, index)
                        secondNumberStart = index + 1
                        Node(parse(firstSnailNumber))
                    }

                    val secondPart = unpacked.substring(secondNumberStart)

                    val right = if (secondPart.first().isDigit()) {
                        val secondNumberEndIndex = secondPart.indexOfFirst { !it.isDigit() }
                        val secondNumber = if (secondNumberEndIndex != -1) secondPart.substring(0, secondNumberEndIndex).toInt() else secondPart.toInt()
                        Leaf(secondNumber, order++)
                    } else {
                        var index = 0
                        val stack = ArrayDeque<Char>()
                        do {
                            if (secondPart[index] == '[') {
                                stack.addLast(secondPart[index])
                            } else if (secondPart[index] == ']') {
                                stack.removeLast()
                            }
                            index++
                        } while (stack.isNotEmpty())

                        val secondSnailNumber = secondPart.substring(0, index)
                        Node(parse(secondSnailNumber))

                    }

                    val result = SnailNumber(left, right)
                    return result
                }

                return parseInternal(s)
            }
        }

        operator fun plus(sn: SnailNumber): SnailNumber {
            return SnailNumber(Node(this), Node(sn)).reduce()
        }

        private fun reduce(): SnailNumber {
            var result: Element = Node(this.copy())
            do {
                do {
                    result = explodeIfPossible(result)
                    val explodePossible = (findElemToExplodeOrNull(result) != null)
                } while (explodePossible)

                result = splitIfPossible(result)
                val explodePossible = (findElemToExplodeOrNull(result) != null)
                val splitPossible = (findElemToSplitOrNull(result)!= null)
            } while (explodePossible || splitPossible)

            return (result as Node).value
        }

        private fun explodeIfPossible(element: Element): Element {
            var e = element
            val maybePairToExplode = findElemToExplodeOrNull(element)

            if (maybePairToExplode != null) {
                val leafCount = leafCount(e)

                val (leftValue, rightValue) = Pair((maybePairToExplode.value.left as Leaf).value,
                (maybePairToExplode.value.right as Leaf).value)

                val (leftOrder, rightOrder) = Pair(maybePairToExplode.value.left.order,
                    maybePairToExplode.value.right.order)

                if (leftOrder > 0) {
                    val onLeftElem = findElement(e) { elem, _ ->
                        (elem is Leaf && elem.order == leftOrder - 1)
                    }!!
                    e = replace(e, onLeftElem, (onLeftElem as Leaf).copy(value = onLeftElem.value + leftValue))
                }

                if (rightOrder < (leafCount-1)) {
                    val onRightElement = findElement(e) { elem, _ ->
                        (elem is Leaf && elem.order == rightOrder + 1)
                    }!!
                    e = replace(e, onRightElement, (onRightElement as Leaf).copy(value = onRightElement.value + rightValue))
                }

                e = replace(e, maybePairToExplode, Leaf(0))
            }
            return e
        }

        private fun findElemToExplodeOrNull(element: Element): Node? {
            return findElement(element) { elem, level ->
                elem is Node && elem.value.left is Leaf && elem.value.right is Leaf && level == 4
            } as? Node
        }

        private fun findElement(element: Element, filter: (Element, Int) -> Boolean): Element? {
            var foundElement: Element? = null

            fun findElementInternal(e: Element, filter: (Element, Int) -> Boolean, l: Int) {
                if (foundElement == null) {
                    if (filter.invoke(e, l)) {
                        foundElement = e
                    }

                    if (e is Node) {
                        findElementInternal(e.value.left, filter, l + 1)
                        findElementInternal(e.value.right, filter, l + 1)
                    }
                }
            }

            findElementInternal(element, filter, 0)

            return foundElement
        }

        private fun leafCount(element: Element): Int {
            var right = element

            while (!(right is Leaf)) {
                right = (right as Node).value.right
            }

            return right.order + 1
        }

        private fun replace(element: Element, e1: Element, e2: Element): Element {
            fun replaceInternal(element: Element, e1: Element, e2: Element): Element {
                if (element == e1) {
                    return e2.copy()
                }

                if (element is Leaf) {
                   return element.copy()
                }

                if (element is Node) {
                    val left = replaceInternal(element.value.left, e1, e2)
                    val right = replaceInternal(element.value.right, e1, e2)

                    return Node(SnailNumber(left, right))
                } else {
                    throw IllegalStateException()
                }
            }

            return  replaceInternal(element, e1, e2)
        }

        private fun splitIfPossible(element: Element): Element {
            var e = element
            val maybeLeafToSplit = findElemToSplitOrNull(element)
            if (maybeLeafToSplit != null) {
                val value = maybeLeafToSplit.value
                val rest = value % 2
                val leftValue = value / 2
                val rightValue = value / 2 + rest

                e = replace(e, maybeLeafToSplit, Node(SnailNumber(Leaf(leftValue), Leaf(rightValue))))
            }

            return e
        }

        private fun findElemToSplitOrNull(element: Element): Leaf? {
           return findElement(element) { elem, _ ->
                elem is Leaf && elem.value >= 10
            } as? Leaf
        }

        fun magnitude(): Int {
            fun magnitudeInternal(sn: SnailNumber): Int {
                val left = if (sn.left is Leaf) {
                    sn.left.value * 3
                } else {
                    magnitudeInternal((sn.left as Node).value) * 3
                }

                val right = if (sn.right is Leaf) {
                    sn.right.value * 2
                } else {
                    magnitudeInternal((sn.right as Node).value) * 2
                }

                return (left + right)
            }

            return magnitudeInternal(this)
        }

        override fun toString(): String {
            fun toString(sn: SnailNumber): String {
                val left = if (sn.left is Leaf) {
                    sn.left.value.toString()
                } else {
                    toString((sn.left as Node).value)
                }

                val right = if (sn.right is Leaf) {
                    sn.right.value.toString()
                } else {
                    toString((sn.right as Node).value)
                }

                return "[$left,$right]"
            }

            return toString(this)
        }
    }

    @Test
    fun partOne() {
        val snailNumbers = readInput()

        val first = snailNumbers.first()
        val rest = snailNumbers.drop(1)

        val snailNumbersSum = rest.fold(first) { acc, sn ->
            acc + sn
        }

        val magnitude = snailNumbersSum.magnitude()

        println(magnitude) // 4391
    }

    private fun readInput(): List<SnailNumber> {
        return File("inputs/${INPUT_FILE_NAME}")
            .readLines()
            .map {
                SnailNumber.parse(it)
            }
    }

    @Test
    fun partTwo() {
        val snailNumbers = readInput()

        val snailNumbersPermutations = snailNumbers.map { s1 ->
            val restOfSnailNumbers = snailNumbers - s1
            restOfSnailNumbers.map { s2 -> Pair(s1, s2) }
        }.flatten()

        val magnitudes = snailNumbersPermutations.map { (s1, s2) ->
            (s1 + s2).magnitude()
        }

        val maxMagnitude = magnitudes.maxOrNull()!!

        println(maxMagnitude) // 4626
    }
}
