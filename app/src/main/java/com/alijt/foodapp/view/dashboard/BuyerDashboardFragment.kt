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
import com.alijt.foodapp.model.Restaurant // Import Restaurant
import com.alijt.foodapp.model.VendorListRequest
import com.alijt.foodapp.repository.RestaurantRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.view.ProfileFragment
import com.alijt.foodapp.view.RestaurantDetailFragment // Import RestaurantDetailFragment
import com.alijt.foodapp.viewmodel.RestaurantViewModel
import com.alijt.foodapp.viewmodel.RestaurantViewModelFactory
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

        val repository = RestaurantRepository()
        restaurantViewModel = ViewModelProvider(this, RestaurantViewModelFactory(repository, sessionManager))
            .get(RestaurantViewModel::class.java)

        // Setup RecyclerView, passing the click listener
        setupRecyclerView()

        fetchRestaurants("")

        restaurantViewModel.restaurants.observe(viewLifecycleOwner) { result ->
            result.onSuccess { restaurants ->
                if (restaurants.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.no_restaurants_found), Toast.LENGTH_SHORT).show()
                } else {
                    restaurantAdapter.submitList(restaurants)
                }
            }.onFailure { exception ->
                Toast.makeText(requireContext(), getString(R.string.failed_to_load_restaurants) + ": ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnViewProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()
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
        // Pass the click listener lambda to the adapter
        restaurantAdapter = RestaurantAdapter { restaurant ->
            // Handle restaurant item click: navigate to RestaurantDetailFragment
            val bundle = Bundle().apply {
                putInt("restaurant_id", restaurant.id) // Pass restaurant ID
                // You can also pass restaurant name, etc. if needed
            }
            val restaurantDetailFragment = RestaurantDetailFragment().apply {
                arguments = bundle
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, restaurantDetailFragment)
                .addToBackStack(null) // Allows going back to BuyerDashboardFragment
                .commit()
        }
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