package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: ApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
) : PostRepository {
    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            dao.getPagingSource()
        },
        remoteMediator = PostRemoteMediator(
            apiService = apiService,
            postDao = dao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb,
        )
    ).flow
        .map {
        it.map(PostEntity::toDto)
//            .insertSeparators { previous, _ ->
//            if (previous?.id?.rem(5) == 0L) {
//                Ad(
//                    Random.nextLong(),
//                    "https://netology.ru",
//                    "figma.jpg"
//                )
//            } else {
//                null
//            }
//        }
    }

//    override val data = dao.getAll()
//        .map(List<PostEntity>::toDto)
//        .flowOn(Dispatchers.Default)

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            val postWithAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file",
                upload.file.name,
                upload.file.asRequestBody()
            )
            val response = apiService.upload(media)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun updateFeed() {
        dao.updateFeed()
    }

    override fun getNewPostsCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity()
                .map {
                    it.copy(hidden = true)
                })

            val newPostsCount = dao.countUnread()
            emit(newPostsCount)
        }
    }
        .catch { e -> println(e) }
//        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

//    override suspend fun getAll() {
//        try {
//            val response = apiService.getAll()
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.insert(body.toEntity())
//        } catch (e: IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
//    }

    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        try {
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likePost(post: Post) {
        dao.likeById(post.id)
        try {
            val response = if (!post.likedByMe) {
                apiService.likeById(post.id)
            } else {
                apiService.dislikeById(post.id)
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}