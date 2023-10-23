package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.LoadStateBinding

class PagingLoadStateViewHolder (
    private val loadingBinding: LoadStateBinding,
    retry: () -> Unit,
) : RecyclerView.ViewHolder(loadingBinding.root) {
    init {
        loadingBinding.retry.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        loadingBinding.progress.isVisible = loadState is LoadState.Loading
        loadingBinding.retry.isVisible = loadState is LoadState.Error
    }
    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): PagingLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.load_state, parent, false)
            val binding = LoadStateBinding.bind(view)
            return PagingLoadStateViewHolder(binding, retry)
        }
    }
}