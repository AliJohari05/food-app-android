// FoodApp/app/src/main/java/com/alijt/foodapp/view/RestaurantDetailFragment.kt
package com.alijt.foodapp.view

import android.os.Bundle
import android.util.Log // Import Log for debugging
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alijt.foodapp.R
import com.alijt.foodapp.adapter.FoodItemAdapter
import com.alijt.foodapp.databinding.FragmentRestaurantDetailBinding
import com.alijt.foodapp.model.FoodItem
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.RestaurantMenuDetailsResponse
import com.alijt.foodapp.model.onFailure
import com.alijt.foodapp.model.onSuccess
import com.alijt.foodapp.repository.MenuRepository
import com.alijt.foodapp.network.ApiService
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.MenuViewModel
import com.alijt.foodapp.viewmodel.MenuViewModelFactory
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonArray // Import JsonArray
import com.google.gson.JsonObject // Import JsonObject
import com.google.gson.JsonPrimitive // Import JsonPrimitive

class RestaurantDetailFragment : Fragment() {

    private var _binding: FragmentRestaurantDetailBinding? = null
    private val binding get() = _binding!!

    private var restaurantId: Int = -1
    private lateinit var menuViewModel: MenuViewModel
    private lateinit var foodItemAdapter: FoodItemAdapter
    private lateinit var sessionManager: SessionManager

    private val foodItemListType = object : TypeToken<List<FoodItem>>() {}.type


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            restaurantId = it.getInt("restaurant_id", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRestaurantId.text = getString(R.string.restaurant_id_format, restaurantId)

        sessionManager = SessionManager(requireContext())
        val apiService = RetrofitClient.instance
        val repository = MenuRepository(apiService)
        menuViewModel = ViewModelProvider(this, MenuViewModelFactory(repository, sessionManager))
            .get(MenuViewModel::class.java)

        setupRecyclerView()

        if (restaurantId != -1) {
            menuViewModel.fetchMenuDetails(restaurantId)
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_invalid_restaurant_id), Toast.LENGTH_SHORT).show()
        }

        menuViewModel.menuDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                displayRestaurantDetails(response.vendor)

                val allFoodItems = mutableListOf<FoodItem>()
                response.menuTitles.forEach { title ->
                    response.additionalProperties?.get(title)?.let { jsonElement ->
                        // NEW LOGS FOR DEBUGGING
                        Log.d("GsonDebug", "--- Processing Menu Category: $title ---")
                        Log.d("GsonDebug", "JsonElement for $title: ${jsonElement.toString()}")
                        Log.d("GsonDebug", "Is JsonArray: ${jsonElement.isJsonArray}")
                        Log.d("GsonDebug", "Is JsonObject: ${jsonElement.isJsonObject}")
                        Log.d("GsonDebug", "Is JsonPrimitive: ${jsonElement.isJsonPrimitive}")
                        Log.d("GsonDebug", "Is JsonNull: ${jsonElement.isJsonNull}")

                        if (jsonElement.isJsonArray) { // Only try to parse if it's actually a JsonArray
                            try {
                                val itemsForCategory = Gson().fromJson<List<FoodItem>>(jsonElement, foodItemListType)
                                Log.d("GsonDebug", "Parsed items for $title: ${itemsForCategory.size} items")
                                allFoodItems.addAll(itemsForCategory)
                            } catch (e: JsonSyntaxException) {
                                Toast.makeText(requireContext(), "Error parsing menu items for $title: ${e.message}", Toast.LENGTH_LONG).show()
                                Log.e("RestaurantDetail", "JsonSyntaxException for $title:", e)
                                e.printStackTrace()
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), "Unexpected error parsing menu items for $title: ${e.message}", Toast.LENGTH_LONG).show()
                                Log.e("RestaurantDetail", "Unexpected Exception for $title:", e)
                                e.printStackTrace()
                            }
                        } else {
                            Log.e("GsonDebug", "JsonElement for $title is NOT a JsonArray. Type: ${jsonElement.javaClass.simpleName}")
                            Toast.makeText(requireContext(), "Unexpected menu item data format for $title", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                if (allFoodItems.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.no_menu_items_found), Toast.LENGTH_SHORT).show()
                } else {
                    foodItemAdapter.submitList(allFoodItems)
                }

            }.onFailure { exception ->
                Toast.makeText(requireContext(), getString(R.string.failed_to_load_menu) + ": ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerView() {
        foodItemAdapter = FoodItemAdapter { foodItem ->
            Toast.makeText(requireContext(), "Add/Remove ${foodItem.name} to cart", Toast.LENGTH_SHORT).show()
        }
        binding.rvMenuItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodItemAdapter
        }
    }

    private fun displayRestaurantDetails(restaurant: Restaurant) {
        binding.tvRestaurantDetailTitle.text = restaurant.name
        binding.tvRestaurantId.text = getString(R.string.restaurant_id_format, restaurant.id)
        binding.tvRestaurantDescriptionDetail.text = restaurant.description ?: ""
        binding.tvRestaurantAddressDetail.text = restaurant.address

        restaurant.profileImageUrl?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(binding.ivRestaurantLogoDetail)
        } ?: run {
            binding.ivRestaurantLogoDetail.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}