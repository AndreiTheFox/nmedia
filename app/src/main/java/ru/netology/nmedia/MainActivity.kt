package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

val post = Post(
    id = 1,
    author = "Нетология. Университет интернет-профессий будущего",
    content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
    published = "21 мая в 18:36",
    likes = 9999,
    shared = 9999,
    views = 2031,
    likedByMe = false,
    sharedByMe = false
)

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Инициализация views данными из поста
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            if (post.likedByMe) {
                likesButton.setImageResource(R.drawable.liked_red)
            }
            likesCount.text = counterWrite(post.likes)
            sharedCount.text = counterWrite(post.shared)
            viewsCount.text = counterWrite(post.views)

            //Нажатие кнопки лайк
           likesButton.setOnClickListener {
                post.likedByMe = !post.likedByMe
                likesCount.text = updateLikesAndCounter()
            }
            //Нажатие кнопки поделиться
            sharePostButton.setOnClickListener {
                sharedCount.text = updateSharedPostState()
            }
        }
    }

    //Обработчик вызова нажатия кнопки лайка
    private fun updateLikesAndCounter(): String {
        //По нажатию кнопки инкремент лайков и проверка состояния likedByMe
        if (post.likedByMe) {
            binding.likesButton.setImageResource(R.drawable.liked_red)
            post.likes++
        } else {
            binding.likesButton.setImageResource(R.drawable.like_button)
            post.likes--
        }
        return counterWrite(post.likes)
    }

    //Обработчик вызова нажатия кнопки поделиться
    private fun updateSharedPostState(): String {
        post.sharedByMe = true
        post.shared++
        binding.sharePostButton.setImageResource(R.drawable.icon_send_48)
        return counterWrite(post.shared)

    }
}

//Функция преобразования входящего целого числа в строку с сокращением записи до вида 1.1К (тысячи) или 1.1М (миллионы)
fun counterWrite(incNumber: Int): String {
    val counterWrite: String = when {
        //Когда входящее число свыше 1 миллиона
        incNumber >= 1000000 -> if (incNumber % 1000000 >= 100) {
            (incNumber / 1000000).toString() + "." + ((incNumber % 1000000) / 100000).toString() + "M"
        } else {    //Когда входящее число от 1 миллиона до 1099999
            (incNumber / 1000).toString() + "M"
        }
        //Когда входящее число свыше 1 тысячи
        (incNumber in 1000..999999) -> if ((incNumber % 1000 >= 100) && (incNumber / 1000 < 10)) {
            (incNumber / 1000).toString() + "." + ((incNumber % 1000) / 100).toString() + "K"
        } else { //Когда входящее число от 1000 до 1099
            (incNumber / 1000).toString() + "K"
        }

        else -> incNumber.toString()
    }
    return counterWrite
}