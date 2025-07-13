package com.alijt.foodapp.view.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.FragmentBuyerDashboardBinding
import com.alijt.foodapp.view.ProfileFragment // Import ProfileFragment

class BuyerDashboardFragment : Fragment() {

    private var _binding: FragmentBuyerDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuyerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvBuyerDashboard.text = getString(R.string.buyer_dashboard_title)

        // Handle View Profile Button Click
        binding.btnViewProfile.setOnClickListener {
            // Replace the current fragment with ProfileFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null) // Allows going back to BuyerDashboardFragment
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}