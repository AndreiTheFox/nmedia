package ru.netology.nmedia.viewmodel

import android.app.Application
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

    init {
        loadPosts()
    }
    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError() {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        val editPost = edited.value
        if (editPost != null) {
            edited.value?.let {
                repository.saveAsync(editPost, object : PostCallback<Unit> {
                    override fun onSuccess(posts: Unit) {
                        _postCreated.postValue(Unit)
                    }

                    override fun onError() {
                        _data.postValue(FeedModel(error = true))
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
            override fun onSuccess(posts: Unit) {
                //Ничего не делать
            }

            override fun onError() {
                _data.postValue(_data.value?.copy(posts = old))
//                _data.postValue(FeedModel(error = true))
            }
        })
    }
    fun likePostAsync(post: Post) {
        //val likedPostId = post.id
            _data.postValue(
                _data.value?.copy(posts = getScreenPosts().map {
                    if (it.id != post.id) it else it.copy(
                        likedByMe = !it.likedByMe,
                        likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                    )
                }
                )
            )
            repository.likePostAsync(post,object : PostCallback<Unit> {
                override fun onSuccess(posts: Unit) {
                    //Ничего не делать
                }
                override fun onError() {
                    _data.postValue(FeedModel(error = true))
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
