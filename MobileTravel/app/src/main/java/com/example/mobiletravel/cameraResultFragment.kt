package com.example.mobiletravel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mobiletravel.databinding.FragmentCameraResultBinding
import com.example.mobiletravel.databinding.FragmentSearchResultBinding
import com.example.mobiletravel.viewmodel.PlaceViewModel
import com.example.mobiletravel.viewmodel.ProfileViewModel


class cameraResultFragment : Fragment() {

    private var _binding: FragmentCameraResultBinding? = null
    private val binding get() = _binding!!
    private val args: cameraResultFragmentArgs by navArgs()
    private val placeViewModel: PlaceViewModel by lazy{
        ViewModelProvider(requireActivity()).get(PlaceViewModel::class.java)
    }
    private val userViewModel: ProfileViewModel by lazy{
        ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraResultBinding.inflate(inflater, container, false)

        userViewModel.user.observe(viewLifecycleOwner,{
            if(it != null){
                placeViewModel.getImageResult(args.recognitionResult,it.email)
            }else{
                placeViewModel.getImageResult(args.recognitionResult,"")
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeViewModel.selectedPlace.observe(viewLifecycleOwner,{
            it?.let{
                binding.apply {
                    Glide
                        .with(resultImg)
                        .load("http://192.168.0.104/travel/src/img/attractions/${it.imageLink}")
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .fallback(R.drawable.ic_launcher_foreground)
                        .into(resultImg)

                    resultTitle.text = it.name
                    resultDetailBtn.setOnClickListener {
                        findNavController().navigate(R.id.action_cameraResultFragment_to_placeDetailFragment)
                    }
                }

            }
        })

    }


}