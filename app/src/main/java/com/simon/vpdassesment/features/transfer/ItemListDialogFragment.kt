package com.simon.vpdassesment.features.transfer

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simon.vpdassesment.databinding.FragmentConfirmTransferBinding
import kotlinx.parcelize.Parcelize


class ConfirmDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentConfirmTransferBinding? = null
    private val binding get() = _binding!!


    companion object {
        private const val ARG_TRANSFER_DETAILS = "transfer_details"

        fun newInstance(transferDetails: TransferDetails): ConfirmDialogFragment {
            val fragment = ConfirmDialogFragment()
            val args = Bundle().apply {
                putParcelable(ARG_TRANSFER_DETAILS, transferDetails)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfirmTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the TransferDetails object from arguments
        val transferDetails = arguments?.getParcelable<TransferDetails>(ARG_TRANSFER_DETAILS)

        // Update the UI with the TransferDetails data
        transferDetails?.let {
            binding.amount.text = it.amount
            binding.destinationAccount.text = it.destinationAccount
            binding.destinationBank.text = it.destinationBank
            binding.destinationUsername.text = it.destinationUsername
            binding.sourceAccount.text = it.sourceAccount
        }

        // Set up button click listeners
        binding.cancel.setOnClickListener {
            // Dismiss the dialog
            dismiss()
        }

        binding.trasnfer.setOnClickListener {

           setFragmentResult("transfer_confirmed", Bundle().apply {
                putBoolean("is_confirmed", true)
            })
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Parcelize
data class TransferDetails(
    val amount: String,
    val destinationAccount: String,
    val destinationBank: String,
    val destinationUsername: String,
    val sourceAccount: String
):Parcelable