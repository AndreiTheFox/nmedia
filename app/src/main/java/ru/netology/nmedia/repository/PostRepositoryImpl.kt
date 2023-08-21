package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post
import kotlin.RuntimeException

class PostRepositoryImpl : PostRepository {
    override fun getAllAsync(callback: PostRepository.PostCallback<List<Post>>) {
        PostApi.service.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(NumberResponseError(response.code()))
                        return
                    }
                    val body = (response.body() ?: run {
                        callback.onError(NumberResponseError(response.code()))
                    }) as List<Post>
                    callback.onSuccess(body)
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })
    }

    override fun saveAsync(post: Post, callback: PostRepository.PostCallback<Post>) {
        PostApi.service.savePost(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(NumberResponseError(response.code()))
                        return
                    }
                    val body = (response.body() ?: run {
                        callback.onError(RuntimeException("response is empty"))
                    }) as Post
                    callback.onSuccess(body)
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.PostCallback<Unit>) {

        PostApi.service.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(NumberResponseError(response.code()))
                        return
                    }
                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })
    }

    override fun likePostAsync(likedPost: Post, callback: PostRepository.PostCallback<Post>) {
        val callbackLikeOrDislike = object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(NumberResponseError(response.code()))
                    return
                }
                val body = (response.body() ?: run {
                    callback.onError(RuntimeException("response is empty"))
                }) as Post
                callback.onSuccess(body)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }
        }
        if (!likedPost.likedByMe) {
            PostApi.service.likePost(likedPost.id)
                .enqueue(callbackLikeOrDislike)
        } else {
            if (likedPost.likes > 0) {
                PostApi.service.unlikePost(likedPost.id)
                    .enqueue(callbackLikeOrDislike)
            } else return
        }
    }
}