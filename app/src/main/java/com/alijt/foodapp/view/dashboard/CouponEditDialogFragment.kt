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
import com.alijt.foodapp.databinding.FragmentCouponEditDialogBinding // باید ایجاد شود
import com.alijt.foodapp.model.Coupon
import com.alijt.foodapp.model.UpdateCouponRequest
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.AdminRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.AdminViewModel
import com.alijt.foodapp.viewmodel.AdminViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CouponEditDialogFragment : DialogFragment() {

    private var _binding: FragmentCouponEditDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminViewModel: AdminViewModel
    private var currentCoupon: Coupon? = null

    // تگ برای آرگومان‌های Fragment
    companion object {
        const val ARG_COUPON = "coupon_object"
        fun newInstance(coupon: Coupon): CouponEditDialogFragment {
            val fragment = CouponEditDialogFragment()
            val args = Bundle().apply {
                putParcelable(ARG_COUPON, coupon) // مطمئن شوید Coupon قابل Parcelable باشد
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentCoupon = it.getParcelable(ARG_COUPON) // دریافت شیء کوپن
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCouponEditDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext())
        val adminRepository = AdminRepository(apiService)
        adminViewModel = ViewModelProvider(requireActivity(), AdminViewModelFactory(adminRepository, sessionManager))
            .get(AdminViewModel::class.java)

        setupSpinners()
        populateCouponData() // پر کردن فیلدها با اطلاعات کوپن
        setupListeners()
        observeViewModel()
    }

    private fun setupSpinners() {
        val couponTypes = arrayOf("fixed", "percent")
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, couponTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCouponType.adapter = typeAdapter
    }

    private fun populateCouponData() {
        currentCoupon?.let { coupon ->
            binding.etCouponCode.setText(coupon.coupon_code)
            binding.etCouponCode.isEnabled = false // کد کوپن معمولا قابل ویرایش نیست

            val typePosition = (binding.spinnerCouponType.adapter as ArrayAdapter<String>).getPosition(coupon.type)
            binding.spinnerCouponType.setSelection(typePosition)

            binding.etCouponValue.setText(coupon.value.toString())
            binding.etMinPrice.setText(coupon.min_price.toString())
            binding.etUserCount.setText(coupon.user_count.toString())
            binding.etStartDate.setText(coupon.start_date)
            binding.etEndDate.setText(coupon.end_date)
        }
    }

    private fun setupListeners() {
        binding.etStartDate.setOnClickListener {
            showDatePickerDialog(binding.etStartDate)
        }

        binding.etEndDate.setOnClickListener {
            showDatePickerDialog(binding.etEndDate)
        }

        binding.btnSaveChanges.setOnClickListener {
            currentCoupon?.let { coupon ->
                val newCouponCode = binding.etCouponCode.text.toString().trim()
                val newType = binding.spinnerCouponType.selectedItem.toString()
                val newValue = binding.etCouponValue.text.toString().toDoubleOrNull()
                val newMinPrice = binding.etMinPrice.text.toString().toIntOrNull()
                val newUserCount = binding.etUserCount.text.toString().toIntOrNull()
                val newStartDate = binding.etStartDate.text.toString().trim()
                val newEndDate = binding.etEndDate.text.toString().trim()

                if (newCouponCode.isEmpty() || newValue == null || newMinPrice == null || newUserCount == null || newStartDate.isEmpty() || newEndDate.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                try {
                    dateFormat.parse(newStartDate)
                    dateFormat.parse(newEndDate)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), getString(R.string.error_invalid_date_format), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val updateRequest = UpdateCouponRequest(
                    coupon_code = newCouponCode,
                    type = newType,
                    value = newValue,
                    min_price = newMinPrice,
                    user_count = newUserCount,
                    start_date = newStartDate,
                    end_date = newEndDate
                )
                adminViewModel.updateCoupon(coupon.id.toString(), updateRequest)
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun showDatePickerDialog(targetEditText: com.google.android.material.textfield.TextInputEditText) {
        val calendar = Calendar.getInstance()
        // سعی کنید تاریخ موجود در EditText را به عنوان تاریخ پیش فرض تقویم تنظیم کنید
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
        adminViewModel.couponUpdateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBarEditCoupon.visibility = View.VISIBLE
                    binding.btnSaveChanges.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBarEditCoupon.visibility = View.GONE
                    binding.btnSaveChanges.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.coupon_updated_successfully), Toast.LENGTH_SHORT).show()
                    dismiss() // بستن دیالوگ پس از موفقیت
                }
                is Result.Failure -> {
                    binding.progressBarEditCoupon.visibility = View.GONE
                    binding.btnSaveChanges.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.error_updating_coupon) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}