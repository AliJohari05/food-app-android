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
// import com.alijt.foodapp.adapter.MenuAdapter // ممکن است نیاز به آداپتر خاص منوها داشته باشید
import com.alijt.foodapp.databinding.FragmentSellerMenusBinding // باید ایجاد شود
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
    // private lateinit var menuAdapter: MenuAdapter // نیاز به تعریف و مقداردهی

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

        setupRecyclerView()
        observeViewModel()

        // دریافت لیست منوها (در آینده متد fetchSellerMenus را در SellerViewModel اضافه خواهیم کرد)
        // sellerViewModel.fetchSellerMenus()
    }

    private fun setupRecyclerView() {
        // menuAdapter = MenuAdapter { menuOrItem ->
        //     Toast.makeText(requireContext(), "Menu/Item clicked: $menuOrItem", Toast.LENGTH_SHORT).show()
        // }
        // binding.rvSellerMenus.apply {
        //     layoutManager = LinearLayoutManager(context)
        //     adapter = menuAdapter
        // }
    }

    private fun observeViewModel() {
        // مشاهده لیست منوها از SellerViewModel
        // sellerViewModel.sellerMenus.observe(viewLifecycleOwner) { result ->
        //     when (result) {
        //         is Result.Loading -> { binding.progressBarSellerMenus.visibility = View.VISIBLE }
        //         is Result.Success -> {
        //             menuAdapter.submitList(result.data)
        //             binding.progressBarSellerMenus.visibility = View.GONE
        //         }
        //         is Result.Failure -> {
        //             Toast.makeText(requireContext(), getString(R.string.error_fetching_menus) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
        //             binding.progressBarSellerMenus.visibility = View.GONE
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