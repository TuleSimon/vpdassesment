package com.simon.vpdassesment.features.signup

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.simon.vpdassesment.databinding.FragmentRegisterUserBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAccountFragment : Fragment() {

    private var _binding: FragmentRegisterUserBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val createAccountViewmodel by viewModels<CreateAccountViewmodel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
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
                createAccountViewmodel.createAccountState.collect { state ->
                    val email = state.email
                    val name = state.name
                    val verifypassword = state.verifypassword
                    val password = state.password
                    val emailError = state.error
                    val nameError = state.errorname
                    val passwordError = state.errorPassword
                    val verifypasswordError = state.errorverifyPassword
                    val currentEmail = binding.emailText.text.toString()
                    val currentPassword = binding.passwordText.text.toString()
                    val currentName = binding.fullnameText.text.toString()
                    val currentverifyPassword = binding.verifypasswordText.text.toString()
                    if (currentEmail != email) {
                        binding.emailText.setText(email)
                    }
                    if (name != currentName) {
                        binding.fullnameText.setText(email)
                    }
                    if (currentPassword != password) {
                        binding.passwordText.setText(password)
                    }
                    if (currentverifyPassword != verifypassword) {
                        binding.verifypasswordText.setText(password)
                    }
                    binding.apply {
                        emailParent.error = emailError
                        emailParent.isErrorEnabled = emailError != null
                        passwordParent.error = passwordError
                        passwordParent.isErrorEnabled = passwordError != null
                        verifypasswordParent.error = verifypasswordError
                        verifypasswordParent.isErrorEnabled = verifypasswordError != null
                        fullnameParent.error = nameError
                        fullnameParent.isErrorEnabled = nameError != null
                    }
                    binding.createAccountButton.isEnabled =state.isValid()

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
        binding.emailText.addTextChangedListener {
            createAccountViewmodel.updateEmail(it.toString())
        }
        binding.passwordText.addTextChangedListener {
            createAccountViewmodel.updatePassword(it.toString())
        }
        binding.fullnameText.addTextChangedListener {
            createAccountViewmodel.updateName(it.toString())
        }
        binding.verifypasswordText.addTextChangedListener {
            createAccountViewmodel.updateVerifyPassword(it.toString())
        }
        binding.createAccountButton.setOnClickListener {
            createAccountViewmodel.register(onError = {
                requireContext().toast(it)
            }) {
                findNavController().navigate(R.id.navigation_home)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun Context.toast(text:Int){
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Context.toast(text:String){
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}