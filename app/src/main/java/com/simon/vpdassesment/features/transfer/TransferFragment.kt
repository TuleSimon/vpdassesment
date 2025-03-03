package com.simon.vpdassesment.features.transfer

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
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.navigation.fragment.findNavController
import com.simon.vpdassesment.R
import com.simon.vpdassesment.core.CurrencyUtils
import com.simon.vpdassesment.core.LoadingDialogFragment
import com.simon.vpdassesment.core.addSafeArea
import com.simon.vpdassesment.databinding.FragmentRegisterUserBinding
import com.simon.vpdassesment.databinding.FragmentTransferBinding
import com.simon.vpdassesment.features.signup.CreateAccountViewmodel
import com.simon.vpdassesment.features.signup.toast
import com.skydoves.powerspinner.DefaultSpinnerAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class TransferFragment : Fragment() {

    private var _binding: FragmentTransferBinding? = null


    private val binding get() = _binding!!

    val transferViewModel by viewModels<TransferViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransferBinding.inflate(inflater, container, false)
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

    var set = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.addSafeArea()
        initViews()
        observeTransferState()
        observeUserAccounts()
        observeSelectedAccount()
        setupFragmentResultListener()
    }

    private fun observeTransferState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transferViewModel.transferState.collectLatest { state ->
                    Napier.e { "Here state $state" }
                    withContext(Dispatchers.Main) {
                        updateBanksSpinner(state)
                        updateSubmitButton(state)
                        binding.parentAmount.error=state.transferError
                        binding.parentAmount.isErrorEnabled=state.transferError!=null
                        handleTransferringState(state)
                    }
                }
            }
        }
    }




    private fun updateBanksSpinner(state: TransferState) {
        if (state.banks.isNotEmpty() && !set) {
            set = true
            binding.banksSpinner.setSpinnerAdapter(DefaultSpinnerAdapter(binding.banksSpinner))
            binding.banksSpinner.setViewTreeLifecycleOwner(viewLifecycleOwner)
            binding.banksSpinner.setItems(state.banks.map { it.name })

            binding.banksSpinner.setOnSpinnerItemSelectedListener<String> { _, _, newIndex, _ ->
                state.banks.getOrNull(newIndex)?.code?.let { code ->
                    transferViewModel.updateDestinationBankId(code)
                }
            }
        }
    }


    private  fun updateSubmitButton(state: TransferState) {

        binding.submit.isEnabled = state.isValid()
        if (state.isValid()) {
            binding.submit.setOnClickListener {
                val transferDetails = TransferDetails(
                    amount = CurrencyUtils.longToCurrencyString((state.amount  ?: 0L)),
                    destinationAccount = state.destinationAccountNumber.toString().orEmpty(),
                    destinationBank = state.banks.firstOrNull { it.code == state.destinationBankId }?.name.orEmpty(),
                    destinationUsername = "Tule Simon Joseph",
                    sourceAccount = state.sourceAccountNumber.toString()
                )
                val dialogFragment = ConfirmDialogFragment.newInstance(transferDetails)
                dialogFragment.show(parentFragmentManager, "ConfirmDialogFragment")
            }
        }
    }

    private fun handleTransferringState(state: TransferState) {
        if (state.transferrring) {
            if (!dialog.isVisible) {
                dialog.show(parentFragmentManager, "loading_dialog")
            }
        } else {
            if (dialog.isVisible) {
                dialog.dismiss()
            }
        }
    }

    private fun observeUserAccounts() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                transferViewModel.userAccounts.collect { accounts ->
                    if (accounts.isNotEmpty()) {
                        binding.selectSource.setSpinnerAdapter(DefaultSpinnerAdapter(binding.selectSource))
                        binding.selectSource.setViewTreeLifecycleOwner(viewLifecycleOwner)
                        binding.selectSource.setItems(accounts.map { it.accountNumber.toString() })
                        binding.selectSource.selectItemByIndex(0)
                        transferViewModel.updateSourceAccountNumber(0)
                        binding.selectSource.setOnSpinnerItemSelectedListener<String> { _, _, newIndex, _ ->
                            transferViewModel.updateSourceAccountNumber(newIndex)
                        }
                    }
                }
            }
        }
    }

    private fun observeSelectedAccount() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                transferViewModel.selectedAccount.collect { account ->
                    if (account != null) {
                        binding.card.apply {
                            amount.text = CurrencyUtils.longToCurrencyString(account.balance)
                            userName.text = account.userName
                            accountNumber.text = account.accountNumber.toString()
                            date.text = getFormattedDate()
                        }
                    }
                }
            }
        }
    }

    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener("transfer_confirmed", viewLifecycleOwner) { _, bundle ->
            val isConfirmed = bundle.getBoolean("is_confirmed", false)
            if (isConfirmed) {
                transferViewModel.transferFunds(
                    onError = { requireContext().toast("Transfer failed") },
                    onSuccess = {
                        requireContext().toast("Transfer successful")
                        findNavController().popBackStack()
                    }
                )
            }
        }
    }
    private fun initViews() {
        binding.amountEdittext.addTextChangedListener {
            transferViewModel.updateAmount(if (it.isNullOrEmpty()) 0L else it.toString().toLong())
        }
        binding.descEdittext.addTextChangedListener {
            transferViewModel.updateDescription(it.toString())
        }
        binding.accountNumberEdittext.addTextChangedListener {
            transferViewModel.updateDestinationAccountNumber(
                if (it.isNullOrEmpty()) 0L else it.toString().toLong()
            )
        }


        binding.submit.setOnClickListener {
            transferViewModel.transferFunds(onError = {
                requireContext().toast(it)
            }) {
                findNavController().navigate(R.id.action_sign_up_to_navigation_home)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


fun getFormattedDate(): String {
    // Get today's date using Calendar
    val calendar = Calendar.getInstance()

    // Extract day, month, and year
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH) // Note: Calendar.MONTH is zero-based (0 = January)
    val year = calendar.get(Calendar.YEAR)

    // Add ordinal suffix to the day (e.g., 1st, 2nd, 3rd, 4th, etc.)
    val dayWithSuffix = when (day) {
        1, 21, 31 -> "${day}st"
        2, 22 -> "${day}nd"
        3, 23 -> "${day}rd"
        else -> "${day}th"
    }

    // Format the month name (e.g., September)
    val monthName = SimpleDateFormat("MMMM", Locale.ENGLISH).format(calendar.time)

    // Return the formatted date
    return "$dayWithSuffix $monthName $year"
}


fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}