package com.simon.vpdassesment.features.home.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.simon.vpdassesment.R
import com.simon.vpdassesment.core.CurrencyUtils
import com.simon.vpdassesment.databinding.AccountCardItemBinding
import com.simon.vpdassesment.features.home.model.CardModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class CardPagerAdapter(fragment: Fragment, private val accounts: List<CardModel>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = accounts.size

    override fun createFragment(position: Int): Fragment {

        val fragment = CardFragment()
        fragment.arguments = Bundle().apply {
            putString(CardFragment.ACCOUNT_DETAILS, Json.encodeToString(accounts[position]))
            putInt(CardFragment.INDEX, position)
        }
        return fragment
    }
}


class CardFragment : Fragment() {

    private lateinit var binding: AccountCardItemBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = AccountCardItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ACCOUNT_DETAILS) }?.apply {
            val string = getString(ACCOUNT_DETAILS)
            val index = getInt(INDEX)
            try {
                val details = Json.decodeFromString<CardModel>(string.orEmpty())
                binding.apply {
                    this.amount.text = (CurrencyUtils.longToCurrencyString(details.balance))
                    this.userName.text = details.userName
                    this.accountNumber.text = requireContext().getString(
                        R.string.account_no_template, details.accountNumber
                    )
                }
                if (index == 0) {
                    binding.parent.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.purple_200
                        )
                    )
                }
                if (index == 1) {
                    binding.parent.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.purple_500
                        )
                    )
                }
                if (index == 2) {
                    binding.parent.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.teal_700
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }


    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    companion object {
        const val ACCOUNT_DETAILS = "AccountDetails"
        const val INDEX = "index"
    }
}

