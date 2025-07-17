package com.alijt.foodapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ItemRestaurantBinding
import com.alijt.foodapp.model.Restaurant

class RestaurantAdapter(
    private val onItemClick: (Restaurant) -> Unit,
    private val onEditClick: (Restaurant) -> Unit
) : ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(RestaurantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = getItem(position)
        holder.bind(restaurant, onItemClick, onEditClick) // onDeleteClick حذف شد
    }

    class RestaurantViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(restaurant: Restaurant, onItemClick: (Restaurant) -> Unit, onEditClick: (Restaurant) -> Unit) { // onDeleteClick حذف شد
            binding.tvRestaurantName.text = restaurant.name
            binding.tvRestaurantAddress.text = restaurant.address
            binding.tvRestaurantPhone.text = binding.root.context.getString(R.string.restaurant_phone_format, restaurant.phone)

            binding.root.setOnClickListener { onItemClick(restaurant) }

            binding.btnEditRestaurant.setOnClickListener { onEditClick(restaurant) }
        }
    }

    private class RestaurantDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem == newItem
        }
    }
}