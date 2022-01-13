package advent.of.code

import org.junit.Test

class Day23 {

    companion object {
        val PART_ONE_INPUT = arrayOf(
            charArrayOf('.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',),
            charArrayOf('#', '#', 'D', '#', 'C', '#', 'B', '#', 'C', '#', '#',),
            charArrayOf('#', '#', 'D', '#', 'A', '#', 'A', '#', 'B', '#', '#',),
        )

        val PART_TWO_INPUT = arrayOf(
            charArrayOf('.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',),
            charArrayOf('#', '#', 'D', '#', 'C', '#', 'B', '#', 'C', '#', '#',),

            charArrayOf('#', '#', 'D', '#', 'C', '#', 'B', '#', 'A', '#', '#',),
            charArrayOf('#', '#', 'D', '#', 'B', '#', 'A', '#', 'C', '#', '#',),

            charArrayOf('#', '#', 'D', '#', 'A', '#', 'A', '#', 'B', '#', '#',),
        )
    }

    data class Pos(val x: Int, val y: Int)

    @JvmInline
    value class State(val s: ULong) {
        companion object {

            private val POW_OF_FIVE = listOf(1UL, 5UL, 25UL, 125UL, 625UL, 3125UL, 15625UL, 78125UL, 390625UL, 1953125UL,
                9765625UL, 48828125UL, 244140625UL, 1220703125UL, 6103515625UL, 30517578125UL, 152587890625UL, 762939453125UL,
                3814697265625UL, 19073486328125UL, 95367431640625UL, 476837158203125UL, 2384185791015625UL, 11920928955078125UL,
                59604644775390625UL, 298023223876953125UL, 1490116119384765625UL)

            private val POW_OF_FIVE_DESC = POW_OF_FIVE.reversed()

            private val SPACE_TO_VALUE = mapOf(
                '.' to 0UL,
                'A' to 1UL,
                'B' to 2UL,
                'C' to 3UL,
                'D' to 4UL,
            )

            private val VALUE_TO_SPACE = mapOf(
                0UL to '.',
                1UL to 'A',
                2UL to 'B',
                3UL to 'C',
                4UL to 'D',
            )

            private val EMPTY_SMALL_BOARD = arrayOf(
                charArrayOf('.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',),
                charArrayOf('#', '#', '.', '#', '.', '#', '.', '#', '.', '#', '#',),
                charArrayOf('#', '#', '.', '#', '.', '#', '.', '#', '.', '#', '#',),
            )

            val SMALL_BOARD_POS = listOf(
                Pos(0, 0), Pos(1, 0), Pos(3, 0), Pos(5, 0), Pos(7, 0), Pos(9, 0), Pos(10, 0),
                Pos(2, 1), Pos(4, 1), Pos(6, 1), Pos(8, 1),
                Pos(2, 2), Pos(4, 2), Pos(6, 2), Pos(8, 2))

            private val EMPTY_BIG_BOARD = arrayOf(
                charArrayOf('.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',),
                charArrayOf('#', '#', '.', '#', '.', '#', '.', '#', '.', '#', '#',),
                charArrayOf('#', '#', '.', '#', '.', '#', '.', '#', '.', '#', '#',),
                charArrayOf('#', '#', '.', '#', '.', '#', '.', '#', '.', '#', '#',),
                charArrayOf('#', '#', '.', '#', '.', '#', '.', '#', '.', '#', '#',),
            )

            val BIG_BOARD_POS = listOf(
                Pos(0, 0), Pos(1, 0), Pos(3, 0), Pos(5, 0), Pos(7, 0), Pos(9, 0), Pos(10, 0),
                Pos(2, 1), Pos(4, 1), Pos(6, 1), Pos(8, 1),
                Pos(2, 2), Pos(4, 2), Pos(6, 2), Pos(8, 2),
                Pos(2, 3), Pos(4, 3), Pos(6, 3), Pos(8, 3),
                Pos(2, 4), Pos(4, 4), Pos(6, 4), Pos(8, 4))

            fun from(ca: Array<CharArray>): State {
                val result = if (ca.size == EMPTY_BIG_BOARD.size) {
                    BIG_BOARD_POS.map { (x, y) -> ca[y][x]}
                } else {
                    SMALL_BOARD_POS.map { (x, y) -> ca[y][x]}
                }.foldIndexed(0UL) {index, acc, char ->
                        val spaceValue = SPACE_TO_VALUE[char]!! * POW_OF_FIVE[index]
                        acc + spaceValue
                }

                return State(result)
            }
        }

        fun toCharArray(): Array<CharArray> {
            val chars = mutableListOf<Char>()
            var n = s
            POW_OF_FIVE_DESC.forEach { divisor ->
                chars.add(
                    VALUE_TO_SPACE[ n / divisor ]!!
                )
                n %= divisor
            }

            val noOfLetters = chars.count { it.isLetter() }
            if (noOfLetters > 8) {
                val charsReversed = chars.reversed()
                val result = Array(EMPTY_BIG_BOARD.size) { y -> CharArray(EMPTY_BIG_BOARD.first().size) { x -> EMPTY_BIG_BOARD[y][x] } }
                BIG_BOARD_POS.forEachIndexed { index, (x, y) ->
                    result[y][x] = charsReversed[index]
                }

                return result
            } else {
                val charsReversed = chars.reversed().dropLast(8)
                val result = Array(EMPTY_SMALL_BOARD.size) { y -> CharArray(EMPTY_SMALL_BOARD.first().size) { x -> EMPTY_SMALL_BOARD[y][x] } }
                SMALL_BOARD_POS.forEachIndexed { index, (x, y) ->
                    result[y][x] = charsReversed[index]
                }

                return result
            }
        }
    }

