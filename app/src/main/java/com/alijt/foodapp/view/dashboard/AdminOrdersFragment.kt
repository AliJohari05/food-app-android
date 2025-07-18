package com.alijt.foodapp.view.dashboard

import android.app.AlertDialog
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
import com.alijt.foodapp.databinding.FragmentAdminOrdersBinding
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.model.Order
import com.alijt.foodapp.model.MessageResponse // اضافه شد
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
        val adminRepository = AdminRepository(apiService, sessionManager)
        adminViewModel = ViewModelProvider(requireActivity(), AdminViewModelFactory(adminRepository, sessionManager))
            .get(AdminViewModel::class.java)

        setupRecyclerView()
        observeViewModels()

        adminViewModel.fetchAllOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            onClick = { order ->
                Toast.makeText(requireContext(), getString(R.string.order_clicked_message, order.id), Toast.LENGTH_SHORT).show()
            },
            onStatusChangeClick = { order, newStatus ->
                Toast.makeText(requireContext(), getString(R.string.update_order_status_clicked, order.id, newStatus), Toast.LENGTH_SHORT).show()
                showUpdateOrderStatusDialog(order, newStatus)
            }
        )
        binding.rvSellerOrders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderAdapter
        }
    }

    private fun observeViewModels() {
        adminViewModel.ordersList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { binding.progressBarSellerOrders.visibility = View.VISIBLE }
                is Result.Success<*> -> {
                    val data = result.data
                    if (data is List<*>) {
                        val orderList = data as List<Order>
                        orderAdapter.submitList(orderList)
                        if (orderList.isEmpty()) {
                            Toast.makeText(requireContext(), getString(R.string.no_orders_found), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.error_unexpected_data_format), Toast.LENGTH_LONG).show()
                    }
                    binding.progressBarSellerOrders.visibility = View.GONE
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_fetching_orders) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                    binding.progressBarSellerOrders.visibility = View.GONE
                }
            }
        }

        adminViewModel.userStatusUpdateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { /* نمایش لودینگ */ }
                is Result.Success<*> -> { // <-- استفاده از <*>
                    val message = result.data // اینجا message از نوع Any? است (که انتظار String را داریم)
                    if (message is String) { // <-- بررسی نوع
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.operation_successful), Toast.LENGTH_SHORT).show() // Fallback message
                    }
                    adminViewModel.fetchAllOrders() // رفرش لیست
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_updating_order_status) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showUpdateOrderStatusDialog(order: Order, newStatus: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.update_order_status_dialog_title))
            .setMessage(getString(R.string.update_order_status_dialog_message, order.id, newStatus))
            .setPositiveButton(getString(R.string.confirm_button)) { dialog, _ ->
                adminViewModel.updateOrderStatus(order.id, newStatus)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}