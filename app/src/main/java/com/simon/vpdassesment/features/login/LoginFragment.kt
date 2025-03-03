package com.simon.vpdassesment.features.login

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.simon.vpdassesment.R
import com.simon.vpdassesment.core.LoadingDialogFragment
import com.simon.vpdassesment.databinding.FragmentLoginBinding
import com.simon.vpdassesment.features.signup.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    val dialog by lazy {
        LoadingDialogFragment()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                loginViewModel.loginState.collect { state ->
                    val email = state.email
                    val password = state.password
                    val emailError = state.error
                    val passwordError = state.errorPassword
                    val currentEmail = binding.emailEdittext.text.toString()
                    val currentPassword = binding.passwordEdttext.text.toString()
                    if (currentEmail != email) {
                        binding.emailEdittext.setText(email)
                    }
                    if (currentPassword != password) {
                        binding.passwordEdttext.setText(password)
                    }
                    binding.apply {
                        parentEmail.error = emailError
                        parentEmail.isErrorEnabled = emailError != null
                        parentPassword.error = passwordError
                        parentPassword.isErrorEnabled = passwordError != null
                    }
                    binding.loginButton.isEnabled =
                        emailError == null && passwordError == null && email.isNotEmpty() && password.isNotEmpty()

                    if (state.loading) {
                        if (dialog.isVisible.not()) {
                            dialog.show(parentFragmentManager, "loading_dialog")
                        }
                    } else {
                        if (dialog.isVisible) {
                            dialog.dismiss()
                        }
                    }
                }
            }
        }

    }

    private fun initViews() {
        binding.emailEdittext.addTextChangedListener {
            loginViewModel.updateEmail(it.toString())
        }
        binding.passwordEdttext.addTextChangedListener {
            loginViewModel.updatePassword(it.toString())
        }
        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.sign_up)
        }
        binding.loginButton.setOnClickListener {
            loginViewModel.login(onError = {
                requireContext().toast(it)
            }) {
                findNavController().navigate(R.id.action_login_fragment_to_navigation_home)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
