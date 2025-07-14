package com.alijt.foodapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ItemRestaurantBinding
import com.alijt.foodapp.model.Restaurant
import com.bumptech.glide.Glide

class RestaurantAdapter(private val clickListener: (Restaurant) -> Unit) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    private var restaurants: List<Restaurant> = emptyList()

    fun submitList(newRestaurants: List<Restaurant>) {
        restaurants = newRestaurants
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.bind(restaurant)
    }

    override fun getItemCount(): Int = restaurants.size

    inner class RestaurantViewHolder(private val binding: ItemRestaurantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(restaurant: Restaurant) {
            binding.tvRestaurantName.text = restaurant.name
            binding.tvRestaurantDescription.text = restaurant.description ?: ""
            binding.tvRestaurantAddress.text = restaurant.address

            restaurant.averageRating?.let { rating ->
                binding.tvRestaurantRating.text = binding.root.context.getString(com.alijt.foodapp.R.string.rating_format, String.format("%.1f", rating))
                binding.tvRestaurantRating.visibility = View.VISIBLE
            } ?: run {
                binding.tvRestaurantRating.visibility = View.GONE
            }

            val displayStatus = restaurant.status ?: "OPEN"
            binding.tvRestaurantStatus.text = displayStatus

            when (displayStatus.uppercase()) {
                "OPEN" -> binding.tvRestaurantStatus.setBackgroundResource(android.R.color.holo_green_dark)
                "CLOSED" -> binding.tvRestaurantStatus.setBackgroundResource(android.R.color.holo_red_dark)
                "BUSY" -> binding.tvRestaurantStatus.setBackgroundResource(android.R.color.holo_orange_dark)
                else -> binding.tvRestaurantStatus.setBackgroundResource(android.R.color.darker_gray)
            }

            restaurant.profileImageUrl?.let { imageUrl ->
                Glide.with(binding.ivRestaurantLogo.context)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivRestaurantLogo)
            } ?: run {
                binding.ivRestaurantLogo.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            binding.root.setOnClickListener {
                clickListener(restaurant)
            }
        }
    }
}