package com.alijt.foodapp.view.dashboard

import android.app.AlertDialog // برای دیالوگ تایید حذف
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alijt.foodapp.R
import com.alijt.foodapp.adapter.RestaurantAdapter
import com.alijt.foodapp.databinding.FragmentMyRestaurantsBinding
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.model.Restaurant // برای پاس دادن آبجکت رستوران
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.SellerRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.SellerViewModel
import com.alijt.foodapp.viewmodel.SellerViewModelFactory

class MyRestaurantsFragment : Fragment() {

    private var _binding: FragmentMyRestaurantsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sellerViewModel: SellerViewModel
    private lateinit var restaurantAdapter: RestaurantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyRestaurantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext())
        val sellerRepository = SellerRepository(apiService, sessionManager)
        sellerViewModel = ViewModelProvider(requireActivity(), SellerViewModelFactory(sellerRepository, sessionManager))
            .get(SellerViewModel::class.java)

        setupRecyclerView()
        setupListeners() // اضافه شد
        observeViewModel()

        sellerViewModel.fetchMyRestaurants()
    }

    private fun setupRecyclerView() {
        restaurantAdapter = RestaurantAdapter(
            onItemClick = { restaurant ->
                Toast.makeText(requireContext(), getString(R.string.restaurant_clicked, restaurant.name), Toast.LENGTH_SHORT).show()
            },
            onEditClick = { restaurant ->
                Toast.makeText(requireContext(), getString(R.string.edit_restaurant_clicked, restaurant.name), Toast.LENGTH_SHORT).show() // رشته جدید
                val dialog = RestaurantEditDialogFragment.newInstance(restaurant)
                dialog.show(childFragmentManager, "RestaurantEditDialog")
            }
        )
        binding.rvMyRestaurants.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = restaurantAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddRestaurant.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.add_new_restaurant_clicked), Toast.LENGTH_SHORT).show()
            val dialog = RestaurantCreateDialogFragment()
            dialog.show(childFragmentManager, "RestaurantCreateDialog")
        }
    }

    private fun observeViewModel() {
        sellerViewModel.myRestaurants.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBarRestaurants.visibility = View.VISIBLE
                }
                is Result.Success<*> -> {
                    val data = result.data
                    if (data is List<*>) {
                        val restaurantList = data as List<Restaurant>
                        if (restaurantList.isEmpty()) {
                            Toast.makeText(requireContext(), getString(R.string.no_restaurants_found), Toast.LENGTH_SHORT).show()
                        } else {
                            restaurantAdapter.submitList(restaurantList)
                        }
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.error_unexpected_data_format), Toast.LENGTH_LONG).show()
                    }
                    binding.progressBarRestaurants.visibility = View.GONE
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_fetching_restaurants) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                    binding.progressBarRestaurants.visibility = View.GONE
                }
            }
        }

        sellerViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success<*> -> {
                    val message = if (result.data is com.alijt.foodapp.model.MessageResponse) (result.data as com.alijt.foodapp.model.MessageResponse).message else result.data.toString()
                    Toast.makeText(requireContext(), getString(R.string.operation_successful) + ": $message", Toast.LENGTH_SHORT).show()
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.operation_failed) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
                is Result.Loading -> { /* مدیریت لودینگ کلی عملیات */ }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}