package com.example.mobiletravel

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.mobiletravel.databinding.FragmentSearchResultBinding
import com.example.mobiletravel.databinding.FragmentSignUpBinding
import com.example.mobiletravel.viewmodel.PlaceViewModel
import java.util.*
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobiletravel.baseclass.User
import com.example.mobiletravel.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val myCalendar = Calendar.getInstance()
    private lateinit var progressDialog: ProgressDialog
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private val viewModel: ProfileViewModel by lazy{
        ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Signing up...")
        progressDialog.setCancelable(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val date =
            OnDateSetListener { view, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateLabel()
            }

        viewModel.user.observe(viewLifecycleOwner) {
            it?.let {
                if (progressDialog.isShowing)
                    progressDialog.dismiss()
                Toast.makeText(requireContext(), "Signed up successfully", Toast.LENGTH_LONG)
                    .show()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    if (progressDialog.isShowing)
                        progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Error: Sign up failed.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }


        binding.dobRegText.setOnClickListener{
            DatePickerDialog(
                requireContext(),
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        binding.signUpRegBtn.setOnClickListener {
            binding.emailRegText.clearFocus()
            binding.nameRegText.clearFocus()
            binding.dobRegText.clearFocus()
            binding.phoneRegText.clearFocus()
            binding.pwRegText.clearFocus()
            binding.pwConfirmText.clearFocus()

            if (binding.emailRegText.text.isNotBlank() &&
                binding.nameRegText.text.isNotBlank() &&
                binding.dobRegText.text.isNotBlank() &&
                binding.phoneRegText.text.isNotBlank() &&
                binding.pwRegText.text.isNotBlank() &&
                binding.pwConfirmText.text.isNotBlank()
            ) {
                if (!binding.pwRegText.text.contentEquals(binding.pwConfirmText.text) ||
                    binding.pwRegText.text.count() < 8 ||
                    binding.pwRegText.text.count() > 20
                ) {
                    if (!binding.pwRegText.text.contentEquals(binding.pwConfirmText.text))
                        Toast.makeText(
                            requireContext(), "Confirm password must same with password",
                            Toast.LENGTH_LONG
                        ).show()
                    if (binding.pwRegText.text.count() < 8)
                        Toast.makeText(
                            requireContext(), "Password must contain 8 characters or above.",
                            Toast.LENGTH_LONG
                        ).show()
                    if (binding.pwRegText.text.count() > 20)
                        Toast.makeText(
                            requireContext(), "Password must not contain more than 20 characters.",
                            Toast.LENGTH_LONG
                        ).show()
                } else if (!validateEmail(binding.emailRegText)) {
                    Toast.makeText(
                        requireContext(), "Please provide a valid email address",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val email = binding.emailRegText.text.toString()
                    val password = binding.pwRegText.text.toString()
                    val name = binding.nameRegText.text.toString()
                    val dob = binding.dobRegText.text.toString()
                    val phone = binding.phoneRegText.text.toString()

                    val user = User(
                        dob = dob,
                        email = email,
                        password = password,
                        name = name,
                        phone = phone
                    )

                    progressDialog.show()
                    viewModel.onSignUp(user)
                }

            } else {
                Toast.makeText(requireContext(), "Please fill in all field.", Toast.LENGTH_LONG)
                    .show()
            }

        }

    }

    private fun updateLabel() {
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.dobRegText.setText(dateFormat.format(myCalendar.time))
    }

    private fun validateEmail(view: TextView): Boolean {
        return view.text.matches(emailPattern.toRegex())
    }


}