package com.example.mobiletravel

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobiletravel.baseclass.User
import com.example.mobiletravel.databinding.FragmentChangePasswordBinding
import com.example.mobiletravel.databinding.FragmentProfileBinding
import com.example.mobiletravel.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by lazy{
        ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    }
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Updating password...")
        progressDialog.setCancelable(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let{
                if(it){
                    if(progressDialog.isShowing)
                        progressDialog.dismiss()
                    Toast.makeText(requireContext(),"Update password failed", Toast.LENGTH_LONG).show()
                }
            }
        })

        binding.confirmBtn.setOnClickListener {
            binding.newPwText.clearFocus()
            binding.newPwConfirmText.clearFocus()
            binding.oldPwText.clearFocus()
            val oldPw = binding.oldPwText.text.toString()
            val newPw = binding.newPwText.text.toString()
            val cNewPw = binding.newPwConfirmText.text.toString()

            if(oldPw.isNotBlank() &&
               newPw.isNotBlank() &&
               cNewPw.isNotBlank()){
                if (!newPw.contentEquals(cNewPw) ||
                    oldPw.contentEquals(newPw) ||
                    !oldPw.contentEquals(viewModel.user.value!!.password) ||
                    newPw.count() < 8 ||
                    newPw.count() > 20
                ) {
                    if (!newPw.contentEquals(cNewPw))
                        Toast.makeText(
                            requireContext(), "Confirm password must same with password",
                            Toast.LENGTH_LONG
                        ).show()
                    if(!oldPw.contentEquals(viewModel.user.value!!.password))
                        Toast.makeText(
                            requireContext(), "Old password is wrong",
                            Toast.LENGTH_LONG
                        ).show()
                    if(oldPw.contentEquals(newPw))
                        Toast.makeText(
                            requireContext(), "New password must not same with old password",
                            Toast.LENGTH_LONG
                        ).show()
                    if (newPw.count() < 8)
                        Toast.makeText(
                            requireContext(), "Password must contain 8 characters or above.",
                            Toast.LENGTH_LONG
                        ).show()
                    if (newPw.count() > 20)
                        Toast.makeText(
                            requireContext(), "Password must not contain more than 20 characters.",
                            Toast.LENGTH_LONG
                        ).show()
                }else{
                    progressDialog.show()
                    lifecycleScope.launch {
                        val user = User(
                            email = viewModel.user.value!!.email,
                            password = newPw,
                            phone = viewModel.user.value!!.phone,
                            dob = viewModel.user.value!!.dob,
                            name = viewModel.user.value!!.name
                        )
                        viewModel.onUpdatePassword(user)
                        if(progressDialog.isShowing)
                            progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }

        }


    }

}