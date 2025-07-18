package com.alijt.foodapp.view.dashboard

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.FragmentCouponCreateDialogBinding
import com.alijt.foodapp.model.CreateCouponRequest
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.AdminRepository
import com.alijt.foodapp.utils.SessionManager // <-- اضافه شد
import com.alijt.foodapp.viewmodel.AdminViewModel
import com.alijt.foodapp.viewmodel.AdminViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CouponCreateDialogFragment : DialogFragment() {

    private var _binding: FragmentCouponCreateDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminViewModel: AdminViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCouponCreateDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext()) // <-- sessionManager تعریف شد
        val adminRepository = AdminRepository(apiService, sessionManager) // <-- sessionManager به AdminRepository پاس داده شد
        adminViewModel = ViewModelProvider(requireActivity(), AdminViewModelFactory(adminRepository, sessionManager)) // <-- sessionManager به Factory پاس داده شد
            .get(AdminViewModel::class.java)

        setupSpinners()
        setupListeners()
        observeViewModel()
    }

    private fun setupSpinners() {
        val couponTypes = arrayOf("fixed", "percent")
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, couponTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCouponType.adapter = typeAdapter
    }

    private fun setupListeners() {
        binding.etStartDate.setOnClickListener {
            showDatePickerDialog(binding.etStartDate)
        }

        binding.etEndDate.setOnClickListener {
            showDatePickerDialog(binding.etEndDate)
        }

        binding.btnCreateCoupon.setOnClickListener {
            val couponCode = binding.etCouponCode.text.toString().trim()
            val type = binding.spinnerCouponType.selectedItem.toString()
            val value = binding.etCouponValue.text.toString().toDoubleOrNull()
            val minPrice = binding.etMinPrice.text.toString().toIntOrNull()
            val userCount = binding.etUserCount.text.toString().toIntOrNull()
            val startDate = binding.etStartDate.text.toString().trim()
            val endDate = binding.etEndDate.text.toString().trim()

            if (couponCode.isEmpty() || value == null || minPrice == null || userCount == null || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                dateFormat.parse(startDate)
                dateFormat.parse(endDate)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), getString(R.string.error_invalid_date_format), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = CreateCouponRequest(
                coupon_code = couponCode,
                type = type,
                value = value,
                min_price = minPrice,
                user_count = userCount,
                start_date = startDate,
                end_date = endDate
            )
            adminViewModel.createCoupon(request)
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun showDatePickerDialog(targetEditText: com.google.android.material.textfield.TextInputEditText) {
        val calendar = Calendar.getInstance()
        val existingDate = targetEditText.text.toString()
        if (existingDate.isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = dateFormat.parse(existingDate)
                date?.let { calendar.time = it }
            } catch (e: Exception) {
                // اگر فرمت تاریخ موجود صحیح نبود، تاریخ فعلی را نشان بده
            }
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format(Locale.getDefault(), "%02d", selectedMonth + 1)
                val formattedDay = String.format(Locale.getDefault(), "%02d", selectedDay)
                val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                targetEditText.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun observeViewModel() {
        adminViewModel.couponCreateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBarCreateCoupon.visibility = View.VISIBLE
                    binding.btnCreateCoupon.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBarCreateCoupon.visibility = View.GONE
                    binding.btnCreateCoupon.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.coupon_created_successfully), Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                is Result.Failure -> {
                    binding.progressBarCreateCoupon.visibility = View.GONE
                    binding.btnCreateCoupon.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.error_creating_coupon) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}