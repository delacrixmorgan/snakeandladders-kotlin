import kotlin.math.ceil
import kotlin.math.min
import kotlin.random.Random

val padding = 5

fun main() {
    println("Welcome to Kotlin Playground Series - Snake and Ladders \uD83D\uDC0D")
    var command = ""

    while (command != "0") {
        val winnerPlayers = GameEngine.movePlayersAndCheckWinCondition()
        printGameBoard(GameEngine.players)
        printRoundHeader()
        printPlayersBoard(GameEngine.players)

        if (winnerPlayers.isNotEmpty()) {
            println("Congrats ${winnerPlayers.map { it.name }.joinToString(", ")} has won \uD83C\uDF89")
            break
        }
        println("Press any key to continue, 0 to exit..")
        print("Command: ")
        command = readln()
    }
}

private fun printGameBoard(
    players: List<Player>,
    boardSize: Int = 100,
    rows: Int = 10
) {
    val columns = ceil((boardSize.toFloat() / rows.toFloat())).toInt()
    println("-".repeat((padding + 1) * rows + 1))

    for (columnIndex in columns - 1 downTo 0) {
        if (columnIndex % 2 == 0) {
            for (rowIndex in 0 until rows) {
                val step = (columnIndex * rows) + rowIndex + 1
                if (step > boardSize) break
                if (rowIndex == 0) print("|")

                val player = players.firstOrNull { it.step == step }
                if (player != null) {
                    print("${player.marker}${step.toString().padStart(padding - 2, ' ')}|")
                } else {
                    print("${step.toString().padStart(padding, ' ')}|")
                }
            }
        } else {
            for (rowIndex in rows - 1 downTo 0) {
                val step = (columnIndex * rows) + rowIndex + 1
                if (step > boardSize) break
                if (rowIndex == rows - 1) print("|")

                val player = players.firstOrNull { it.step == step }
                if (player != null) {
                    print("${player.marker}${step.toString().padStart(padding - 2, ' ')}|")
                } else {
                    print("${step.toString().padStart(padding, ' ')}|")
                }
            }
        }
        println()
        println("-".repeat((padding + 1) * rows + 1))
    }
}

private fun printRoundHeader() {
    println("| ${"⏲\uFE0F".padStart(padding - 2, ' ')} | Round ${GameEngine.round}")
}

private fun printPlayersBoard(
    players: List<Player>
) {
    players.forEach { player ->
        print("| ${player.marker.padStart(padding - 2, ' ')} | ")
        when (player.status) {
            Status.Normal -> println("${player.name} is currently at Step ${player.step}")
            Status.Snake -> println("${player.name} has been bitten by a Snake and now at Step ${player.step}")
            Status.Ladder -> println("${player.name} has found a ladder and now at Step ${player.step}")
        }
    }
}

// Model
data class Player(
    val name: String,
    val marker: String,
    var step: Int,
    var status: Status = Status.Normal
)

enum class Status {
    Normal,
    Snake,
    Ladder,
}

sealed class Component(val step: Int, val length: Int) {
    class Snake(step: Int, length: Int) : Component(step, length)
    class Ladder(step: Int, length: Int) : Component(step, length)
}

// Engine
object GameEngine {
    var round: Int = 0
    val players = listOf(
        Player("Aerith", "\uD83E\uDD8A", 1),
        Player("Tifa", "\uD83D\uDC35", 1),
        Player("Yuffie", "\uD83E\uDD84", 1),
    )
    val components = listOf(
        Component.Snake(step = 32, length = 12),
        Component.Snake(step = 63, length = 45),
        Component.Snake(step = 88, length = 64),
        Component.Snake(step = 97, length = 78),
        Component.Snake(step = 95, length = 56),
        Component.Ladder(step = 4, length = 10),
        Component.Ladder(step = 8, length = 2),
        Component.Ladder(step = 21, length = 21),
        Component.Ladder(step = 28, length = 48),
        Component.Ladder(step = 50, length = 17),
        Component.Ladder(step = 86, length = 13),
        Component.Ladder(step = 71, length = 21),
    )

    fun movePlayersAndCheckWinCondition(): List<Player> {
        round += 1
        players.forEach { player ->
            val diceRow = Random.nextInt(1, 6)
            val (status, step) = resolvePlayerStepIfNeeded(player.step + diceRow)
            player.status = status
            player.step = min(100, step)
        }
        return players.filter { it.step >= 100 }
    }

    private fun resolvePlayerStepIfNeeded(step: Int): Pair<Status, Int> {
        return when (val component = components.firstOrNull { it.step == step }) {
            is Component.Ladder -> Pair(Status.Ladder, step + component.length)
            is Component.Snake -> Pair(Status.Snake, step - component.length)
            null -> Pair(Status.Normal, step)
        }
    }
}