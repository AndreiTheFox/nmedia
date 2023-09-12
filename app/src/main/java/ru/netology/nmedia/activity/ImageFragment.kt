package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.util.glideDownloadFullImage

class ImageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentImageBinding.inflate(
            inflater,
            container,
            false
        )
        val serverPathUrl = "${BuildConfig.BASE_URL}"
        val attachmentsUrl = "${serverPathUrl}/media"
        val attachmentUrl = arguments?.getString("attachUrl")
        val downloadAttachUrl = "${attachmentsUrl}/${attachmentUrl}"
        glideDownloadFullImage(downloadAttachUrl, binding.imageAttachment)
        binding.navBack.setOnClickListener{
            findNavController().navigateUp()
        }
        return binding.root
    }
}
