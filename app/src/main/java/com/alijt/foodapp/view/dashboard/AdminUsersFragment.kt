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
import com.alijt.foodapp.adapter.UserListAdapter
import com.alijt.foodapp.databinding.FragmentAdminUsersBinding
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.AdminRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.AdminViewModel
import com.alijt.foodapp.viewmodel.AdminViewModelFactory

class AdminUsersFragment : Fragment() {

    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var userListAdapter: UserListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext())
        val adminRepository = AdminRepository(apiService, sessionManager)
        adminViewModel = ViewModelProvider(requireActivity(), AdminViewModelFactory(adminRepository, sessionManager))
            .get(AdminViewModel::class.java)

        setupRecyclerView()
        observeViewModels()

        adminViewModel.fetchAllUsers()
    }

    private fun setupRecyclerView() {
        userListAdapter = UserListAdapter { user, newStatus ->
            adminViewModel.updateUserStatus(user.id.toString(), newStatus)
        }
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userListAdapter
        }
    }

    private fun observeViewModels() {
        adminViewModel.usersList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { binding.progressBarUsers.visibility = View.VISIBLE }
                is Result.Success<*> -> {
                    val data = result.data
                    if (data is List<*>) {
                        val userList = data as List<com.alijt.foodapp.model.User>
                        userListAdapter.submitList(userList)
                        if (userList.isEmpty()) {
                            Toast.makeText(requireContext(), getString(R.string.no_users_found), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.error_unexpected_data_format), Toast.LENGTH_LONG).show()
                    }
                    binding.progressBarUsers.visibility = View.GONE
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_fetching_users) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                    binding.progressBarUsers.visibility = View.GONE
                }
            }
        }

        // <-- اینجا اصلاح شد: result.data به String تبدیل می‌شود -->
        adminViewModel.userStatusUpdateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { /* نمایش لودینگ */ }
                is Result.Success<*> -> { // <-- استفاده از <*>
                    val message = result.data // اینجا message از نوع Any? است (که اکنون انتظار String را داریم)
                    if (message is String) { // <-- بررسی نوع
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.operation_successful), Toast.LENGTH_SHORT).show() // Fallback message
                    }
                    adminViewModel.fetchAllUsers() // رفرش لیست
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_updating_user_status) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}