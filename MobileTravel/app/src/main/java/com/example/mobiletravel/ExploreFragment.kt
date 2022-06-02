package com.example.mobiletravel

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobiletravel.databinding.FragmentExploreBinding
import com.example.mobiletravel.baseclass.Place
import com.example.mobiletravel.viewmodel.PlaceViewModel
import com.example.mobiletravel.viewmodel.ProfileViewModel
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ExploreFragment : Fragment(), ExploreAdapter.CellClickListener {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private val placeViewModel: PlaceViewModel by lazy{
        ViewModelProvider(requireActivity()).get(PlaceViewModel::class.java)
    }
    private val userViewModel: ProfileViewModel by lazy{
        ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    }

    private lateinit var getImage: ActivityResultLauncher<String>
    private lateinit var captureImage: ActivityResultLauncher<Void>
    private lateinit var requestPermission: ActivityResultLauncher<String>
    private lateinit var requestPermission1: ActivityResultLauncher<String>
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        userViewModel.user.observe(viewLifecycleOwner,{
            if(it != null){
                placeViewModel.getHomeAttraction(it.email)
            }else{
                placeViewModel.getHomeAttraction("")
            }
        })

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Analyzing image...")
        progressDialog.setCancelable(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val fileName = "labelsV2.txt"
        //val inputString = requireActivity().assets.open(fileName).bufferedReader().use{it.readText()}
        //val predictionList = inputString.split("\n")

        getImage =
            registerForActivityResult(ActivityResultContracts.GetContent()) {


                if(it != null){
                    progressDialog.show()
                    val bitmap = BitmapFactory.decodeStream(requireActivity().contentResolver.openInputStream(it))

                    Handler(Looper.getMainLooper()).postDelayed({

                        val attractionName = runDetection(bitmap)

                        if(attractionName != null){
                            if(userViewModel.user.value != null){
                                Log.i("capture img", attractionName)
                                setObserver()
                                placeViewModel.getImageResult(attractionName,userViewModel.user.value!!.email)
                            }
                            else{
                                Log.i("capture img", attractionName)
                                setObserver()
                                placeViewModel.getImageResult(attractionName,"")
                            }
                        }else{
                            if(progressDialog.isShowing)
                                progressDialog.dismiss()
                            Toast.makeText(requireContext(),"No attraction is detected", Toast.LENGTH_LONG).show()
                        }

                    }, 50)
                }else{
                    Toast.makeText(requireContext(),"No image is selected",Toast.LENGTH_LONG).show()
                }


            }

        captureImage =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                if(it != null){
                    progressDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({

                        val attractionName = runDetection(it)

                        if(attractionName != null){
                            if(userViewModel.user.value != null){
                                Log.i("capture img", attractionName)
                                setObserver()
                                placeViewModel.getImageResult(attractionName,userViewModel.user.value!!.email)
                            }
                            else{
                                Log.i("capture img", attractionName)
                                setObserver()
                                placeViewModel.getImageResult(attractionName,"")
                            }
                        }else{
                            if(progressDialog.isShowing)
                                progressDialog.dismiss()
                            Toast.makeText(requireContext(),"No attraction is detected", Toast.LENGTH_LONG).show()
                        }

                    }, 50)
                }else{
                    Toast.makeText(requireContext(),"No image is captured",Toast.LENGTH_LONG).show()
                }

            }

        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){granted->
            if(granted){
                captureImage.launch(null)
            }else{
                Toast.makeText(requireContext(), "Permission denied, please try again", Toast.LENGTH_SHORT).show()
            }
        }

        requestPermission1 = registerForActivityResult(ActivityResultContracts.RequestPermission()){granted->
            if(granted){
                getImage.launch("image/*")
            }else{
                Toast.makeText(requireContext(), "Permission denied, please try again", Toast.LENGTH_SHORT).show()
            }
        }

        //adapter
        val adapter = ExploreAdapter(this)
        binding.exploreRecyclerView.adapter = adapter
        placeViewModel.homeAttraction.observe(viewLifecycleOwner, Observer{
            it?.let{
                adapter.exploreList = it
            }
        })

        //search
        binding.searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBar.clearFocus()
                return if(query != null){
                    val searchQuery = ExploreFragmentDirections.actionExploreFragmentToSearchResultFragment(query)
                    findNavController().navigate(searchQuery)
                    true
                }else
                    false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        binding.attractionBtn.setOnClickListener{
            val searchQuery = ExploreFragmentDirections.actionExploreFragmentToNearbyPlaceFragment("Attraction")
            findNavController().navigate(searchQuery)
        }

        binding.placesBtn.setOnClickListener{
            val searchQuery = ExploreFragmentDirections.actionExploreFragmentToNearbyPlaceFragment("Accommodation")
            findNavController().navigate(searchQuery)
        }

        binding.fnbBtn.setOnClickListener{
            val searchQuery = ExploreFragmentDirections.actionExploreFragmentToNearbyPlaceFragment("Food")
            findNavController().navigate(searchQuery)
        }


        binding.aiBtn.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.cam_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.cameraBtn -> {
                        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED){
                            captureImage.launch(null)
                        }else{
                            requestPermission.launch(android.Manifest.permission.CAMERA)
                        }

                    }
                    R.id.uploadBtn -> {
                        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                            getImage.launch("image/*")
                        }else{
                            requestPermission1.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                }
                true
            }
            popupMenu.show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCellClickListener(data: Place) {
        binding.searchBar.clearFocus()
        placeViewModel.setSelectedPlace(data)
        findNavController().navigate(R.id.action_exploreFragment_to_placeDetailFragment)

    }


    private fun runDetection(image: Bitmap): String?{
        var result: String? = null
        val image = TensorImage.fromBitmap(image)

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(1)
            .setScoreThreshold(0.5f)
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            requireContext(), // the application context
            "objdetv303.tflite", // must be same as the filename in assets folder
            options
        )

        val results = detector.detect(image)
        for ((i, obj) in results.withIndex()) {
            for ((j, category) in obj.categories.withIndex()) {
                result = category.label
            }
        }

        return result
    }

    private fun setObserver(){
        placeViewModel.selectedPlace.observe(viewLifecycleOwner,{
            it?.let{
                if(progressDialog.isShowing)
                    progressDialog.dismiss()
                findNavController().navigate(R.id.action_exploreFragment_to_placeDetailFragment)
            }
        })
    }
}

/*//val attractionName = predictionList[getMax(bitmap)]
        val model = Objdetv303.newInstance(requireContext())

        // Creates inputs for reference.
        val image = TensorImage.fromBitmap(image)

        // Runs model inference and gets result.
        val outputs = model.process(image)
        val detectionResult = outputs.detectionResultList.get(0)

        // Gets result from DetectionResult.
        val attractionName = detectionResult.categoryAsString

        // Releases model resources if no longer used.
        model.close()*/


