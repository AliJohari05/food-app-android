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
import com.alijt.foodapp.databinding.FragmentSellerOrdersBinding
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.model.Order
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.SellerRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.SellerViewModel
import com.alijt.foodapp.viewmodel.SellerViewModelFactory

class SellerOrdersFragment : Fragment() {

    private var _binding: FragmentSellerOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var sellerViewModel: SellerViewModel
    private lateinit var orderAdapter: OrderAdapter
    private var currentRestaurantId: Int? = null // برای نگهداری شناسه رستوران انتخاب شده

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext())
        val sellerRepository = SellerRepository(apiService, sessionManager)
        sellerViewModel = ViewModelProvider(requireActivity(), SellerViewModelFactory(sellerRepository, sessionManager))
            .get(SellerViewModel::class.java)

        // در اینجا باید شناسه رستوران انتخاب شده را دریافت کنید.
        // مثلاً از طریق Bundle اگر از MyRestaurantsFragment ناوبری می‌کنید
        // یا اگر فقط یک رستوران دارید، آن را از SellerViewModel.myRestaurants.value.first() بگیرید.
        // برای شروع، فرض می‌کنیم یک رستوران انتخاب شده وجود دارد.
        // بهترین راه این است که MyRestaurantsFragment پس از انتخاب رستوران، ID آن را به SellerOrdersFragment (یا ViewModel مشترک) بدهد.
        // فعلاً به صورت دستی currentRestaurantId را ست می‌کنیم.
        currentRestaurantId = 1 // TODO: این را با ID رستوران واقعی جایگزین کنید (باید از طریق انتخاب رستوران یا از API دریافت شود)
        currentRestaurantId?.let { sellerViewModel.fetchRestaurantOrders(it) } // دریافت سفارشات هنگام ایجاد Fragment

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            onClick = { order ->
                Toast.makeText(requireContext(), getString(R.string.order_clicked_message, order.id), Toast.LENGTH_SHORT).show()
                // اینجا می‌توانید به Fragment جزئیات سفارش ناوبری کنید
            },
            onStatusChangeClick = { order, newStatus -> // <-- این lambda را به OrderAdapter اضافه کنید
                Toast.makeText(requireContext(), getString(R.string.update_order_status_clicked, order.id, newStatus), Toast.LENGTH_SHORT).show() // رشته جدید
                showUpdateOrderStatusDialog(order, newStatus)
            }
        )
        binding.rvSellerOrders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderAdapter
        }
    }

    private fun observeViewModel() {
        sellerViewModel.restaurantOrders.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { binding.progressBarSellerOrders.visibility = View.VISIBLE }
                is Result.Success<*> -> {
                    val data = result.data
                    if (data is List<*>) {
                        val orderList = data as List<Order>
                        orderAdapter.submitList(orderList)
                        if (orderList.isEmpty()) {
                            Toast.makeText(requireContext(), getString(R.string.no_orders_found), Toast.LENGTH_SHORT).show() // رشته جدید
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

        sellerViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success<*> -> {
                    val message = if (result.data is com.alijt.foodapp.model.MessageResponse) (result.data as com.alijt.foodapp.model.MessageResponse).message else result.data.toString()
                    Toast.makeText(requireContext(), getString(R.string.operation_successful) + ": $message", Toast.LENGTH_SHORT).show()
                    // پس از موفقیت، لیست سفارشات را رفرش کنید.
                    currentRestaurantId?.let { sellerViewModel.fetchRestaurantOrders(it) }
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

    private fun showUpdateOrderStatusDialog(order: Order, newStatus: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.update_order_status_dialog_title)) // رشته جدید
            .setMessage(getString(R.string.update_order_status_dialog_message, order.id, newStatus)) // رشته جدید
            .setPositiveButton(getString(R.string.confirm_button)) { dialog, _ -> // رشته جدید
                sellerViewModel.updateOrderStatus(order.id, newStatus)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}