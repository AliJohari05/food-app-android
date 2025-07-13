package com.alijt.foodapp.view.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.FragmentSellerDashboardBinding

class SellerDashboardFragment : Fragment() {
    private var _binding: FragmentSellerDashboardBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentSellerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set the text of a TextView in the layout
        binding.tvSellerDashboard.text = getString(R.string.seller_dashboard_title)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear up the binding when the view is destroyed
    }

}