package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this) { post ->
            //Обновление view при обновлении данных в посте
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likesButton.setImageResource(if (post.likedByMe) R.drawable.liked_red else R.drawable.like_button)
                sharePostButton.setImageResource(if (post.sharedByMe) R.drawable.icon_send_48 else R.drawable.icon_share_48)
                likesCount.text = counterWrite(post.likes)
                sharedCount.text = counterWrite(post.shared)
                viewsCount.text = counterWrite(post.views)
            }
        }

        //Обработчик кнопки поделиться
        binding.sharePostButton.setOnClickListener {
            viewModel.share()
        }
        //Обработчик кнопки лайк
        binding.likesButton.setOnClickListener {
            viewModel.like()
        }

    } //Конец onCreate
}//Конец Main

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