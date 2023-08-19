package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post
import java.lang.RuntimeException

interface PostRepository {
    fun getAll(): List<Post>
    fun getAllAsync (callback : PostCallback<List<Post>>)
    fun saveAsync (post: Post, callback: PostCallback<Post>)
    fun removeByIdAsync(id: Long, callback: PostCallback<Unit>)
    fun likePostAsync(likedPost: Post, callback: PostCallback<Post>)

    interface PostCallback<T>{
        fun onSuccess (result: T)
        fun onError(e:RuntimeException)
    }
}