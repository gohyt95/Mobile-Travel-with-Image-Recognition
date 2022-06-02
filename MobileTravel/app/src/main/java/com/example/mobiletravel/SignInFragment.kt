package com.example.mobiletravel

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.mobiletravel.baseclass.Place
import com.example.mobiletravel.baseclass.User
import com.example.mobiletravel.database.getDatabase
import com.example.mobiletravel.databinding.FragmentSignInBinding
import com.example.mobiletravel.databinding.FragmentSignUpBinding
import com.example.mobiletravel.network.TravelApi
import com.example.mobiletravel.repository.UserRepository
import com.example.mobiletravel.viewmodel.PlaceViewModel
import com.example.mobiletravel.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by lazy{
        ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    }
    private lateinit var progressDialog: ProgressDialog
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Signing in...")
        progressDialog.setCancelable(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner, {
            it?.let{
                if(progressDialog.isShowing)
                    progressDialog.dismiss()
                findNavController().popBackStack()
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            it?.let{
                if(it){
                    if(progressDialog.isShowing)
                        progressDialog.dismiss()
                    Toast.makeText(requireContext(),"Wrong email or password, please try again.", Toast.LENGTH_LONG).show()
                }
            }
        })


        binding.signInBtn.setOnClickListener{
            binding.emailText.clearFocus()
            binding.pwRegText.clearFocus()
            if(binding.emailText.text.isNotBlank() && binding.pwRegText.text.isNotBlank()){
                if(!validateEmail(binding.emailText))
                    Toast.makeText(requireContext(), "Please provide a valid email address",
                        Toast.LENGTH_LONG).show()
                else{
                    val email = binding.emailText.text.toString()
                    val password = binding.pwRegText.text.toString()
                    progressDialog.show()
                    viewModel.onSignIn(email, password)
                }
            }else{
                Toast.makeText(requireContext(),"Please fill in both field.", Toast.LENGTH_LONG).show()
            }
        }

        binding.signUpBtn.setOnClickListener{
            binding.emailText.clearFocus()
            binding.pwRegText.clearFocus()
            findNavController().safeNavigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment4())
        }
    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction)
        }
    }

    private fun validateEmail(view: TextView): Boolean {
        return view.text.matches(emailPattern.toRegex())
    }



}