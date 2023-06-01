package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = listOf(
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likes = 9999,
            shared = 9999,
            views = 2031,
            likedByMe = false,
            sharedByMe = false
        ),
        Post(
            id = 2,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Это второй пост",
            published = "21 мая в 18:36",
            likes = 9999,
            shared = 9999,
            views = 2031,
            likedByMe = false,
            sharedByMe = false
        ),
        Post(
            id = 3,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Это третий пост",
            published = "21 мая в 18:36",
            likes = 9999,
            shared = 9999,
            views = 2031,
            likedByMe = false,
            sharedByMe = false
        ),
        Post(
            id = 4,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Это четвертый пост",
            published = "21 мая в 18:36",
            likes = 9999,
            shared = 9999,
            views = 2031,
            likedByMe = false,
            sharedByMe = false
        ),
        Post(
            id = 5,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Это пятый пост",
            published = "21 мая в 18:36",
            likes = 99,
            shared = 99,
            views = 99,
            likedByMe = false,
            sharedByMe = false
        ),
        Post(
            id = 6,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Это шестой пост",
            published = "21 мая в 18:36",
            likes = 999,
            shared = 999,
            views = 16,
            likedByMe = false,
            sharedByMe = false
        ),
        Post(
            id = 7,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Это седьмой пост",
            published = "21 мая в 18:36",
            likes = 10999,
            shared = 15,
            views = 9999,
            likedByMe = false,
            sharedByMe = false
        )

    )
    private val data = MutableLiveData(posts)
    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if(it.id!=id) it else it.copy(likedByMe = !it.likedByMe, likes = if (it.likedByMe) it.likes - 1 else it.likes +1)
        }
        saveData()
    }

    override fun sharePost(id: Long) {
        posts = posts.map {
            if(it.id!=id) it else it.copy(sharedByMe = true, shared = it.shared + 1)
        }
        saveData()
    }

    private fun saveData() {
        data.value = posts
    }
}