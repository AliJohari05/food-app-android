package com.alijt.foodapp.view.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.FragmentAdminDashboardBinding
import com.alijt.foodapp.network.RetrofitClient
import com.alijt.foodapp.repository.AdminRepository
import com.alijt.foodapp.utils.SessionManager // <-- اضافه شد
import com.alijt.foodapp.viewmodel.AdminViewModel
import com.alijt.foodapp.viewmodel.AdminViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminViewModel: AdminViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitClient.instance
        val sessionManager = SessionManager(requireContext()) // <-- sessionManager تعریف شد
        val adminRepository = AdminRepository(apiService, sessionManager) // <-- sessionManager به AdminRepository پاس داده شد
        adminViewModel = ViewModelProvider(requireActivity(), AdminViewModelFactory(adminRepository, sessionManager)) // <-- sessionManager به Factory پاس داده شد
            .get(AdminViewModel::class.java)

        setupViewPagerAndTabs()
        setupProfileButton()
    }

    private fun setupViewPagerAndTabs() {
        val tabTitles = arrayOf(
            getString(R.string.users_tab_title),
            getString(R.string.orders_tab_title),
            getString(R.string.transactions_tab_title),
            getString(R.string.coupons_tab_title)
        )

        binding.viewPagerAdmin.adapter = AdminPagerAdapter(this)

        TabLayoutMediator(binding.tabLayoutAdmin, binding.viewPagerAdmin) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupProfileButton() {
        binding.btnViewProfile.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_adminDashboardFragment_to_profileFragment)
            } catch (e: Exception) {
                Log.e("AdminDashboard", "Navigation to profile failed: ${e.message}", e)
                Toast.makeText(requireContext(), getString(R.string.error_navigation_profile) + ": ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class AdminPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AdminUsersFragment()
                1 -> AdminOrdersFragment()
                2 -> AdminTransactionsFragment()
                3 -> AdminCouponsFragment()
                else -> throw IllegalArgumentException("Invalid tab position")
            }
        }
    }
}