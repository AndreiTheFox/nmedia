package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import java.lang.RuntimeException
import kotlin.concurrent.thread

private val empty = Post(id = 0)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            // Начинаем загрузку
            _data.postValue(FeedModel(loading = true))
            try {
                // Данные успешно получены
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                // Получена ошибка
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun save() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likePost(post: Post) {
        val id = post.id
        thread {
            _data.postValue(
                _data.value?.copy(posts = getScreenPosts().map {
                    if (it.id != id) it else it.copy(
                        likedByMe = !it.likedByMe,
                        likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                    )
                }
                )
            )
            repository.likePost(post)
        }
    }

    fun removeById(id: Long) {
        thread {
            val old = getScreenPosts()
            _data.postValue(
                _data.value?.copy(posts = getScreenPosts()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    private fun getScreenPosts(): List<Post> {
        return _data.value?.posts
            ?: throw RuntimeException("List of posts is NULL - something missing")
    }
}
