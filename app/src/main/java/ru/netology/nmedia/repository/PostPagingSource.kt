package ru.netology.nmedia.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError
import java.io.IOException

class PostPagingSource(
    private val apiService: ApiService
) : PagingSource<Long, Post>() {
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val response = when (params) {
                is LoadParams.Refresh -> {
                    apiService.getLatest(params.loadSize)
                }

                is LoadParams.Append -> apiService.getBefore(
                    id = params.key,
                    count = params.loadSize
                )

                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(),
                    nextKey = null,
                    prevKey = params.key
                )
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(),response.message())
            }
            val body = response.body().orEmpty()
            val nextKey = body.lastOrNull()?.id
            return LoadResult.Page(body, prevKey = params.key, nextKey)
        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }

}