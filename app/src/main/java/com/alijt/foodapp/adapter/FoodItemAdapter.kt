// FoodApp/app/src/main/java/com/alijt/foodapp/adapter/FoodItemAdapter.kt
package com.alijt.foodapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ItemFoodItemBinding // Correct binding for item_food_item.xml
// import com.alijt.foodapp.databinding.ItemRestaurantBinding // This import is not needed here
import com.alijt.foodapp.model.FoodItem // Import FoodItem model
// import com.alijt.foodapp.model.Restaurant // This import is not needed here
import com.bumptech.glide.Glide

class FoodItemAdapter(private val clickListener: (FoodItem) -> Unit) : RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>() {

    private var foodItems: List<FoodItem> = emptyList()

    // Corrected: submitList now accepts List<FoodItem> directly
    fun submitList(newFoodItems: List<FoodItem>) {
        foodItems = newFoodItems
        notifyDataSetChanged() // Notify the adapter that data has changed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder { // Corrected ViewHolder type
        // Inflate item_food_item.xml and create ItemFoodItemBinding
        val binding = ItemFoodItemBinding.inflate(LayoutInflater.from(parent.context), parent, false) // Corrected binding
        return FoodItemViewHolder(binding) // Corrected ViewHolder class
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) { // Corrected ViewHolder type
        val foodItem = foodItems[position] // Corrected variable name and type
        holder.bind(foodItem)
    }

    override fun getItemCount(): Int = foodItems.size // Corrected variable name

    inner class FoodItemViewHolder(private val binding: ItemFoodItemBinding) : RecyclerView.ViewHolder(binding.root) { // Corrected binding and class name
        fun bind(foodItem: FoodItem) { // Corrected parameter type
            binding.tvFoodItemName.text = foodItem.name
            binding.tvFoodItemDescription.text = foodItem.description ?: ""
            // CORRECTED: Pass Double directly to getString, and check price_format in strings.xml
            binding.tvFoodItemPrice.text = binding.root.context.getString(R.string.price_format, foodItem.price)
            binding.tvFoodItemSupply.text = binding.root.context.getString(R.string.supply_format, foodItem.supply)

            // CORRECTED: Use foodItem.imageUrl and resolve Glide ambiguity
            foodItem.imageUrl?.let { imageUrl ->
                Glide.with(binding.ivFoodItemImage.context)
                    .load(imageUrl as String?) // Explicitly cast to String? to resolve ambiguity
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivFoodItemImage)
            } ?: run {
                binding.ivFoodItemImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            binding.btnAddRemoveCart.setOnClickListener {
                clickListener(foodItem)
            }
        }
    }
}