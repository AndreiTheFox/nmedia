package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.dto.Post

class EditPostResultContract: ActivityResultContract<Post, String?>() {

    override fun createIntent(context: Context, input: Post): Intent {
        val intent = Intent(context, EditPostActivity::class.java).apply {
            putExtra("post_text", input.content)
            type = "text/plain"
        }
        return intent
    }
    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        if (resultCode == Activity.RESULT_OK) {
            intent?.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }
}