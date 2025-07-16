package com.alijt.foodapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ItemUserBinding
import com.alijt.foodapp.model.User

class UserListAdapter(private val onStatusChange: (User, String) -> Unit) :
    ListAdapter<User, UserListAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user, onStatusChange)
    }

    class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User, onStatusChange: (User, String) -> Unit) {
            binding.tvUserName.text = user.fullName
            binding.tvUserPhone.text = binding.root.context.getString(R.string.user_phone_format, user.phone ?: "")
            binding.tvUserRole.text = binding.root.context.getString(R.string.user_role_format, user.role)

            // نمایش وضعیت کاربر
            val statusText = when (user.status?.uppercase()) { // مطمئن شوید status از بک‌اند می‌آید
                "APPROVED" -> binding.root.context.getString(R.string.status_approved)
                "REJECTED" -> binding.root.context.getString(R.string.status_rejected)
                "PENDING_APPROVAL" -> binding.root.context.getString(R.string.status_pending)
                else -> binding.root.context.getString(R.string.status_unknown)
            }
            binding.tvUserStatus.visibility = View.VISIBLE
            binding.tvUserStatus.text = binding.root.context.getString(R.string.user_status_format, statusText)

            // منطق نمایش دکمه‌های تأیید/رد
            // دکمه‌ها فقط برای 'seller' و 'courier' و تنها زمانی که وضعیت 'APPROVED' یا 'REJECTED' نیست نمایش داده می‌شوند.
            val currentStatus = user.status?.uppercase()
            if ((user.role.equals("seller", ignoreCase = true) || user.role.equals("courier", ignoreCase = true)) &&
                (currentStatus != "APPROVED" && currentStatus != "REJECTED")) {
                binding.btnApprove.visibility = View.VISIBLE
                binding.btnReject.visibility = View.VISIBLE
            } else {
                binding.btnApprove.visibility = View.GONE
                binding.btnReject.visibility = View.GONE
            }

            binding.btnApprove.setOnClickListener {
                onStatusChange(user, "approved")
            }

            binding.btnReject.setOnClickListener {
                onStatusChange(user, "rejected")
            }
        }
    }

    private class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem // Data class equals handles all properties
        }
    }
}