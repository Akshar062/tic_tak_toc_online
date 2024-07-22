package com.akshar.tiktaktoe

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.akshar.tiktaktoe.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playOffline.setOnClickListener {
            createOfflineGame()
        }

        binding.playOnline.setOnClickListener {
            createOnlineGame()
        }

        binding.joinGame.setOnClickListener {
            joinOnlineGame()
        }
    }

    private fun createOnlineGame() {
        GameData.myId = "X"
        GameData.saveGameModel(GameModel(
            gameStatus = GameStatus.CREATED,
            gameId = Random.nextInt(1000, 9999).toString()
        ))
        startGame()
    }

    private fun joinOnlineGame() {
        var gameId = binding.joinGameCode.text.toString()
        if (gameId.isEmpty()) {
            binding.joinGameCode.error = "Enter Game Code"
            return
        }
        GameData.myId = "O"
        Firebase.firestore.collection("games").document(gameId).get().addOnSuccessListener {
            val gameModel = it?.toObject(GameModel::class.java)
            if (gameModel != null) {
                gameModel.gameStatus = GameStatus.JOINED
                GameData.saveGameModel(gameModel)
                startGame()
            } else {
                binding.joinGameCode.error = "Invalid Game Code"
            }
        }
    }

    private fun createOfflineGame() {
        GameData.saveGameModel(GameModel(
            gameStatus = GameStatus.JOINED
        ))
        startGame()
    }

    private fun startGame() {
        startActivity(Intent(this, GameActivity::class.java))
    }
}