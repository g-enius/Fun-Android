package com.funapp.android.services.search

import com.funapp.android.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchService {
    fun search(query: String): Flow<SearchResult>
}
