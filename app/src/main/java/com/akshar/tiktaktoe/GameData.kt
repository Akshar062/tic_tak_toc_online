package com.akshar.tiktaktoe

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object GameData {
    private val _gameModel : MutableLiveData<GameModel> = MutableLiveData()
    var gameModel : MutableLiveData<GameModel> = _gameModel
    var myId = ""

    fun saveGameModel(gameModel: GameModel) {
        _gameModel.postValue(gameModel)
        if (gameModel.gameId != "-1") {
            Firebase.firestore.collection("games").document(gameModel.gameId).set(gameModel)
        }
    }

    fun fetchGameModel() {
        gameModel.value?.apply {
            if (gameId != "-1") {
                Firebase.firestore.collection("games").document(gameId).addSnapshotListener { value, error ->
                    if (error == null) {
                        val model = value?.toObject(GameModel::class.java)
                        if (model != null) {
                            _gameModel.postValue(model)
                        }
                    }
                }
            }
        }
    }
}
