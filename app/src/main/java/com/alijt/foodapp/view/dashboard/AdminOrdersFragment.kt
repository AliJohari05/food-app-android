package com.alijt.foodapp.view.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alijt.foodapp.R
import com.alijt.foodapp.adapter.OrderAdapter
import com.alijt.foodapp.databinding.FragmentAdminOrdersBinding // باید ایجاد شود
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.AdminRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.AdminViewModel
import com.alijt.foodapp.viewmodel.AdminViewModelFactory

class AdminOrdersFragment : Fragment() {

    private var _binding: FragmentAdminOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext())
        val adminRepository = AdminRepository(apiService)
        val factory = AdminViewModelFactory(adminRepository, sessionManager)
        adminViewModel = ViewModelProvider(requireActivity(), factory).get(AdminViewModel::class.java) // ViewModel مشترک

        setupRecyclerView()
        observeViewModels()

        adminViewModel.fetchAllOrders() // دریافت داده‌ها
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter { order ->
            Toast.makeText(requireContext(), getString(R.string.order_clicked_message, order.id), Toast.LENGTH_SHORT).show()
        }
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderAdapter
        }
    }

    private fun observeViewModels() {
        adminViewModel.ordersList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBarOrders.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    orderAdapter.submitList(result.data)
                    binding.progressBarOrders.visibility = View.GONE
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_fetching_orders) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                    binding.progressBarOrders.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}