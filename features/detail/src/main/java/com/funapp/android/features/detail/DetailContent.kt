package com.funapp.android.features.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import com.funapp.android.platform.ui.components.ErrorView
import com.funapp.android.platform.ui.components.FavoriteButton
import com.funapp.android.platform.ui.components.LoadingIndicator

private sealed class DescriptionBlock {
    data class TextBlock(val text: String) : DescriptionBlock()
    data class CodeBlock(val code: String, val language: String) : DescriptionBlock()
}

private fun parseDescription(text: String): List<DescriptionBlock> {
    val blocks = mutableListOf<DescriptionBlock>()
    val codeBlockRegex = Regex("```(\\w*)\\n([\\s\\S]*?)```")
    var lastIndex = 0

    codeBlockRegex.findAll(text).forEach { match ->
        val beforeCode = text.substring(lastIndex, match.range.first).trim()
        if (beforeCode.isNotEmpty()) {
            blocks.add(DescriptionBlock.TextBlock(beforeCode))
        }
        val language = match.groupValues[1].ifEmpty { "code" }
        val code = match.groupValues[2].trimEnd()
        blocks.add(DescriptionBlock.CodeBlock(code, language))
        lastIndex = match.range.last + 1
    }

    val remaining = text.substring(lastIndex).trim()
    if (remaining.isNotEmpty()) {
        blocks.add(DescriptionBlock.TextBlock(remaining))
    }

    return blocks
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailContent(
    state: DetailState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onGenerateSummary: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    state.item?.let { item ->
                        IconButton(onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, "Check out ${item.title}!")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }
                        FavoriteButton(
                            isFavorite = item.isFavorite,
                            onClick = onFavoriteToggle
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                LoadingIndicator(modifier = Modifier.padding(padding))
            }
            state.error != null -> {
                ErrorView(
                    message = state.error,
                    onRetry = onRefresh,
                    modifier = Modifier.padding(padding)
                )
            }
            state.item != null -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Large title
                    Text(
                        text = state.item.title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category badge (blue pill)
                    if (state.item.category.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = state.item.category,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // "How it's used in this demo" heading
                    Text(
                        text = "How it's used in this demo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rich description content
                    DescriptionContent(
                        description = state.detailedDescription ?: state.item.description
                    )

                    // AI Summary section
                    if (state.showAiSummary) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "AI Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        when {
                            state.isAiSummarizing -> {
                                CircularProgressIndicator()
                            }
                            state.aiSummary != null -> {
                                Text(
                                    text = state.aiSummary,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            else -> {
                                Button(onClick = onGenerateSummary) {
                                    Text("Generate Summary")
                                }
                                if (state.aiSummaryError != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = state.aiSummaryError,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    // iOS equivalent callout
                    if (state.item.iosEquivalent != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "iOS equivalent: ${state.item.iosEquivalent}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DescriptionContent(description: String) {
    val blocks = parseDescription(description)
    blocks.forEach { block ->
        when (block) {
            is DescriptionBlock.TextBlock -> {
                Text(
                    text = block.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            is DescriptionBlock.CodeBlock -> {
                CodeBlockView(code = block.code, language = block.language)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CodeBlockView(code: String, language: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Text(
            text = language,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = code,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.horizontalScroll(rememberScrollState())
        )
    }
}
