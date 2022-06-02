package com.example.mobiletravel

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.mobiletravel.databinding.FragmentProfileBinding
import com.example.mobiletravel.viewmodel.PlaceViewModel
import com.example.mobiletravel.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(){

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: ProfileViewModel by lazy{
        ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    }
    private val placeViewModel: PlaceViewModel by lazy{
        ViewModelProvider(requireActivity()).get(PlaceViewModel::class.java)
    }
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if(userViewModel.user.value == null) {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToSignInFragment())
        }
        _binding = FragmentProfileBinding.inflate(inflater, container,false)

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Signing out...")
        progressDialog.setCancelable(false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.user.observe(viewLifecycleOwner, Observer {
            it?.let{
                binding.profileName.text = it.name
                binding.profileMail.text = it.email
            }
        })


        binding.signOutBtn.setOnClickListener{
            progressDialog.show()
            lifecycleScope.launch {
                userViewModel.onSignOut()
                if(userViewModel.user.value == null) {
                    if(progressDialog.isShowing)
                        progressDialog.dismiss()
                    placeViewModel.clearOnSignOut()
                    findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToSignInFragment())
                }
            }
        }

        binding.updateProfileBtn.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
        }

        binding.changePwBtn.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToChangePasswordFragment())
        }
    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction)
        }
    }


}