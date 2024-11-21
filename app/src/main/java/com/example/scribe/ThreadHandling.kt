package com.example.scribe

import kotlinx.coroutines.*

// Base
suspend fun launchMain(block: suspend CoroutineScope.() -> Unit) {
    withContext(Dispatchers.Main) {
        block()
    }
}

// Base
suspend fun launchDefault(block: suspend CoroutineScope.() -> Unit) {
    withContext(Dispatchers.Default) {
        block()
    }
}