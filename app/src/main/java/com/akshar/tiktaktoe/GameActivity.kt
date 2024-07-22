package com.akshar.tiktaktoe

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akshar.tiktaktoe.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private var gameModel : GameModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GameData.fetchGameModel()

        GameData.gameModel.observe(this) {
            gameModel = it
            setUi()
        }

        binding.startGame.setOnClickListener {
            startGame()
        }
    }

    private fun setUi() {
        gameModel?.apply {
            binding.button1.text = filledPosition[0]
            binding.button2.text = filledPosition[1]
            binding.button3.text = filledPosition[2]
            binding.button4.text = filledPosition[3]
            binding.button5.text = filledPosition[4]
            binding.button6.text = filledPosition[5]
            binding.button7.text = filledPosition[6]
            binding.button8.text = filledPosition[7]
            binding.button9.text = filledPosition[8]

            binding.startGame.visibility = View.VISIBLE

            binding.gameStatus.text =
                when (gameStatus) {
                    GameStatus.CREATED -> {
                        binding.startGame.visibility = View.INVISIBLE
                        "GameID: $gameId"
                    }
                    GameStatus.JOINED -> "Click on Start Game"
                    GameStatus.IN_PROGRESS -> {
                        binding.startGame.visibility = View.INVISIBLE
                        when(GameData.myId){
                            currentPlayer -> "Your Turn"
                            else -> "Opponent Turn"
                        }
                    }
                    GameStatus.FINISHED -> if (winner.isNotEmpty()) {
                        when(GameData.myId){
                            winner -> "You Won"
                            else -> "Opponent Won"
                        }
                    } else "Game Draw"
                }
        }
    }

    fun onButtonClick(view: View) {
        val buttonSelected = view as Button
        var cellID = 0
        when (buttonSelected.id) {
            R.id.button1 -> cellID = 1
            R.id.button2 -> cellID = 2
            R.id.button3 -> cellID = 3
            R.id.button4 -> cellID = 4
            R.id.button5 -> cellID = 5
            R.id.button6 -> cellID = 6
            R.id.button7 -> cellID = 7
            R.id.button8 -> cellID = 8
            R.id.button9 -> cellID = 9
        }
        gameModel?.apply {

            if (gameStatus == GameStatus.CREATED || gameStatus == GameStatus.JOINED) {
                Toast.makeText(this@GameActivity, "Start Game First", Toast.LENGTH_SHORT).show()
                return
            }

            if (gameId == "-1" && currentPlayer == GameData.myId) {
                Toast.makeText(this@GameActivity, "It is not your turn", Toast.LENGTH_SHORT).show()
                return
            }
            if (gameStatus == GameStatus.IN_PROGRESS && filledPosition[cellID - 1].isEmpty()) {
                filledPosition[cellID - 1] = currentPlayer
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                updateGameData(
                    GameModel(
                        gameId = gameId,
                        filledPosition = filledPosition,
                        winner = winner,
                        gameStatus = gameStatus,
                        currentPlayer = currentPlayer
                    )
                )
            } else {
                Toast.makeText(this@GameActivity, "Invalid Move", Toast.LENGTH_SHORT).show()
            }
        }
        checkForWinner()
    }

    private fun checkForWinner(){
        val winningPositions = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )
        gameModel?.apply {
            for (i in winningPositions.indices) {
                if (filledPosition[winningPositions[i][0]] == filledPosition[winningPositions[i][1]] &&
                    filledPosition[winningPositions[i][1]] == filledPosition[winningPositions[i][2]] &&
                    filledPosition[winningPositions[i][0]].isNotEmpty()
                ) {
                    winner = filledPosition[winningPositions[i][0]]

                    gameStatus = GameStatus.FINISHED
                    updateGameData(
                        GameModel(
                            gameId = gameId,
                            filledPosition = filledPosition,
                            winner = winner,
                            gameStatus = gameStatus,
                            currentPlayer = currentPlayer
                        )
                    )
                    return
                }
            }
            if (filledPosition.none { it.isEmpty() }) {
                gameStatus = GameStatus.FINISHED
                updateGameData(
                    GameModel(
                        gameId = gameId,
                        filledPosition = filledPosition,
                        winner = winner,
                        gameStatus = gameStatus,
                        currentPlayer = currentPlayer
                    )
                )
            }
        }
    }

    private fun startGame() {
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.IN_PROGRESS,
                )
            )
        }
    }

    private fun updateGameData(model: GameModel) {
        GameData.saveGameModel(model)
    }
}