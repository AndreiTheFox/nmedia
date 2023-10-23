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
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.counterWrite
import ru.netology.nmedia.util.glideDownloadFullImage
import ru.netology.nmedia.util.glideDownloadRoundImage
import ru.netology.nmedia.util.load
import javax.inject.Singleton

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onRemove(post: Post) {}
    fun onEdit(post: Post) {}
    fun onImageClick(post: Post) {}
    fun onAdClick(ad: Ad) {}
    fun onPostClick(post: Post) {}
}

@Singleton
class FeedAdapter(
    private val onInteractionListener: OnInteractionListener
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> throw IllegalArgumentException("unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.card_ad -> {
                val binding = CardAdBinding.inflate(layoutInflater, parent, false)
                AdViewHolder(binding, onInteractionListener)
            }

            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(layoutInflater, parent, false)
                PostViewHolder(binding, onInteractionListener)
            }

            else -> throw IllegalArgumentException("unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> {
                (holder as? AdViewHolder)?.bind(item)
            }
            is Post -> {
                (holder as? PostViewHolder)?.bind(item)
            }
            null -> {
                error("unknown item type")
            }
        }
    }

//    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
//        val post = getItem(position) ?: return
//        holder.bind(post)
//    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
        binding.apply {
            image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
            image.setOnClickListener {
                onInteractionListener.onAdClick(ad)
            }
        }
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

class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}