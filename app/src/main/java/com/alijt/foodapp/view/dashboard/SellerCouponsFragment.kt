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
import com.alijt.foodapp.adapter.CouponAdapter // استفاده از CouponAdapter که قبلا ساختیم
import com.alijt.foodapp.databinding.FragmentSellerCouponsBinding // باید ایجاد شود
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.SellerRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.SellerViewModel
import com.alijt.foodapp.viewmodel.SellerViewModelFactory

class SellerCouponsFragment : Fragment() {

    private var _binding: FragmentSellerCouponsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sellerViewModel: SellerViewModel
    private lateinit var couponAdapter: CouponAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerCouponsBinding.inflate(inflater, container, false)
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
        setupListeners() // افزودن شنونده‌ها
        observeViewModel()

        // دریافت لیست کوپن‌های فروشنده (در آینده متد fetchSellerCoupons را در SellerViewModel اضافه خواهیم کرد)
        // sellerViewModel.fetchSellerCoupons()
    }

    private fun setupRecyclerView() {
        couponAdapter = CouponAdapter(
            onEditClick = { coupon ->
                Toast.makeText(requireContext(), getString(R.string.coupon_edit_clicked, coupon.coupon_code), Toast.LENGTH_SHORT).show()
                // اینجا می‌توانید دیالوگ ویرایش کوپن را نمایش دهید
            },
            onDeleteClick = { couponId ->
                showDeleteConfirmationDialog(couponId) // نمایش دیالوگ تایید حذف
            }
        )
        binding.rvSellerCoupons.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = couponAdapter
        }
    }

    private fun setupListeners() {
        // دکمه شناور برای افزودن کوپن جدید (اگر در XML دارید)
        binding.fabAddCoupon.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.add_new_coupon_clicked), Toast.LENGTH_SHORT).show()
            // اینجا می‌توانید دیالوگ یا Fragment جدید برای ایجاد کوپن نمایش دهید
        }
    }

    private fun observeViewModel() {
        // مشاهده لیست کوپن‌ها از SellerViewModel
        // sellerViewModel.sellerCoupons.observe(viewLifecycleOwner) { result ->
        //     when (result) {
        //         is Result.Loading -> { binding.progressBarSellerCoupons.visibility = View.VISIBLE }
        //         is Result.Success -> {
        //             couponAdapter.submitList(result.data)
        //             binding.progressBarSellerCoupons.visibility = View.GONE
        //         }
        //         is Result.Failure -> {
        //             Toast.makeText(requireContext(), getString(R.string.error_fetching_coupons) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
        //             binding.progressBarSellerCoupons.visibility = View.GONE
        //         }
        //     }
        // }

        // مشاهده نتایج عملیات یک‌باره (اگر در این Fragment استفاده شود)
        // sellerViewModel.operationResult.observe(viewLifecycleOwner) { result -> /* ... */ }
    }

    // متد برای نمایش دیالوگ تایید حذف کوپن (همانند AdminCouponsFragment)
    private fun showDeleteConfirmationDialog(couponId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_coupon_dialog_title))
            .setMessage(getString(R.string.delete_coupon_dialog_message, couponId))
            .setPositiveButton(getString(R.string.delete_button)) { dialog, _ ->
                // adminViewModel.deleteCoupon(couponId.toString()) // اینجا باید از sellerViewModel استفاده شود
                Toast.makeText(requireContext(), getString(R.string.delete_coupon_confirm_message, couponId), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}