package com.alijt.foodapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View // برای View.GONE/VISIBLE
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ItemOrderBinding
import com.alijt.foodapp.model.Order

class OrderAdapter(
    private val onClick: (Order) -> Unit,
    private val onStatusChangeClick: (Order, String) -> Unit // <-- اضافه شد
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order, onClick, onStatusChangeClick) // <-- اضافه شد
    }

    class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order, onClick: (Order) -> Unit, onStatusChangeClick: (Order, String) -> Unit) { // <-- اضافه شد
            binding.tvOrderId.text = binding.root.context.getString(R.string.order_id_format_full, order.id)
            binding.tvOrderStatus.text = binding.root.context.getString(R.string.order_status_format_full, order.status)
            binding.tvOrderPrice.text = binding.root.context.getString(R.string.order_price_format_full, order.payPrice.toDouble()) // cast به Double برای %.2f

            binding.root.setOnClickListener { onClick(order) }

            // منطق نمایش/عدم نمایش دکمه‌های تغییر وضعیت
            // دکمه‌ها فقط اگر وضعیت سفارش "waiting vendor" باشد نمایش داده می‌شوند.
            // یا وضعیت‌های دیگر که فروشنده بتواند آنها را تغییر دهد.
            if (order.status.equals("waiting vendor", ignoreCase = true)) {
                binding.btnAcceptOrder.visibility = View.VISIBLE
                binding.btnRejectOrder.visibility = View.VISIBLE
                binding.btnServeOrder.visibility = View.GONE // هنوز آماده سرویس نیست
            } else if (order.status.equals("accepted", ignoreCase = true)) {
                binding.btnAcceptOrder.visibility = View.GONE
                binding.btnRejectOrder.visibility = View.GONE
                binding.btnServeOrder.visibility = View.VISIBLE // حالا آماده سرویس است
            }
            else {
                binding.btnAcceptOrder.visibility = View.GONE
                binding.btnRejectOrder.visibility = View.GONE
                binding.btnServeOrder.visibility = View.GONE
            }

            binding.btnAcceptOrder.setOnClickListener { onStatusChangeClick(order, "accepted") }
            binding.btnRejectOrder.setOnClickListener { onStatusChangeClick(order, "rejected") }
            binding.btnServeOrder.setOnClickListener { onStatusChangeClick(order, "served") }
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