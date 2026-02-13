package com.funapp.android.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ViewCarousel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.funapp.android.model.Item
import com.funapp.android.platform.ui.components.ErrorView
import com.funapp.android.platform.ui.components.FavoriteButton
import com.funapp.android.platform.ui.components.LoadingIndicator
import com.funapp.android.platform.ui.theme.itemColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeContent(
    state: HomeState,
    onRefresh: () -> Unit,
    onItemClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading && state.carouselPages.isEmpty() && state.allItems.isEmpty() -> {
                LoadingIndicator(modifier = Modifier.padding(padding))
            }
            state.error != null -> {
                ErrorView(
                    message = state.error,
                    onRetry = onRefresh,
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                PullToRefreshBox(
                    isRefreshing = state.isLoading,
                    onRefresh = onRefresh,
                    modifier = Modifier.padding(padding)
                ) {
                    if (state.carouselEnabled) {
                        CarouselView(
                            pages = state.carouselPages,
                            onItemClick = onItemClick,
                            onFavoriteToggle = onFavoriteToggle
                        )
                    } else {
                        CarouselDisabledView()
                    }
                }
            }
        }
    }
}

@Composable
private fun CarouselView(
    pages: List<List<Item>>,
    onItemClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit
) {
    if (pages.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { pages.size })

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Featured",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) { pageIndex ->
                val pageItems = pages[pageIndex]
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    pageItems.forEach { item ->
                        CarouselCard(
                            item = item,
                            onItemClick = { onItemClick(item.id) },
                            onFavoriteToggle = { onFavoriteToggle(item.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pageItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Page indicator dots
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == pagerState.currentPage)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        Color.Gray.copy(alpha = 0.4f)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CarouselDisabledView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ViewCarousel,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Text(
                text = "Carousel Disabled",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Enable the carousel from Settings",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CarouselCard(
    item: Item,
    onItemClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = itemColor(item.iconColor)

    Card(
        modifier = modifier
            .clickable(onClick = onItemClick),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.subtitle.isNotEmpty()) {
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                FavoriteButton(
                    isFavorite = item.isFavorite,
                    onClick = onFavoriteToggle
                )
            }
        }
    }
}
