package com.simon.vpdassesment.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.simon.vpdassesment.R
import com.simon.vpdassesment.core.addSafeArea
import com.simon.vpdassesment.databinding.FragmentHomeBinding
import com.simon.vpdassesment.features.home.adapter.CardPagerAdapter
import com.simon.vpdassesment.features.home.adapter.TransactionAdapter
import com.simon.vpdassesment.features.transfer.gone
import com.simon.vpdassesment.features.transfer.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val homeViewModel by viewModels<HomeViewModel>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCardAdapter()
        binding.root.addSafeArea()
        binding.sendMoney.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_transfer_fragment)
        }
    }

    private fun setUpCardAdapter() {

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                homeViewModel.accounts.collect { accounts ->
                    if (accounts.isNotEmpty()) {
                        if (binding.cardsViewPager.adapter?.itemCount == null &&
                            (binding.cardsViewPager.adapter?.itemCount ?: 0) < 1
                        ) {
                            binding.cardsViewPager.config()
                            val adapter = CardPagerAdapter(this@HomeFragment, accounts)
                            binding.cardsViewPager.adapter = adapter
                            binding.cardsViewPager.setPageTransformer(MarginPageTransformer(40));
                        }
                    }
                }
            }
        }

    }


    private fun setUpTransactions() {
        val adapter = TransactionAdapter()
        binding.transactionsRv.layoutManager = LinearLayoutManager(context)
        binding.transactionsRv.adapter = adapter
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.transactions.collectLatest {
                    if(it.isNotEmpty()){
                        withContext(Dispatchers.Main) {
                            binding.transactionsRv.visible()
                            binding.empty.gone()
                            adapter.updateList(it)
                        }
                    }
                    else{
                        withContext(Dispatchers.Main){
                            binding.transactionsRv.gone()
                            binding.empty.visible()
                        }
                    }
                }
            }
        }
    }

    fun ViewPager2.config() {
        offscreenPageLimit = 1
        val recyclerView = getChildAt(0) as RecyclerView
        recyclerView.apply {
            val screenWidth = resources.displayMetrics.widthPixels
            val pageWidth = (screenWidth * 0.85f).toInt()
            val padding = (screenWidth - pageWidth) / 2
            // Set padding on the inner RecyclerView
            setPadding(padding, 0, padding, 0)

            clipToPadding = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}