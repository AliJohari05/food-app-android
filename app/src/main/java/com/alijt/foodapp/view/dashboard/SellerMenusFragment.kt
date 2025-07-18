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
import com.alijt.foodapp.adapter.FoodItemAdapter
import com.alijt.foodapp.databinding.FragmentSellerMenusBinding
import com.alijt.foodapp.model.FoodItem
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.SellerRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.SellerViewModel
import com.alijt.foodapp.viewmodel.SellerViewModelFactory

class SellerMenusFragment : Fragment() {

    private var _binding: FragmentSellerMenusBinding? = null
    private val binding get() = _binding!!
    private lateinit var sellerViewModel: SellerViewModel
    private lateinit var foodItemAdapter: FoodItemAdapter
    private var currentRestaurantId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerMenusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext())
        val sellerRepository = SellerRepository(apiService, sessionManager)
        sellerViewModel = ViewModelProvider(requireActivity(), SellerViewModelFactory(sellerRepository, sessionManager))
            .get(SellerViewModel::class.java)

        // اینجا باید شناسه رستوران انتخاب شده را دریافت کنید.
        // مثلاً از طریق Bundle اگر از MyRestaurantsFragment ناوبری می‌کنید
        // یا اگر فقط یک رستوران دارید، آن را از SellerViewModel.myRestaurants.value.first() بگیرید.
        // برای شروع، فرض می‌کنیم یک رستوران انتخاب شده وجود دارد.
        // sellerViewModel.myRestaurants.observe(viewLifecycleOwner) { result ->
        //     if (result is Result.Success && result.data.isNotEmpty()) {
        //         currentRestaurantId = result.data.first().id // اولین رستوران را انتخاب کن
        //         currentRestaurantId?.let { sellerViewModel.fetchRestaurantFoodItems(it) }
        //     }
        // }


        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // در اینجا، بعد از اینکه رستوران انتخاب شد، آیتم‌های آن را دریافت می‌کنیم.
        // فعلاً به صورت دستی currentRestaurantId را ست می‌کنیم.
        currentRestaurantId = 1 // TODO: این را با ID رستوران واقعی جایگزین کنید
        currentRestaurantId?.let { sellerViewModel.fetchRestaurantFoodItems(it) }
    }

    private fun setupRecyclerView() {
        foodItemAdapter = FoodItemAdapter(
            onItemClick = { foodItem: FoodItem -> // <-- اینجا نوع پارامتر به صراحت مشخص شد
                Toast.makeText(requireContext(), getString(R.string.food_item_clicked, foodItem.name), Toast.LENGTH_SHORT).show()
            },
            onEditClick = { foodItem: FoodItem -> // <-- اینجا نوع پارامتر به صراحت مشخص شد
                Toast.makeText(requireContext(), getString(R.string.edit_food_item_clicked, foodItem.name), Toast.LENGTH_SHORT).show()
                // نمایش دیالوگ ویرایش آیتم غذایی
            },
            onDeleteClick = { foodItemId: Int -> // <-- اینجا نوع پارامتر به صراحت مشخص شد
                Toast.makeText(requireContext(), getString(R.string.delete_food_item_confirm, foodItemId), Toast.LENGTH_SHORT).show()
                currentRestaurantId?.let { restaurantId ->
                    showDeleteFoodItemConfirmationDialog(restaurantId, foodItemId)
                } ?: Toast.makeText(requireContext(), getString(R.string.error_no_restaurant_selected), Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvSellerMenus.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodItemAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddMenuOrItem.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.add_menu_or_item_clicked), Toast.LENGTH_SHORT).show()
            // اینجا می‌توانید دیالوگی برای انتخاب "افزودن آیتم غذایی" یا "افزودن دسته‌بندی منو" نمایش دهید
        }
    }

    private fun observeViewModel() {
        sellerViewModel.restaurantFoodItems.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { binding.progressBarSellerMenus.visibility = View.VISIBLE }
                is Result.Success<*> -> {
                    val data = result.data
                    if (data is List<*>) {
                        val foodItemList = data as List<FoodItem>
                        foodItemAdapter.submitList(foodItemList)
                        if (foodItemList.isEmpty()) {
                            Toast.makeText(requireContext(), getString(R.string.no_food_items_found), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.error_unexpected_data_format), Toast.LENGTH_LONG).show()
                    }
                    binding.progressBarSellerMenus.visibility = View.GONE
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_fetching_food_items) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                    binding.progressBarSellerMenus.visibility = View.GONE
                }
            }
        }

        sellerViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success<*> -> {
                    val message = if (result.data is com.alijt.foodapp.model.MessageResponse) (result.data as com.alijt.foodapp.model.MessageResponse).message else result.data.toString()
                    Toast.makeText(requireContext(), getString(R.string.operation_successful) + ": $message", Toast.LENGTH_SHORT).show()
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

    private fun showDeleteFoodItemConfirmationDialog(restaurantId: Int, foodItemId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_food_item_dialog_title))
            .setMessage(getString(R.string.delete_food_item_dialog_message, foodItemId))
            .setPositiveButton(getString(R.string.delete_button)) { dialog, _ ->
                sellerViewModel.deleteFoodItem(restaurantId, foodItemId)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}