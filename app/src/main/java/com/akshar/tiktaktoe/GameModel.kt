package com.akshar.tiktaktoe


data class GameModel (
    var gameId : String = "-1",
    var filledPosition : MutableList<String> = mutableListOf("", "", "", "", "", "", "", "", ""),
    var winner : String = "",
    var gameStatus: GameStatus = GameStatus.CREATED,
    var currentPlayer: String = arrayOf("X", "O").random()
)

enum class GameStatus {
    CREATED,
    JOINED,
    IN_PROGRESS,
    FINISHED
}