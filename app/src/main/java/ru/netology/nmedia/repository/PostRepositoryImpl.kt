package ru.netology.nmedia.repository

import androidx.lifecycle.*
import okio.IOException
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.*

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getAll().map(List<PostEntity>::toDto)

    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
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
            val response = PostsApi.service.removeById(id)
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
            val response = if (!post.likedByMe) {PostsApi.service.likeById(post.id)} else {PostsApi.service.dislikeById(post.id)}
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

//    override fun likePostAsync(likedPost: Post, callback: PostRepository.PostCallback<Post>) {
//        val callbackLikeOrDislike = object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                if (!response.isSuccessful) {
//                    callback.onError(NumberResponseError(response.code()))
//                    return
//                }
//                val body = (response.body() ?: run {
//                    callback.onError(RuntimeException("response is empty"))
//                }) as Post
//                callback.onSuccess(body)
//            }
//



//            override fun onFailure(call: Call<Post>, t: Throwable) {
//                callback.onError(RuntimeException(t))
//            }
//        }
//        if (!likedPost.likedByMe) {
//            PostApi.service.likePost(likedPost.id)
//                .enqueue(callbackLikeOrDislike)
//        } else {
//            if (likedPost.likes > 0) {
//                PostApi.service.unlikePost(likedPost.id)
//                    .enqueue(callbackLikeOrDislike)
//            } else return
//        }
//    }

}