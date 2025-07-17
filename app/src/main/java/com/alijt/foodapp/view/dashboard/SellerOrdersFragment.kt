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
import com.alijt.foodapp.adapter.OrderAdapter // استفاده از OrderAdapter که قبلا برای ادمین ساختیم
import com.alijt.foodapp.databinding.FragmentSellerOrdersBinding // باید ایجاد شود
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.SellerRepository // نیاز به SellerRepository
import com.alijt.foodapp.utils.SessionManager // نیاز به SessionManager
import com.alijt.foodapp.viewmodel.SellerViewModel // نیاز به SellerViewModel
import com.alijt.foodapp.viewmodel.SellerViewModelFactory // نیاز به SellerViewModelFactory

class SellerOrdersFragment : Fragment() {

    private var _binding: FragmentSellerOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var sellerViewModel: SellerViewModel
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel را از Activity والد (که SellerDashboardFragment آن را میزبانی می‌کند) به اشتراک بگذارید
        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext())
        val sellerRepository = SellerRepository(apiService, sessionManager)
        sellerViewModel = ViewModelProvider(requireActivity(), SellerViewModelFactory(sellerRepository, sessionManager))
            .get(SellerViewModel::class.java)

        setupRecyclerView()
        observeViewModel()

        // دریافت لیست سفارشات فروشنده (در آینده متد fetchSellerOrders را در SellerViewModel اضافه خواهیم کرد)
        // sellerViewModel.fetchSellerOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter { order ->
            Toast.makeText(requireContext(), getString(R.string.order_clicked_message, order.id), Toast.LENGTH_SHORT).show()
            // اینجا می‌توانید به Fragment جزئیات سفارش ناوبری کنید
        }
        binding.rvSellerOrders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderAdapter
        }
    }

    private fun observeViewModel() {
        // مشاهده لیست سفارشات از SellerViewModel
        // sellerViewModel.sellerOrders.observe(viewLifecycleOwner) { result ->
        //     when (result) {
        //         is Result.Loading -> { binding.progressBarSellerOrders.visibility = View.VISIBLE }
        //         is Result.Success -> {
        //             orderAdapter.submitList(result.data)
        //             binding.progressBarSellerOrders.visibility = View.GONE
        //         }
        //         is Result.Failure -> {
        //             Toast.makeText(requireContext(), getString(R.string.error_fetching_orders) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
        //             binding.progressBarSellerOrders.visibility = View.GONE
        //         }
        //     }
        // }

        // مشاهده نتایج عملیات یک‌باره (اگر در این Fragment استفاده شود)
        // sellerViewModel.operationResult.observe(viewLifecycleOwner) { result -> /* ... */ }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}