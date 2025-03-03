package com.simon.vpdassesment.features.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.simon.vpdassesment.core.entity.TransactionEntity
import com.simon.vpdassesment.databinding.TransactionRowBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter() : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = TransactionRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.bind(transaction)
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    private val transactionList = mutableListOf<TransactionEntity>()
    fun updateList(newList: List<TransactionEntity>) {
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(TransactionDiffCallback(transactionList, newList))
        transactionList.clear()
        transactionList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class TransactionViewHolder(private val binding: TransactionRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: TransactionEntity) {
            binding.desc.text = transaction.description
            binding.amount.text = "₦${transaction.amount}"

            val date = Date(transaction.timeStampLong)
            val format = SimpleDateFormat("dd MMMM ентитету", Locale.getDefault())
            binding.date.text = format.format(date)
        }
    }
}


class TransactionDiffCallback(
    private val oldList: List<TransactionEntity>,
    private val newList: List<TransactionEntity>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].transactionId == newList[newItemPosition].transactionId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}