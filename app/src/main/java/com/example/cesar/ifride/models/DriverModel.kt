package com.example.cesar.ifride.models

import java.util.concurrent.CompletableFuture

open class DriverModel(
    val userReference: String?,
    val cnh: String,
    val carModel: String,
    val carColor: String,
    val plate: String
) {
}