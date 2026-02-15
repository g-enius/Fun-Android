package com.funapp.android.services.ai

import android.content.Context
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.summarization.Summarization
import com.google.mlkit.genai.summarization.SummarizationRequest
import com.google.mlkit.genai.summarization.Summarizer
import com.google.mlkit.genai.summarization.SummarizerOptions
import com.google.mlkit.genai.summarization.SummarizerOptions.InputType
import com.google.mlkit.genai.summarization.SummarizerOptions.Language
import com.google.mlkit.genai.summarization.SummarizerOptions.OutputType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultAiService(private val context: Context) : AiService {

    private var summarizerInitialized = false
    private val summarizer: Summarizer by lazy {
        summarizerInitialized = true
        val options = SummarizerOptions.builder(context)
            .setInputType(InputType.ARTICLE)
            .setOutputType(OutputType.ONE_BULLET)
            .setLanguage(Language.ENGLISH)
            .build()
        Summarization.getClient(options)
    }

    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val status = summarizer.checkFeatureStatus().get()
            status == FeatureStatus.AVAILABLE
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun summarize(text: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = SummarizationRequest.builder(text).build()
            val result = summarizer.runInference(request).get()
            Result.success(result.getSummary())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun close() {
        if (summarizerInitialized) {
            summarizer.close()
        }
    }
}
