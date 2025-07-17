package com.alijt.foodapp.view.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alijt.foodapp.R
import com.alijt.foodapp.adapter.RestaurantAdapter
import com.alijt.foodapp.databinding.FragmentBuyerDashboardBinding
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.VendorListRequest
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.repository.RestaurantRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.view.ProfileFragment
import com.alijt.foodapp.view.RestaurantDetailFragment
import com.alijt.foodapp.viewmodel.RestaurantViewModel
import com.alijt.foodapp.viewmodel.RestaurantViewModelFactory
import com.alijt.foodapp.network.RetrofitClient // اضافه شد
import com.alijt.foodapp.network.ApiService // اضافه شد
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BuyerDashboardFragment : Fragment() {

    private var _binding: FragmentBuyerDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var restaurantViewModel: RestaurantViewModel
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var sessionManager: SessionManager

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuyerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvBuyerDashboard.text = getString(R.string.restaurants_list_title)

        sessionManager = SessionManager(requireContext())

        // اصلاح شد: apiService و sessionManager به سازنده RestaurantRepository ارسال شدند
        val apiService = RetrofitClient.instance
        val repository = RestaurantRepository(apiService, sessionManager) // <-- اینجا اصلاح شد
        restaurantViewModel = ViewModelProvider(this, RestaurantViewModelFactory(repository, sessionManager))
            .get(RestaurantViewModel::class.java)

        setupRecyclerView()

        fetchRestaurants("")

        restaurantViewModel.restaurants.observe(viewLifecycleOwner) { result ->
            if (result is Result.Loading) {
                // نمایش لودینگ
            } else if (result is Result.Success<*>) {
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
            } else if (result is Result.Failure) {
                Toast.makeText(requireContext(), getString(R.string.failed_to_load_restaurants) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnViewProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Navigation to profile not implemented via NavComponent yet in BuyerDashboard", Toast.LENGTH_SHORT).show()
        }

        binding.etSearchRestaurant.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                s?.toString()?.let { query ->
                    searchJob = lifecycleScope.launch {
                        delay(500)
                        fetchRestaurants(query)
                    }
                } ?: run {
                    fetchRestaurants("")
                }
            }
        })
    }

    private fun setupRecyclerView() {
        restaurantAdapter = RestaurantAdapter(
            onItemClick = { restaurant ->
                restaurant.id?.let { restaurantId ->
                    val bundle = Bundle().apply {
                        putInt("restaurant_id", restaurantId)
                    }
                    val restaurantDetailFragment = RestaurantDetailFragment().apply {
                        arguments = bundle
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, restaurantDetailFragment)
                        .addToBackStack(null)
                        .commit()
                } ?: run {
                    Toast.makeText(requireContext(), getString(R.string.error_invalid_restaurant_id_for_details), Toast.LENGTH_SHORT).show()
                }
            },
            onEditClick = { restaurant ->
                Toast.makeText(requireContext(), getString(R.string.buyer_no_edit_option), Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvRestaurants.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = restaurantAdapter
        }
    }

    private fun fetchRestaurants(searchQuery: String) {
        val request = VendorListRequest(search = searchQuery.ifEmpty { null })
        restaurantViewModel.fetchRestaurants(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchJob?.cancel()
    }
}