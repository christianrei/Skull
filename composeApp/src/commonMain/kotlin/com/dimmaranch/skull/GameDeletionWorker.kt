package com.dimmaranch.skull

//import androidx.work.OneTimeWorkRequest
//import androidx.work.WorkManager
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import java.util.concurrent.TimeUnit
//
//class GameDeletionWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
//    override fun doWork(): Result {
//        val gameId = inputData.getString("gameId") ?: return Result.failure()
//
//        deleteGameData(gameId)
//        return Result.success()
//    }
//
//    private fun deleteGameData(gameId: String) {
//        val database = Firebase.database
//        val gameRef = database.getReference("games/$gameId")
//
//        gameRef.removeValue()
//            .addOnSuccessListener {
//                Log.d("Game", "Game data successfully deleted.")
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Game", "Error deleting game data", exception)
//            }
//    }
//}
//
//// Schedule work to delete game after 12 hours
//fun scheduleGameDeletion(gameId: String) {
//    val data = workDataOf("gameId" to gameId)
//    val deleteGameRequest = OneTimeWorkRequest.Builder(GameDeletionWorker::class.java)
//        .setInitialDelay(12, TimeUnit.HOURS) // Set delay to 12 hours
//        .setInputData(data)
//        .build()
//
//    WorkManager.getInstance(context).enqueue(deleteGameRequest)
//}
