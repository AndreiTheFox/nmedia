package ru.netology.nmedia.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.repository.PostRepository.PostCallback
import ru.netology.nmedia.util.SingleLiveEvent
import java.lang.RuntimeException

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
    val toastServerError: Toast = Toast.makeText(
    getApplication(),
    "Ошибка сервера.\nПопробуй еще раз.",
    Toast.LENGTH_SHORT
    )

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostCallback<List<Post>> {
            override fun onSuccess(result: List<Post>) {
                _data.postValue(FeedModel(posts = result, empty = result.isEmpty()))
            }
            override fun onError(e: RuntimeException) {

//                val first = e.message?.substringAfter("status")
//                val second = first?.substring(2, endIndex = 5)
//
//                println(second)

                Toast.makeText(
                    getApplication(),
                    "Ошибка загрузки постов.",
                    Toast.LENGTH_SHORT
                ).show()
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        val editPost = edited.value
        if (editPost != null) {
            edited.value?.let {
                repository.saveAsync(editPost, object : PostCallback<Post> {
                    override fun onSuccess(result: Post) {
                        _postCreated.postValue(Unit)
                    }
                    override fun onError(e: RuntimeException) {
                        toastServerError.show()
   //                     _data.postValue(FeedModel(error = true))
                    }
                }
                )
            }
            edited.value = empty
        }
    }

    fun removeByIdAsync(id: Long) {
        val old = getScreenPosts()
        _data.postValue(
            _data.value?.copy(posts = getScreenPosts()
                .filter { it.id != id }
            )
        )
        repository.removeByIdAsync(id, object : PostCallback<Unit> {
            override fun onSuccess(result: Unit) {
            }

            override fun onError(e: RuntimeException) {
                toastServerError.show()
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

    fun likePostAsync(likedPost: Post) {
        repository.likePostAsync(likedPost, object : PostCallback<Post> {
            override fun onSuccess(result: Post) {
                val updatedPosts = _data.value?.posts?.map {
                    if (it.id == result.id) {
                        result
                    } else it
                }.orEmpty()
                _data.postValue(_data.value?.copy(posts = updatedPosts))
            }

            override fun onError(e: RuntimeException) {
                toastServerError.show()
            }
        })
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

    private fun getScreenPosts(): List<Post> {
        return _data.value?.posts
            ?: throw RuntimeException("List of posts is NULL - something missing")
    }
}
