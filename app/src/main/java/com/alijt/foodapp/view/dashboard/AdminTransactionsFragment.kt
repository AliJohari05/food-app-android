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
import com.alijt.foodapp.adapter.TransactionAdapter
import com.alijt.foodapp.databinding.FragmentAdminTransactionsBinding // باید ایجاد شود
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.AdminRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.AdminViewModel
import com.alijt.foodapp.viewmodel.AdminViewModelFactory

class AdminTransactionsFragment : Fragment() {

    private var _binding: FragmentAdminTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext())
        val adminRepository = AdminRepository(apiService)
        val factory = AdminViewModelFactory(adminRepository, sessionManager)
        adminViewModel = ViewModelProvider(requireActivity(), factory).get(AdminViewModel::class.java) // ViewModel مشترک

        setupRecyclerView()
        observeViewModels()

        adminViewModel.fetchAllTransactions() // دریافت داده‌ها
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter { transaction ->
            Toast.makeText(requireContext(), getString(R.string.transaction_clicked_message, transaction.id), Toast.LENGTH_SHORT).show()
        }
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

    private fun observeViewModels() {
        adminViewModel.transactionsList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBarTransactions.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    transactionAdapter.submitList(result.data)
                    binding.progressBarTransactions.visibility = View.GONE
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_fetching_transactions) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                    binding.progressBarTransactions.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}