package ru.netology.nmedia.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private var savedPostText: String = ""
    private val binding: ActivityMainBinding
        get() = _binding!!
    val viewModel: PostViewModel by viewModels()

    private val interactionListener: OnInteractionListener = object : OnInteractionListener {
        override fun onEdit(post: Post) {
            viewModel.edit(post)
        }

        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onShare(post: Post) {
            viewModel.sharePost(post.id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = PostsAdapter(interactionListener)
        binding.list.adapter = adapter
        val editText = binding.content
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.groupSaveRollback.visibility = View.INVISIBLE
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if (!editText.text.isNullOrBlank()) {
                    binding.groupSaveRollback.visibility = View.VISIBLE
                } else binding.groupSaveRollback.visibility = View.INVISIBLE
            }
        })

        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this) { post ->
            if (post.id != 0L) {
                with(binding.content) {
                    requestFocus()
                    setText(post.content)
                    setSelection(post.content.length)
                    savedPostText = post.content.trim()
                }
            }
        }

        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.changeContent(text.toString())
                    viewModel.save()
                    setText("")
                    clearFocus()
                    AndroidUtils.hideKeyboard(this)
                }
            }
        }

        binding.rollbackPostChanges.setOnClickListener {
            viewModel.edited.observe(this) { post ->
                if (post.id == 0L) {
                    binding.content.text =
                        null  //Если поста еще не существует, просто очищаем ввод в null
                } else binding.content.setText(savedPostText) //Иначе - возвращаем текст исходного поста
            }
        }
    }

}//Конец Main

//Функция преобразования входящего целого числа в строку с сокращением записи до вида 1.1К (тысячи) или 1.1М (миллионы)
fun counterWrite(incNumber: Int): String {
    val counterWrite: String = when {
        //Когда входящее число свыше 1 миллиона
        incNumber >= 1000000 -> if (incNumber % 1000000 >= 100) {
            (incNumber / 1000000).toString() + "." + ((incNumber % 1000000) / 100000).toString() + "M "
        } else {    //Когда входящее число от 1 миллиона до 1099999
            (incNumber / 1000).toString() + "M "
        }
        //Когда входящее число свыше 1 тысячи
        (incNumber in 1000..999999) -> if ((incNumber % 1000 >= 100) && (incNumber / 1000 < 10)) {
            (incNumber / 1000).toString() + "." + ((incNumber % 1000) / 100).toString() + "K "
        } else { //Когда входящее число от 1000 до 1099
            (incNumber / 1000).toString() + "K "
        }

        else -> incNumber.toString()
    }
    return counterWrite
}

object AndroidUtils {
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}