package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post
interface PostRepository {
//    fun getAll(): List<Post>
//    fun likePost(post: Post)
//    fun sharePost(id: Long)
//    fun removeById(id: Long)
//    fun save(post: Post)
    fun getAllAsync (callback : PostCallback<List<Post>>)
    fun saveAsync (post: Post, callback: PostCallback<Unit>)
    fun removeByIdAsync(id: Long, callback: PostCallback<Unit>)
    fun likePostAsync(post: Post, callback: PostCallback<Unit>)

    interface PostCallback<T>{
        fun onSuccess (posts: T)
        fun onError()
    }
}