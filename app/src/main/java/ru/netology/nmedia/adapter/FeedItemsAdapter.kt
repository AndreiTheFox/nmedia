package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
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
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> throw IllegalArgumentException("unknown item type")
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