package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.LoadStateBinding
class LoadStateViewHolder(
    private val loadingBinding: LoadStateBinding,
    private val onInteractionListener: PagingLoadStateAdapter.OnInteractionListener,
) : RecyclerView.ViewHolder(loadingBinding.root) {

    fun bind(loadState: LoadState) {
        loadingBinding.apply {
            progress.isVisible = loadState is LoadState.Loading
            retry.isVisible = loadState is LoadState.Error
            retry.setOnClickListener {
                onInteractionListener.onRetry()
            }
        }
    }
}
class PagingLoadStateAdapter(
    private val onInteractionListener: OnInteractionListener
) : LoadStateAdapter<LoadStateViewHolder>() {
    interface OnInteractionListener {
        fun onRetry()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadStateViewHolder = LoadStateViewHolder(
        LoadStateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onInteractionListener
    )

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}