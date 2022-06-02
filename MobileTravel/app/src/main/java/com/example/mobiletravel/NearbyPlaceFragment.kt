package com.example.mobiletravel

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mobiletravel.baseclass.Place
import com.example.mobiletravel.databinding.FragmentNearbyPlaceBinding
import com.example.mobiletravel.viewmodel.PlaceViewModel
import com.example.mobiletravel.viewmodel.ProfileViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class NearbyPlaceFragment : Fragment(), NearbyAdapter.CellClickListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentNearbyPlaceBinding? = null
    private val binding get() = _binding!!
    private val args: NearbyPlaceFragmentArgs by navArgs()
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
        _binding = FragmentNearbyPlaceBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.nearbyHeading.append(args.placeType)

        fusedLocationClient =  LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
        //get current location latitude and longitude
        getCurrentLocation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NearbyAdapter(this)
        binding.nearbyRecyclerView.adapter = adapter
        placeViewModel.nearbyPlaces.observe(viewLifecycleOwner, Observer{
            it?.let{
                adapter.nearbyResultList = it

            }
        })

    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission( requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    //get nearby places based on current lat and long

                    userViewModel.user.observe(viewLifecycleOwner,{
                        if(it != null){
                            when(args.placeType) {
                                "Attraction" -> placeViewModel.getNearbyPlace(
                                    location.latitude,
                                    location.longitude,
                                    1,
                                    it.email
                                )
                                "Accommodation" -> placeViewModel.getNearbyPlace(
                                    location.latitude,
                                    location.longitude,
                                    2,
                                    it.email
                                )
                                "Food" -> placeViewModel.getNearbyPlace(
                                    location.latitude,
                                    location.longitude,
                                    3,
                                    it.email
                                )
                            }
                        }
                        else{
                            when(args.placeType) {
                                "Attraction" -> placeViewModel.getNearbyPlace(
                                    location.latitude,
                                    location.longitude,
                                    1,
                                    ""
                                )
                                "Accommodation" -> placeViewModel.getNearbyPlace(
                                    location.latitude,
                                    location.longitude,
                                    2,
                                    ""
                                )
                                "Food" -> placeViewModel.getNearbyPlace(
                                    location.latitude,
                                    location.longitude,
                                    3,
                                    ""
                                )
                            }
                        }
                    })

                    Log.i("latlong","lat :" + location.latitude+ ",long:"+ location.longitude )
                }else{
                    Toast.makeText(context, "location null", Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    override fun onCellClickListener(data: Place) {
        placeViewModel.setSelectedPlace(data)
        findNavController().navigate(com.example.mobiletravel.R.id.action_nearbyPlaceFragment_to_placeDetailFragment)
    }

}