    data class StateAndCost(val s: State, val c: Int)

    data class SmartState(private val s: StateAndCost) {

        companion object {
            val RESTRICTED_HALL_POSITIONS = listOf(2, 4, 6, 8)
            val AMPHIPODS = listOf('A', 'B', 'C', 'D')
            val AMPHIPODS_ROOM_X = mapOf(
                'A' to 2,
                'B' to 4,
                'C' to 6,
                'D' to 8,
            )

            val AMPHIPODS_MOVING_COSTS = mapOf(
                'A' to 1,
                'B' to 10,
                'C' to 100,
                'D' to 1000,
            )
        }

        private val board: Array<CharArray> = s.s.toCharArray()

        private val enterableRooms = AMPHIPODS.map { amphipod ->
            amphipod to roomCouldBeEntered(amphipod, AMPHIPODS_ROOM_X[amphipod]!!)
        }.toMap()

        private val organisedRooms = AMPHIPODS.map { amphipod ->
            amphipod to isRoomOrganised(amphipod, AMPHIPODS_ROOM_X[amphipod]!!)
        }.toMap()

        val isOrganized = organisedRooms.all { (_, isOrganized) -> isOrganized }

        private fun roomCouldBeEntered(amphipod: Char, x: Int): Boolean {
            return (1 until board.size).all { y ->
                board[y][x] == amphipod || board[y][x] == '.'
            }
        }

        private fun isRoomOrganised(amphipod: Char, x: Int): Boolean {
            return (1 until board.size).all { y -> board[y][x] == amphipod }
        }

        fun generateNextStates(): List<StateAndCost> {
            val amphipodPos = (if (board.size > 3) State.BIG_BOARD_POS else State.SMALL_BOARD_POS)
                .mapNotNull { pos -> if (board[pos.y][pos.x].isLetter()) pos else null }

            val listOfStatesAndCosts: List<List<StateAndCost>> = amphipodPos.map { pos ->
                val (x, y) = pos
                val amphipod = board[y][x]
                val isInValidRoom = isInValidRoom(amphipod, pos)

                val hasMoved = (y == 0)
                val roomCouldBeEntered = enterableRooms[amphipod]!!

                when {
                    isInValidRoom -> {
                        /* already set in correct place case */
                        emptyList()
                    }
                    (hasMoved && roomCouldBeEntered) -> {
                        /* from hallway to valid room case */
                        val pathNotBlocked =
                            (minOf(x, AMPHIPODS_ROOM_X[amphipod]!!)..maxOf(x, AMPHIPODS_ROOM_X[amphipod]!!)).map {
                                board[0][it]
                            }.any { it != '.' }

                        if (pathNotBlocked) {
                            val endPositionsToCost = findPathToRoom(amphipod, pos, s.c)
                            endPositionsToCost.map { (endPos, cost) ->
                                StateAndCost(
                                    State.from(board.copy().swap(pos, endPos)),
                                    cost
                                )
                            }
                        } else {
                            emptyList()
                        }
                    }
                    (!hasMoved) -> {
                        /* from invalid room to hallway case */
                        val endPositionsToCost = findPathToHall(amphipod, pos, s.c)
                        endPositionsToCost.map { (endPos, cost) ->
                            StateAndCost(
                                State.from(board.copy().swap(pos, endPos)),
                                cost
                            )
                        }
                    }
                    else -> emptyList()
                }
            }


            return listOfStatesAndCosts.flatten()
        }

        private fun isInValidRoom(amphipod: Char, p: Pos): Boolean {
            val isInRoom = (1 until board.size).map { y -> Pos(AMPHIPODS_ROOM_X[amphipod]!!, y) }.contains(p)

            if (isInRoom) {
                val roomsBelowWithAmphipods = (1 until board.size)
                    .map { y -> Pos(AMPHIPODS_ROOM_X[amphipod]!!, y) }
                    .filter { (_, y) -> y > p.y }
                    .all { (x, y) -> board[y][x] == amphipod }

                return roomsBelowWithAmphipods
            }

            return false
        }

