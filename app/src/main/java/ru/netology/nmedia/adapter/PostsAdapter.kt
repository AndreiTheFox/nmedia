package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.counterWrite
import ru.netology.nmedia.util.glideDownloadFullImage
import ru.netology.nmedia.util.glideDownloadRoundImage
import javax.inject.Singleton

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onRemove(post: Post) {}
    fun onEdit(post: Post) {}
    fun onImageClick(post: Post) {}

    //    fun onVideoClick(post: Post) {}
    fun onPostClick(post: Post) {}
}
@Singleton
class PostsAdapter (private val onInteractionListener: OnInteractionListener) :
    PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
    }
}

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

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}