package com.simon.vpdassesment.core

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import com.simon.vpdassesment.R
import com.simon.vpdassesment.databinding.FragmentDialogBinding

class LoadingDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var animation: Animation

    companion object {
        private const val KEY_PROMPT = "KEY_PROMPT"

        fun newInstance(prompt: String? = null): LoadingDialogFragment {
            return LoadingDialogFragment().apply {
                arguments = Bundle().apply {
                    if (!prompt.isNullOrEmpty()) putString(KEY_PROMPT, prompt)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.LoadingDialog)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogBinding.inflate(inflater, container, false)
        arguments?.getString(KEY_PROMPT)?.let { prompt ->
            if (prompt.isNotBlank()) binding.tvLoading.text = prompt
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
