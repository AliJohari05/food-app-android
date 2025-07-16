package com.alijt.foodapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ItemOrderBinding // باید ایجاد شود
import com.alijt.foodapp.model.Order

class OrderAdapter(private val onClick: (Order) -> Unit) :
    ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order, onClick)
    }

    class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order, onClick: (Order) -> Unit) {
            binding.tvOrderId.text = binding.root.context.getString(R.string.order_id_format, order.id)
            binding.tvOrderStatus.text = binding.root.context.getString(R.string.order_status_format, order.status)
            binding.tvOrderPrice.text = binding.root.context.getString(R.string.order_price_format, order.payPrice)
            binding.root.setOnClickListener { onClick(order) }
        }
    }

    private class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}