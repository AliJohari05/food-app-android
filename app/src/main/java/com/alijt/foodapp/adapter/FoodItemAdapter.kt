package com.alijt.foodapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ItemFoodItemBinding
import com.alijt.foodapp.model.FoodItem

class FoodItemAdapter(
    private val onItemClick: (FoodItem) -> Unit,
    private val onEditClick: (FoodItem) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : ListAdapter<FoodItem, FoodItemAdapter.FoodItemViewHolder>(FoodItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val binding = ItemFoodItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val foodItem = getItem(position)
        holder.bind(foodItem, onItemClick, onEditClick, onDeleteClick)
    }

    class FoodItemViewHolder(private val binding: ItemFoodItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(foodItem: FoodItem, onItemClick: (FoodItem) -> Unit, onEditClick: (FoodItem) -> Unit, onDeleteClick: (Int) -> Unit) {
            binding.tvFoodItemName.text = foodItem.name
            binding.tvFoodItemDescription.text = foodItem.description
            binding.tvFoodItemPrice.text = binding.root.context.getString(R.string.food_item_price_format, foodItem.price) // رشته جدید
            binding.tvFoodItemSupply.text = binding.root.context.getString(R.string.food_item_supply_format, foodItem.supply) // رشته جدید

            binding.root.setOnClickListener { onItemClick(foodItem) }

            binding.btnEditFoodItem.setOnClickListener { onEditClick(foodItem) } // فرض شده btnEditFoodItem در XML هست
            binding.btnDeleteFoodItem.setOnClickListener { foodItem.id?.let { id -> onDeleteClick(id) } } // فرض شده btnDeleteFoodItem در XML هست
        }
    }

    private class FoodItemDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem == newItem
        }
    }
}