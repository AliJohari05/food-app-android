package com.alijt.foodapp.view.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider // در آینده برای ViewModel نیاز است
import androidx.navigation.fragment.findNavController // برای ناوبری به پروفایل
import androidx.viewpager2.adapter.FragmentStateAdapter // برای ViewPager2
import com.alijt.foodapp.R // برای دسترسی به رشته‌ها
import com.alijt.foodapp.databinding.FragmentSellerDashboardBinding // برای ViewBinding
import com.alijt.foodapp.network.RetrofitClient // در آینده برای ViewModel نیاز است
import com.alijt.foodapp.repository.AuthRepository // در آینده برای ViewModel نیاز است
import com.alijt.foodapp.utils.SessionManager // در آینده برای ViewModel نیاز است
import com.alijt.foodapp.viewmodel.AuthViewModel // در آینده برای ViewModel نیاز است
import com.alijt.foodapp.viewmodel.AuthViewModelFactory // در آینده برای ViewModel نیاز است
import com.google.android.material.tabs.TabLayoutMediator // برای اتصال TabLayout به ViewPager2

class SellerDashboardFragment : Fragment() {

    private var _binding: FragmentSellerDashboardBinding? = null
    private val binding get() = _binding!!

    // ViewModel در مراحل بعدی اضافه خواهد شد
    // private lateinit var sellerViewModel: SellerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel در مراحل بعدی مقداردهی می‌شود
        // val apiService = RetrofitClient.instance
        // val sessionManager = SessionManager(requireContext())
        // val sellerRepository = SellerRepository(apiService, sessionManager) // باید ایجاد شود
        // sellerViewModel = ViewModelProvider(this, SellerViewModelFactory(sellerRepository)).get(SellerViewModel::class.java)

        setupViewPagerAndTabs()
        setupProfileButton()
    }

    private fun setupViewPagerAndTabs() {
        val tabTitles = arrayOf(
            getString(R.string.my_restaurants_tab_title), // این رشته‌ها را در strings.xml اضافه کنید
            getString(R.string.seller_orders_tab_title),
            getString(R.string.seller_menus_tab_title),
            getString(R.string.seller_coupons_tab_title)
        )

        binding.viewPagerSeller.adapter = SellerPagerAdapter(this)

        TabLayoutMediator(binding.tabLayoutSeller, binding.viewPagerSeller) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupProfileButton() {
        binding.btnSellerViewProfile.setOnClickListener {
            try {
                // فرض می‌کنیم اکشن ناوبری از sellerDashboardFragment به profileFragment در nav_graph.xml تعریف شده است
                findNavController().navigate(R.id.action_sellerDashboardFragment_to_profileFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), getString(R.string.error_navigation_profile) + ": ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Pager Adapter برای ViewPager2
    private inner class SellerPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 4 // تعداد تب‌ها (رستوران‌ها، سفارشات، منوها، کوپن‌ها)

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MyRestaurantsFragment() // این Fragmentها در مراحل بعدی ایجاد می‌شوند
                1 -> SellerOrdersFragment()
                2 -> SellerMenusFragment()
                3 -> SellerCouponsFragment()
                else -> throw IllegalArgumentException("Invalid tab position")
            }
        }
    }
}