package com.funapp.android.services.search

import com.funapp.android.model.SearchResult
import com.funapp.android.services.network.NetworkService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DefaultSearchService(
    private val networkService: NetworkService
) : SearchService {

    override fun search(query: String): Flow<SearchResult> = flow {
        val result = networkService.searchItems(query)
        result.onSuccess { searchResult ->
            emit(searchResult)
        }.onFailure {
            emit(SearchResult(query = query, results = emptyList(), totalCount = 0))
        }
    }
}
