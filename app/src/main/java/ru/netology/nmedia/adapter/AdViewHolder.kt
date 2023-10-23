package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.util.load

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