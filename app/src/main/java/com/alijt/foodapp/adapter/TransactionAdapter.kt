package com.alijt.foodapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ItemTransactionBinding // باید ایجاد شود
import com.alijt.foodapp.model.Transaction

class TransactionAdapter(private val onClick: (Transaction) -> Unit) :
    ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction, onClick)
    }

    class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction, onClick: (Transaction) -> Unit) {
            binding.tvTransactionId.text = binding.root.context.getString(R.string.transaction_id_format, transaction.id)
            binding.tvTransactionAmount.text = binding.root.context.getString(R.string.transaction_amount_format, transaction.amount)
            binding.tvTransactionStatus.text = binding.root.context.getString(R.string.transaction_status_format, transaction.status)
            binding.root.setOnClickListener { onClick(transaction) }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}