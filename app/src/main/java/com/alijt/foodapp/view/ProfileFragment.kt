package com.alijt.foodapp.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider // Import ViewModelProvider
import com.alijt.foodapp.R // Import R
import com.alijt.foodapp.databinding.FragmentProfileBinding
import com.alijt.foodapp.model.ProfileUpdateRequest
import com.alijt.foodapp.model.User
import com.alijt.foodapp.repository.AuthRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.AuthViewModel
import com.alijt.foodapp.viewmodel.AuthViewModelFactory
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    private var selectedImageUri: Uri? = null
    private var profileImageBase64String: String? = null

    // ActivityResultLauncher for picking image from gallery
    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.ivProfilePicture.setImageURI(it) // Display selected image
                convertImageToBase64(it) // Convert to Base64
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SessionManager and AuthViewModel
        sessionManager = SessionManager(requireContext())
        val repository = AuthRepository()
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(repository, sessionManager))
            .get(AuthViewModel::class.java)

        // --- Fetch User Profile Data ---
        authViewModel.fetchUserProfile()

        authViewModel.userProfile.observe(viewLifecycleOwner) { result ->
            result.onSuccess { user ->
                displayProfileData(user)
            }.onFailure { exception ->
                Toast.makeText(requireContext(), getString(R.string.failed_to_load_profile), Toast.LENGTH_LONG).show()
            }
        }

        // --- Handle Image Selection ---
        binding.btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*") // Launch gallery to pick an image
        }

        // --- Handle Save Changes Button Click ---
        binding.btnSaveChanges.setOnClickListener {
            saveProfileChanges()
        }

        // --- Observe Profile Update Result ---
        authViewModel.profileUpdateResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { messageResponse ->
                Toast.makeText(requireContext(), getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                // Optionally re-fetch profile to ensure UI is up-to-date
                authViewModel.fetchUserProfile()
            }.onFailure { exception ->
                Toast.makeText(requireContext(), getString(R.string.failed_to_update_profile), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun displayProfileData(user: User) {
        binding.etFullNameProfile.setText(user.fullName)
        binding.etPhoneProfile.setText(user.phone)
        binding.etEmailProfile.setText(user.email)
        binding.etAddressProfile.setText(user.address)

        user.bank_info?.let { bankInfo ->
            binding.etBankNameProfile.setText(bankInfo.bank_name)
            binding.etAccountNumberProfile.setText(bankInfo.account_number)
        }

        // Load profile image if URL is available (using a library like Glide/Picasso is recommended)
        user.profileImageUrl?.let { imageUrl ->
            // TODO: Use an image loading library (e.g., Glide, Picasso) to load image from URL
            // For now, if profileImageBase64 is available, display it
            // val decodedImage = Base64.decode(user.profileImageBase64, Base64.DEFAULT)
            // val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
            // binding.ivProfilePicture.setImageBitmap(bitmap)
        }
    }

    private fun saveProfileChanges() {
        val fullName = binding.etFullNameProfile.text.toString().trim()
        val phone = binding.etPhoneProfile.text.toString().trim()
        val email = binding.etEmailProfile.text.toString().trim()
        val address = binding.etAddressProfile.text.toString().trim()
        val bankName = binding.etBankNameProfile.text.toString().trim()
        val accountNumber = binding.etAccountNumberProfile.text.toString().trim()

        // Basic validation (can be expanded)
        if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.fill_all_required_fields), Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show()
            return
        }

        val bankInfo = if (bankName.isNotEmpty() && accountNumber.isNotEmpty()) {
            com.alijt.foodapp.model.BankInfo(bankName, accountNumber)
        } else {
            null
        }

        val updateRequest = ProfileUpdateRequest(
            fullName = fullName,
            phone = phone,
            email = if (email.isEmpty()) null else email, // Send null if empty
            address = address,
            profileImageBase64 = profileImageBase64String, // Use the Base64 string from image selection
            bank_info = bankInfo
        )

        authViewModel.updateUserProfile(updateRequest)
    }

    private fun convertImageToBase64(uri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        inputStream?.use {
            val bitmap = BitmapFactory.decodeStream(it)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val byteArray = outputStream.toByteArray()
            profileImageBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
        } ?: run {
            Toast.makeText(requireContext(), "Failed to convert image.", Toast.LENGTH_SHORT).show()
            profileImageBase64String = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}