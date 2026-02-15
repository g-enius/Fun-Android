package com.funapp.android.services.ai

interface AiService {
    suspend fun isAvailable(): Boolean
    suspend fun summarize(text: String): Result<String>
    fun close() {}
}
