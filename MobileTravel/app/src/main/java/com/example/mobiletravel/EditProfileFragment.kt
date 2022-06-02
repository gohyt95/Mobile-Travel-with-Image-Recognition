package com.example.mobiletravel

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobiletravel.baseclass.User
import com.example.mobiletravel.databinding.FragmentChangePasswordBinding
import com.example.mobiletravel.databinding.FragmentEditProfileBinding
import com.example.mobiletravel.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val myCalendar = Calendar.getInstance()
    private val phonePattern = "^(01)[0-46-9]*[0-9]{7,8}\$"
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    }
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Updating profile...")
        progressDialog.setCancelable(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner, {
            it?.let {
                binding.phoneText.setText(it.phone)
                binding.nameText.setText(it.name)
                binding.dobText.setText(it.dob)
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let{
                if(it){
                    if(progressDialog.isShowing)
                        progressDialog.dismiss()
                    Toast.makeText(requireContext(),"Update profile failed", Toast.LENGTH_LONG).show()
                }
            }
        })

        val date =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateLabel()
            }

        binding.dobText.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        binding.editProfileBtn.setOnClickListener {
            binding.dobText.clearFocus()
            binding.nameText.clearFocus()
            binding.phoneText.clearFocus()

            val phone = binding.phoneText.text
            val dob = binding.dobText.text
            val name = binding.nameText.text

            if (phone.isNotBlank() &&
                name.isNotBlank() &&
                dob.isNotBlank()
            ) {
                if (!validatePhone(binding.phoneText)) {
                    Toast.makeText(
                        requireContext(), "Please provide a valid phone number",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    progressDialog.show()
                    lifecycleScope.launch {
                        val user = User(
                            email = viewModel.user.value!!.email,
                            password = viewModel.user.value!!.password,
                            phone = phone.toString(),
                            dob = dob.toString(),
                            name = name.toString()
                        )
                        viewModel.onUpdateProfile(user)
                        if(progressDialog.isShowing)
                            progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }else {
                Toast.makeText(requireContext(), "Please fill in all field.", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    private fun updateLabel() {
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.dobText.setText(dateFormat.format(myCalendar.time))
    }

    private fun validatePhone(view: TextView): Boolean {
        return view.text.matches(phonePattern.toRegex())
    }
}