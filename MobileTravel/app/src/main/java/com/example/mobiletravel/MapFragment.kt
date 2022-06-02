package com.example.mobiletravel

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobiletravel.databinding.FragmentMapBinding
import com.example.mobiletravel.viewmodel.PlaceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.example.mobiletravel.baseclass.Place
import com.example.mobiletravel.viewmodel.ProfileViewModel
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.model.Marker




class MapFragment : Fragment(), OnMapReadyCallback, NearbyPlaceMapAdapter.CellClickListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val placeKey = "AIzaSyBEzMWa9wXiMlYmrql-JLxJnRRwH3aQqm8"
    private lateinit var placesClient: PlacesClient
    private lateinit var placeList: List<Place>
    private val adapter = NearbyPlaceMapAdapter(this)
    private val placeViewModel: PlaceViewModel by lazy{
        ViewModelProvider(requireActivity()).get(PlaceViewModel::class.java)
    }
    private val userViewModel: ProfileViewModel by lazy{
        ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    }
    private lateinit var progressDialog: ProgressDialog
    private var gpsStatus:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient =  LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val mapview = binding.googleMap

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Fetching places......")
        progressDialog.setCancelable(false)
        locationEnabled()

        Places.initialize(requireActivity().application, placeKey)
        placesClient = Places.createClient(requireContext())
        if(!gpsStatus){
            mapview.onCreate(savedInstanceState)
            mapview.onResume()
            mapview.getMapAsync(this)
            Toast.makeText(requireContext(), "Please enable location service", Toast.LENGTH_LONG).show()
        }
        else{
            mapview.onCreate(savedInstanceState)
            mapview.onResume()
            mapview.getMapAsync(this)
        }
        binding.mapRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "Please enable location permission", Toast.LENGTH_LONG).show()
            return ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
        }else if(!gpsStatus){
            Toast.makeText(requireContext(), "Please enable location service", Toast.LENGTH_LONG).show()
        }else{
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = false
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.map_style))
            setCurrentLocation()
            getAllPlacesNearby()
            placeViewModel.nearbyPlaces.observe(viewLifecycleOwner, Observer{
                it?.let{
                    placeList = it
                    adapter.nearbyResultList = it
                    if(progressDialog.isShowing)
                        progressDialog.dismiss()
                    updateMap()
                }
            })

            binding.searAreaBtn.setOnClickListener {
                val camCen = googleMap.cameraPosition.target

                userViewModel.user.observe(viewLifecycleOwner,{
                    if(it != null)
                        placeViewModel.getAllNearbyPlaces(camCen.latitude, camCen.longitude,it.email)
                    else
                        placeViewModel.getAllNearbyPlaces(camCen.latitude, camCen.longitude,"")
                })
                binding.searAreaBtn.visibility = View.INVISIBLE
            }

            googleMap.setOnCameraMoveListener {
                binding.searAreaBtn.visibility = View.VISIBLE
            }


            googleMap.setOnMarkerClickListener { marker ->
                val index = marker.snippet!!.toInt()
                binding.mapRecyclerView.smoothScrollToPosition(index)
                false
            }

        }

    }

    @SuppressLint("MissingPermission")
    fun setCurrentLocation(){
        fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Set the map's camera position to the current location of the device.
                lastLocation = task.result
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(lastLocation.latitude,
                        lastLocation.longitude), 14f))
            } else {
                Log.i("Map init status", "failed")
                googleMap.uiSettings.isMyLocationButtonEnabled = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getAllPlacesNearby(){
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                //get nearby places based on current lat and long
                userViewModel.user.observe(viewLifecycleOwner,{
                    if(it != null)
                        placeViewModel.getAllNearbyPlaces(location.latitude, location.longitude,it.email)
                    else
                        placeViewModel.getAllNearbyPlaces(location.latitude, location.longitude,"")
                })
            }else{
                Toast.makeText(context, "location null", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun updateMap(){
        googleMap.clear()
        for(index in placeList.indices){
            googleMap.addMarker(
                MarkerOptions().position(LatLng(placeList[index].latitude,placeList[index].longitude)).snippet(index.toString())
                       // MarkerOptions().position(LatLng(placeList[index].latitude,placeList[index].longitude)).title(index.toString())
            )
        }
    }

    override fun onCellClickListener(data: Place) {
        placeViewModel.setSelectedPlace(data)
        findNavController().navigate(com.example.mobiletravel.R.id.action_mapFragment_to_placeDetailFragment)
    }

    private fun locationEnabled() {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


}