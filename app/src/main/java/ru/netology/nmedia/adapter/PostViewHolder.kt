package ru.netology.nmedia.adapter

import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.counterWrite
import ru.netology.nmedia.util.glideDownloadFullImage
import ru.netology.nmedia.util.glideDownloadRoundImage

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    private val serverPathUrl = "${BuildConfig.BASE_URL}"
    private val avatarsPathUrl = "${serverPathUrl}/avatars"
    private val attachmentsUrl = "${serverPathUrl}/media"
    fun bind(post: Post) {
        binding.apply {
            val downloadAvatarUrl = "${avatarsPathUrl}/${post.authorAvatar}"
            if (post.attachment != null) {
                val downloadAttachUrl = "${attachmentsUrl}/${post.attachment.url}"
                glideDownloadFullImage(downloadAttachUrl, binding.attachment)
                binding.attachment.visibility = View.VISIBLE
            } else {
                binding.attachment.visibility = View.GONE
            }
            glideDownloadRoundImage(downloadAvatarUrl, binding.avatar)
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.text = counterWrite(post.likes)
            sharePostButton.text = counterWrite(post.shares)
            viewsButton.text = counterWrite(post.views)
            like.isChecked = post.likedByMe

            menu.isVisible = post.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                like.isChecked = !like.isChecked
                onInteractionListener.onLike(post)
            }

            sharePostButton.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            attachment.setOnClickListener {
                onInteractionListener.onImageClick(post)
            }

            root.setOnClickListener {
                onInteractionListener.onPostClick(post)
            }
        }
    }
}