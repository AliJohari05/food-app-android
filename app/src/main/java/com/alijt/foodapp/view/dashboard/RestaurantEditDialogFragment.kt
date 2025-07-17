package com.alijt.foodapp.view.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.FragmentRestaurantEditDialogBinding
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.SellerRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.SellerViewModel
import com.alijt.foodapp.viewmodel.SellerViewModelFactory

class RestaurantEditDialogFragment : DialogFragment() {

    private var _binding: FragmentRestaurantEditDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var sellerViewModel: SellerViewModel
    private var currentRestaurant: Restaurant? = null

    companion object {
        const val ARG_RESTAURANT = "restaurant_object"
        fun newInstance(restaurant: Restaurant): RestaurantEditDialogFragment {
            val fragment = RestaurantEditDialogFragment()
            val args = Bundle().apply {
                putParcelable(ARG_RESTAURANT, restaurant)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentRestaurant = it.getParcelable(ARG_RESTAURANT) // دریافت شیء رستوران
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantEditDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext())
        val sellerRepository = SellerRepository(apiService, sessionManager)
        sellerViewModel = ViewModelProvider(requireActivity(), SellerViewModelFactory(sellerRepository, sessionManager))
            .get(SellerViewModel::class.java)

        populateRestaurantData()
        setupListeners()
        observeViewModel()
    }

    private fun populateRestaurantData() {
        currentRestaurant?.let { restaurant ->
            binding.etRestaurantName.setText(restaurant.name)
            binding.etRestaurantAddress.setText(restaurant.address)
            binding.etRestaurantPhone.setText(restaurant.phone)
            binding.etRestaurantLogoBase64.setText(restaurant.logobase64)
            binding.etRestaurantTaxFee.setText(restaurant.taxFee?.toString() ?: "")
            binding.etRestaurantAdditionalFee.setText(restaurant.additionalFee?.toString() ?: "")
        }
    }

    private fun setupListeners() {
        binding.btnSaveChanges.setOnClickListener {
            currentRestaurant?.let { restaurant ->
                val name = binding.etRestaurantName.text.toString().trim()
                val address = binding.etRestaurantAddress.text.toString().trim()
                val phone = binding.etRestaurantPhone.text.toString().trim()
                val logoBase64 = binding.etRestaurantLogoBase64.text.toString().trim()
                val taxFee = binding.etRestaurantTaxFee.text.toString().toIntOrNull()
                val additionalFee = binding.etRestaurantAdditionalFee.text.toString().toIntOrNull()

                if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.error_fill_required_restaurant_fields), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (phone.length < 10) {
                    Toast.makeText(requireContext(), getString(R.string.error_invalid_phone_format), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (taxFee != null && taxFee < 0) {
                    Toast.makeText(requireContext(), getString(R.string.error_negative_tax_fee), Toast.LENGTH_SHORT).show();
                    return@setOnClickListener
                }
                if (additionalFee != null && additionalFee < 0) {
                    Toast.makeText(requireContext(), getString(R.string.error_negative_additional_fee), Toast.LENGTH_SHORT).show();
                    return@setOnClickListener
                }

                val updatedRestaurant = Restaurant(
                    id = restaurant.id,
                    name = name,
                    address = address,
                    phone = phone,
                    logobase64 = if (logoBase64.isEmpty()) null else logoBase64,
                    taxFee = taxFee,
                    additionalFee = additionalFee
                )
                sellerViewModel.updateRestaurant(updatedRestaurant)
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun observeViewModel() {
        sellerViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBarEditRestaurant.visibility = View.VISIBLE
                    binding.btnSaveChanges.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBarEditRestaurant.visibility = View.GONE
                    binding.btnSaveChanges.isEnabled = true
                    val message = if (result.data is com.alijt.foodapp.model.MessageResponse) result.data.message else result.data.toString()
                    Toast.makeText(requireContext(), getString(R.string.restaurant_updated_successfully) + ": $message", Toast.LENGTH_SHORT).show()
                    dismiss() // بستن دیالوگ پس از موفقیت
                }
                is Result.Failure -> {
                    binding.progressBarEditRestaurant.visibility = View.GONE
                    binding.btnSaveChanges.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.error_updating_restaurant) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}