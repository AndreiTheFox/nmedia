package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post

class PostRepositorySQLiteImpl(private val dao: PostDao) : PostRepository {
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun save(post: Post) {
      //  val id = post.id
       //val saved = dao.save(post)
        dao.save(post)
        posts = dao.getAll()
//        posts = if (id == 0L) {
//            listOf(saved) + posts
//        } else {
//            posts.map {
//                if (it.id != id) it else saved
//            }
//        }
        data.value = posts
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
        posts = dao.getAll()
//        posts = posts.map {
//            if (it.id != id) it else it.copy(
//                likedByMe = !it.likedByMe,
//                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
//            )
//        }
        data.value = posts
    }

    override fun sharePost(id: Long) {
        dao.sharePost(id)
        posts = dao.getAll()
        data.value = posts
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
        posts = dao.getAll()
        data.value = posts
    }


}