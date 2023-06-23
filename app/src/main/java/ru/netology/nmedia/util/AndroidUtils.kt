package ru.netology.nmedia.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
    object AndroidUtils {
        fun hideKeyboard(view: View) {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

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