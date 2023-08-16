package ru.netology.nmedia.activity


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

//        val viewModel: PostViewModel by viewModels(
//            ownerProducer = ::requireParentFragment
//        )

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onPostClick(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply {
                        putLong("postId", post.id)
                    }
                )
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    }
                )
            }

            override fun onRemove(post: Post) {
                viewModel.removeByIdAsync(post.id)
            }

            override fun onLike(post: Post) {
                viewModel.likePostAsync(post)
            }

//            override fun onShare(post: Post) {
//                val intent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    putExtra(Intent.EXTRA_TEXT, post.content)
//                    type = "text/plain"
//                }
//                val shareIntent =
//                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
//                startActivity(shareIntent)
//                viewModel.sharePost(post.id)
//            }

//            override fun onVideoClick(post: Post) {
//                val parsedUri = Uri.parse(post.video).toString().trim()
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(parsedUri))
//                startActivity(intent)
//            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.errorGroup.isVisible = state.error
            binding.empty.isVisible = state.empty
            binding.progress.isVisible = state.loading
        }
        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadPosts()
            binding.swiperefresh.isRefreshing = false
        }
        return binding.root
    }
}//Конец Main