        private fun findPathToRoom(amphipod: Char, p: Pos, initCost: Int): List<Pair<Pos, Int>> {
            val visited = mutableListOf<Pos>()
            val endPosWithCost = mutableListOf<Pair<Pos, Int>>()

            fun findPathToRoomRec(amphipod: Char, p: Pos, cost: Int) {
                visited.add(p)

                listOf(
                    p.copy(y = p.y + 1),
                    p.copy(x = p.x - 1),
                    p.copy(x = p.x + 1),
                )
                    .filter { isValid(it) }
                    .filter { !visited.contains(it) }
                    .forEach { pos ->
                        if (isInValidRoom(amphipod, pos)) {
                            endPosWithCost.add(pos to cost + AMPHIPODS_MOVING_COSTS[amphipod]!!)
                            return
                        }
                        findPathToRoomRec(amphipod, pos, cost + AMPHIPODS_MOVING_COSTS[amphipod]!!)
                    }
            }

            findPathToRoomRec(amphipod, p, initCost)

            return endPosWithCost
        }

        private fun findPathToHall(amphipod: Char, p: Pos, initCost: Int): List<Pair<Pos, Int>> {
            val visited = mutableListOf<Pos>()
            val endPosWithCost = mutableListOf<Pair<Pos, Int>>()

            fun findPathToWallRec(amphipod: Char, p: Pos, cost: Int) {
                visited.add(p)

                listOf(
                    p.copy(y = p.y - 1),
                    p.copy(x = p.x - 1),
                    p.copy(x = p.x + 1),
                )
                    .filter { isValid(it) }
                    .filter { !visited.contains(it) }
                    .forEach { pos ->
                        if (!RESTRICTED_HALL_POSITIONS.contains(pos.x)) {
                            endPosWithCost.add(pos to cost + AMPHIPODS_MOVING_COSTS[amphipod]!!)
                        }

                        findPathToWallRec(amphipod, pos, cost + AMPHIPODS_MOVING_COSTS[amphipod]!!)
                    }
            }

            findPathToWallRec(amphipod, p, initCost)

            return endPosWithCost
        }

        private fun isValid(p: Pos): Boolean {
            return  p.y >= 0 && p.y < board.size &&
                    p.x >= 0 && p.x < board.first().size &&
                    board[p.y][p.x] == '.'

        }

        private fun Array<CharArray>.copy(): Array<CharArray> {
            return  Array(this.size) { y -> CharArray(this.first().size) { x -> this[y][x] } }
        }

        private fun Array<CharArray>.swap(p1: Pos, p2: Pos): Array<CharArray> {
            val temp = this[p1.y][p1.x]
            this[p1.y][p1.x] = this[p2.y][p2.x]
            this[p2.y][p2.x] = temp
            return this
        }
    }

    @Test
    fun partOne() {
        val visitedStates = sortedSetOf<State>( compareBy { it.s } )

        val statesToVisit = sortedSetOf<StateAndCost>( compareBy { it.s.s } )
        statesToVisit.add(StateAndCost(State.from(PART_ONE_INPUT), 0))

        while (statesToVisit.isNotEmpty()) {
            val minCost = statesToVisit.minOf { it.c }

            val currentStates = statesToVisit.filter { it.c == minCost }
            visitedStates.addAll(currentStates.map { it.s })
            statesToVisit.removeAll(currentStates.toSet())

            val currentSmartStates = currentStates.map { stateAndCost -> SmartState(stateAndCost) }

            if (currentSmartStates.any {it.isOrganized} ) {
                println(minCost) // 19059
                break
            }

            currentSmartStates.map { it.generateNextStates() }
                .flatten()
                .forEach { newStateAndCost ->
                    val maybeInQueue = statesToVisit.find { it.s == newStateAndCost.s }
                    if (maybeInQueue == null) {
                        statesToVisit.add(newStateAndCost)
                    } else if (maybeInQueue.c > newStateAndCost.c) {
                        statesToVisit.remove(maybeInQueue)
                        statesToVisit.add(newStateAndCost)
                    }
                }
        }
    }

    @Test
    fun partTwo() {
        val visitedStates = sortedSetOf<State>( compareBy { it.s } )

        val statesToVisit = sortedSetOf<StateAndCost>( compareBy { it.s.s } )
        statesToVisit.add(StateAndCost(State.from(PART_TWO_INPUT), 0))

        while (statesToVisit.isNotEmpty()) {
            val minCost = statesToVisit.minOf { it.c }

            val currentStates = statesToVisit.filter { it.c == minCost }
            visitedStates.addAll(currentStates.map { it.s })
            statesToVisit.removeAll(currentStates.toSet())

            val currentSmartStates = currentStates.map { stateAndCost -> SmartState(stateAndCost) }

            if (currentSmartStates.any {it.isOrganized} ) {
                println(minCost) // 48541
                break
            }

            currentSmartStates.map { it.generateNextStates() }
                .flatten()
                .forEach { newStateAndCost ->
                    val maybeInQueue = statesToVisit.find { it.s == newStateAndCost.s }
                    if (maybeInQueue == null) {
                        statesToVisit.add(newStateAndCost)
                    } else if (maybeInQueue.c > newStateAndCost.c) {
                        statesToVisit.remove(maybeInQueue)
                        statesToVisit.add(newStateAndCost)
                    }
                }
        }
    }
}