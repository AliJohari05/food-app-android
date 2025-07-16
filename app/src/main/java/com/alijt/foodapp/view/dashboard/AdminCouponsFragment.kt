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
import com.alijt.foodapp.adapter.CouponAdapter
import com.alijt.foodapp.databinding.FragmentAdminCouponsBinding
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.model.Coupon
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.AdminRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.AdminViewModel
import com.alijt.foodapp.viewmodel.AdminViewModelFactory

class AdminCouponsFragment : Fragment() {

    private var _binding: FragmentAdminCouponsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var couponAdapter: CouponAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminCouponsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext())
        val adminRepository = AdminRepository(apiService)
        adminViewModel = ViewModelProvider(requireActivity(), AdminViewModelFactory(adminRepository, sessionManager))
            .get(AdminViewModel::class.java)

        setupRecyclerView()
        setupListeners()
        observeViewModels()

        adminViewModel.fetchAllCoupons()
    }

    private fun setupRecyclerView() {
        couponAdapter = CouponAdapter(
            onEditClick = { coupon ->
                Toast.makeText(requireContext(), getString(R.string.coupon_edit_clicked, coupon.coupon_code), Toast.LENGTH_SHORT).show()
                val dialog = CouponEditDialogFragment.newInstance(coupon)
                dialog.show(childFragmentManager, "CouponEditDialog")
            },
            onDeleteClick = { couponId ->
                showDeleteConfirmationDialog(couponId)
            }
        )
        binding.rvCoupons.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = couponAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddCoupon.setOnClickListener {
            val dialog = CouponCreateDialogFragment()
            dialog.show(childFragmentManager, "CouponCreateDialog")
        }
    }

    private fun observeViewModels() {
        adminViewModel.couponsList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { binding.progressBarCoupons.visibility = View.VISIBLE }
                is Result.Success -> {
                    couponAdapter.submitList(result.data)
                    binding.progressBarCoupons.visibility = View.GONE
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_fetching_coupons) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                    binding.progressBarCoupons.visibility = View.GONE
                }
            }
        }

        adminViewModel.couponCreateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { /* Show loading */ }
                is Result.Success -> { Toast.makeText(requireContext(), getString(R.string.coupon_created_successfully), Toast.LENGTH_SHORT).show() }
                is Result.Failure -> { Toast.makeText(requireContext(), getString(R.string.error_creating_coupon) + ": ${result.exception.message}", Toast.LENGTH_LONG).show() }
            }
        }

        adminViewModel.couponUpdateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { /* Show loading */ }
                is Result.Success -> { Toast.makeText(requireContext(), getString(R.string.coupon_updated_successfully), Toast.LENGTH_SHORT).show() }
                is Result.Failure -> { Toast.makeText(requireContext(), getString(R.string.error_updating_coupon) + ": ${result.exception.message}", Toast.LENGTH_LONG).show() }
            }
        }

        adminViewModel.couponDeleteResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { /* Show loading */ }
                is Result.Success -> {
                    Toast.makeText(requireContext(), result.data, Toast.LENGTH_SHORT).show() // <-- تغییر: از result.data استفاده شد
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_deleting_coupon) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        adminViewModel.couponDetails.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { /* Show loading */ }
                is Result.Success -> { Toast.makeText(requireContext(), getString(R.string.coupon_details_loaded, result.data.coupon_code), Toast.LENGTH_SHORT).show() }
                is Result.Failure -> { Toast.makeText(requireContext(), getString(R.string.error_fetching_coupon_details) + ": ${result.exception.message}", Toast.LENGTH_LONG).show() }
            }
        }
    }

    private fun showDeleteConfirmationDialog(couponId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_coupon_dialog_title))
            .setMessage(getString(R.string.delete_coupon_dialog_message, couponId))
            .setPositiveButton(getString(R.string.delete_button)) { dialog, _ ->
                adminViewModel.deleteCoupon(couponId.toString())
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