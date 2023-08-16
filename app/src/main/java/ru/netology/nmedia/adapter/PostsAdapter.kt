package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.counterWrite
import ru.netology.nmedia.util.glideDownloadFullImage
import ru.netology.nmedia.util.glideDownloadRoundImage

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onRemove(post: Post) {}
    fun onEdit(post: Post) {}
//    fun onVideoClick(post: Post) {}
    fun onPostClick(post: Post) {}
}

class PostsAdapter(private val onInteractionListener: OnInteractionListener) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    private val serverPathUrl = "http://10.0.2.2:9999/"
    private val avatarsPathUrl = "${serverPathUrl}/avatars"
    private val attachmentsUrl = "${serverPathUrl}/images"
    fun bind(post: Post) {
        binding.apply {
            val downloadAvatarUrl = "${avatarsPathUrl}/${post.authorAvatar}"

            if (post.attachment != null) {
                val downloadAttachUrl = "${attachmentsUrl}/${post.attachment.url}"
                glideDownloadFullImage(downloadAttachUrl, binding.attachment)
            }
            else {
                binding.attachment.visibility = View.GONE
            }
//            Glide.with(avatar)
//                .load(downloadAvatarUrl)
//                .placeholder(R.drawable.ic_loading_24)
//                .error(R.drawable.ic_error_24)
//                .timeout(10_000)
//                .centerInside()
//                .centerCrop()
//                .circleCrop()
//                .into(avatar)
            glideDownloadRoundImage(downloadAvatarUrl,binding.avatar)


            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.text = counterWrite(post.likes)
            sharePostButton.text = counterWrite(post.shares)
            viewsButton.text = counterWrite(post.views)
            like.isChecked = post.likedByMe
            sharePostButton.isChecked = post.sharedByMe
            sharePostButton.isCheckable = !post.sharedByMe

//            if (post.video.isNullOrBlank()) {
//                video.visibility = View.GONE
//                videoUrl.visibility = View.GONE
//                playVideo.visibility = View.GONE
//            } else {
//                videoUrl.text = post.video
//            }

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
                onInteractionListener.onLike(post)
            }

            sharePostButton.setOnClickListener {
                onInteractionListener.onShare(post)
            }
//            video.setOnClickListener {
//                onInteractionListener.onVideoClick(post)
//            }
//            playVideo.setOnClickListener {
//                onInteractionListener.onVideoClick(post)
//            }
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