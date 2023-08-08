package ru.netology.nmedia.repository
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import ru.netology.nmedia.dto.Post
//
//class PostRepositoryInMemoryImpl : PostRepository {
//    private var nextId = 1L
//    private var posts = listOf(
//        Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
//            published = "21 мая в 18:36",
//            likes = 9999,
//            shares = 9999,
//            views = 2031,
//            likedByMe = false,
//            sharedByMe = false,
//            video = "https://www.youtube.com/watch?v=kdxT-kY0DTQ"
//        ),
//        Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Это второй пост",
//            published = "21 мая в 18:36",
//            likes = 9999,
//            shares = 9999,
//            views = 2031,
//            likedByMe = false,
//            sharedByMe = false,
//        video = "https://vk.com/video-39915550_456242702"
//        ),
//        Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Это третий пост",
//            published = "21 мая в 18:36",
//            likes = 9999,
//            shares = 9999,
//            views = 2031,
//            likedByMe = false,
//            sharedByMe = false
//        ),
//        Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Это четвертый пост",
//            published = "21 мая в 18:36",
//            likes = 9999,
//            shares = 9999,
//            views = 2031,
//            likedByMe = false,
//            sharedByMe = false
//        ),
//        Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Это пятый пост",
//            published = "21 мая в 18:36",
//            likes = 99,
//            shares = 99,
//            views = 99,
//            likedByMe = false,
//            sharedByMe = false
//        ),
//        Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Это шестой пост",
//            published = "21 мая в 18:36",
//            likes = 999,
//            shares = 999,
//            views = 16,
//            likedByMe = false,
//            sharedByMe = false
//        ),
//        Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Это седьмой пост",
//            published = "21 мая в 18:36",
//            likes = 10999,
//            shares = 15,
//            views = 9999,
//            likedByMe = false,
//            sharedByMe = false
//        )
//
//    )
//    private val data = MutableLiveData(posts)
//    override fun getAll(): LiveData<List<Post>> = data
//    override fun likeById(id: Long) {
//        posts = posts.map {
//            if (it.id != id) it else it.copy(
//                likedByMe = !it.likedByMe,
//                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
//            )
//        }
//        saveData()
//    }
//
//
//    override fun sharePost(id: Long) {
//
//        posts = posts.map {
//            if (it.id != id) it else it.copy(sharedByMe = true, shares = it.shares + 1)
//        }
//        saveData()
//    }
//
//    override fun save(post: Post) {
//        posts = if (post.id == 0L) {
//            listOf(
//                post.copy(
//                    id = nextId++,
//                    author = "Me",
//                    published = "now"
//                )
//            ) + posts
//        } else {
//            posts.map {
//                if (it.id != post.id) it else it.copy(content = post.content)
//            }
//        }
//        saveData()
//    }
//
//    override fun removeById(id: Long) {
//        posts = posts.filter { it.id != id }
//        saveData()
//    }
//
//    private fun saveData() {
//        data.value = posts
//    }
//}