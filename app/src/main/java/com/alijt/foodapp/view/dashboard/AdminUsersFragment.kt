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
        val adminRepository = AdminRepository(apiService)
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
                is Result.Success -> {
                    userListAdapter.submitList(result.data)
                    binding.progressBarUsers.visibility = View.GONE
                }
                is Result.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_fetching_users) + ": ${result.exception.message}", Toast.LENGTH_LONG).show()
                    binding.progressBarUsers.visibility = View.GONE
                }
            }
        }

        adminViewModel.userStatusUpdateResult.observe(viewLifecycleOwner) { result -> // <-- observe تغییر می‌کند
            when (result) {
                is Result.Loading -> {
                    // نمایش لودینگ
                }
                is Result.Success -> {
                    Toast.makeText(requireContext(), result.data, Toast.LENGTH_SHORT).show()
                    // لیست کاربران پس از به‌روزرسانی در ViewModel رفرش می‌شود.
                    // adminViewModel.fetchAllUsers() // این خط نیازی نیست اگر ViewModel خودش رفرش می‌کند
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