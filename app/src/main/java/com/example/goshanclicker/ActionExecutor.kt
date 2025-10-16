package com.example.goshanclicker

import android.util.Log
import gamebot.GameBotGrpc
import gamebot.Gamebot
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

class ActionExecutor() {

    fun perform(x: Float, y: Float, duration: Int) {

        /*
        // gRPC-запрос к Python-серверу
        val channel: ManagedChannel = ManagedChannelBuilder
            .forAddress("127.0.0.1", 50051)
            .usePlaintext()
            .build()

        try {
            val stub = GameBotGrpc.newBlockingStub(channel)

            val request = Gamebot.Action.newBuilder()
                .setX(x)
                .setY(y)
                .setDuration(duration)
                .build()

            //val response = stub.sendAction(request)
            val response = Gamebot.ActionResponse.newBuilder()
                .setStatus("success").build()

            Log.i("ActionExecutor", "Ответ от сервера: ${response.status}")

            // Выполняем клик через AccessibilityService
            //service.performClick(x, y)

        } catch (e: Exception) {
            Log.e("ActionExecutor", "Ошибка gRPC запроса: ${e.message}", e)
        } finally {
            channel.shutdown()
        }
         */
    }
}
