package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post
interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun sharePost(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
}