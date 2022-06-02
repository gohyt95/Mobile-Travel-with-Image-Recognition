package com.example.mobiletravel

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mobiletravel.databinding.FragmentPlaceDetailBinding
import com.example.mobiletravel.viewmodel.PlaceViewModel
import java.util.*
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.mobiletravel.baseclass.Place
import com.example.mobiletravel.baseclass.User
import com.example.mobiletravel.viewmodel.ProfileViewModel


class PlaceDetailFragment : Fragment() {

    private var _binding: FragmentPlaceDetailBinding? = null
    private val binding get() = _binding!!
    private val placeViewModel: PlaceViewModel by lazy{
        ViewModelProvider(requireActivity()).get(PlaceViewModel::class.java)
    }
    private val userViewModel: ProfileViewModel by lazy{
        ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    }
    private lateinit var data: Place

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaceDetailBinding.inflate(inflater, container,false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                placeViewModel.clearSelectedPlace()
                if (isEnabled) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data = placeViewModel.selectedPlace.value!!

        placeViewModel.selectedPlace.observe(viewLifecycleOwner,{
            it?.let{
                data = it
                if(it.bookmark == 1) {
                    binding.bookmarkIcon.setBackgroundResource(R.drawable.bookmark__1_)
                }else{
                    binding.bookmarkIcon.setBackgroundResource(R.drawable.bookmark)
                }
            }
        })

        userViewModel.user.observe(viewLifecycleOwner,{
            if(it == null){
                binding.bookmarkIcon.visibility =  View.GONE
                binding.ratingBtn.visibility = View.GONE
            }/*else{
                if(data.bookmark == 1) {
                    binding.bookmarkIcon.setBackgroundResource(R.drawable.bookmark__1_)
                }else{
                    binding.bookmarkIcon.setBackgroundResource(R.drawable.bookmark)
                }
            }*/
        })


        binding.placeTitle.text = data.name
        if(data.avgRating != null) binding.ratingTxt.text = data.avgRating!!.substring(0,3) + " (${data.ratingCount} rated)"
        else binding.ratingTxt.text ="0"
        binding.cityTxt.text = data.city
        if(!data.phone.isNullOrBlank()){
            binding.phoneTxt.visibility = View.VISIBLE
            binding.placePhone.visibility = View.VISIBLE
            binding.phoneTxt.text = data.phone
        }
        else{
            binding.phoneTxt.visibility = View.GONE
            binding.placePhone.visibility = View.GONE
        }
        binding.placeDesc.text = data.description
        if(data.category.equals(1))
            binding.catTxt.text = "Attraction"
        else if(data.category.equals(2))
            binding.catTxt.text = "Accomodation"
        else
            binding.catTxt.text = "Food"
        binding.opHourTxt.text = stringToTime(data.openHour!!) + "-" + stringToTime(data.closeHour!!)

        if(getOperationStatus(data.openHour!!, data.closeHour!!) == 1){
            when(data.category){
                2 -> {
                    binding.placeOpHour.visibility = View.GONE
                    binding.opHourTxt.visibility = View.GONE
                    binding.placeStatus.visibility = View.GONE
                    binding.statusTxt.visibility = View.GONE
                }
                else ->{
                    binding.placeOpHour.visibility = View.VISIBLE
                    binding.opHourTxt.visibility = View.VISIBLE
                    binding.placeStatus.visibility = View.VISIBLE
                    binding.statusTxt.visibility = View.VISIBLE
                    binding.statusTxt.text = "Open now"
                    binding.statusTxt.setTextColor(Color.parseColor("#009900"))
                }
            }
        }else{
            when(data.category){
                2 -> {
                    binding.placeOpHour.visibility = View.GONE
                    binding.opHourTxt.visibility = View.GONE
                    binding.placeStatus.visibility = View.GONE
                    binding.statusTxt.visibility = View.GONE
                }
                else ->{
                    binding.placeOpHour.visibility = View.VISIBLE
                    binding.opHourTxt.visibility = View.VISIBLE
                    binding.placeStatus.visibility = View.VISIBLE
                    binding.statusTxt.visibility = View.VISIBLE
                    binding.statusTxt.text = "Closed"
                    binding.statusTxt.setTextColor(Color.parseColor("#990000"))
                }
            }
        }

        Glide
            .with(binding.placeImg)
            .load("https://travelimgreg.000webhostapp.com/src/img/attractions/${data.imageLink}")
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .fallback(R.drawable.ic_launcher_foreground)
            .into(binding.placeImg)

        binding.placeDirBtn.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${data.latitude},${data.longitude}")
            //val gmmIntentUri = Uri.parse("geo:0,0?q=hospitals+%26+police+stations")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(requireContext().packageManager)?.let {
                startActivity(mapIntent)
            }
        }

        binding.ratingBtn.setOnClickListener {
            showDialog()
        }

        binding.bookmarkIcon.setOnClickListener{
            if(data.bookmark == 1){
                placeViewModel.removeBookmark(data.pid, userViewModel.user.value!!.email)
                binding.bookmarkIcon.setBackgroundResource(R.drawable.bookmark)
            }else{
                placeViewModel.addBookmark(data.pid, userViewModel.user.value!!.email)
                binding.bookmarkIcon.setBackgroundResource(R.drawable.bookmark__1_)
            }
        }

    }

    private fun stringToTime(time:String):String{

        var output = ""

        when(time.substring(0,2)){
            "01" -> output = "1" + time.substring(2,5) + "am"
            "02" -> output = "2" + time.substring(2,5) + "am"
            "03" -> output = "3" + time.substring(2,5) + "am"
            "04" -> output = "4" + time.substring(2,5) + "am"
            "05" -> output = "5" + time.substring(2,5) + "am"
            "06" -> output = "6" + time.substring(2,5) + "am"
            "07" -> output = "7" + time.substring(2,5) + "am"
            "08" -> output = "8" + time.substring(2,5) + "am"
            "09" -> output = "9" + time.substring(2,5) + "am"
            "10" -> output = "10" + time.substring(2,5) + "am"
            "11" -> output = "11" + time.substring(2,5) + "am"
            "12" -> output = "12" + time.substring(2,5) + "pm"
            "13" -> output = "1" + time.substring(2,5) + "pm"
            "14" -> output = "2" + time.substring(2,5) + "pm"
            "15" -> output = "3" + time.substring(2,5) + "pm"
            "16" -> output = "4" + time.substring(2,5) + "pm"
            "17" -> output = "5" + time.substring(2,5) + "pm"
            "18" -> output = "6" + time.substring(2,5) + "pm"
            "19" -> output = "7" + time.substring(2,5) + "pm"
            "20" -> output = "8" + time.substring(2,5) + "pm"
            "21" -> output = "9" + time.substring(2,5) + "pm"
            "22" -> output = "10" + time.substring(2,5) + "pm"
            "23" -> output = "11" + time.substring(2,5) + "pm"
            "24" -> output = "12" + time.substring(2,5) + "am"
        }

        return output

    }

    private fun getOperationStatus(open:String, close:String): Int{
        var status = 0
        val currentTime: Date = Calendar.getInstance().time
        val tempOpen: Int = open.substring(0,2).toInt()
        val tempClose: Int = close.substring(0,2).toInt()
        if(currentTime.hours >= tempOpen && currentTime.hours <= tempClose)
            status = 1
        else
            status = 0

        return status
    }

    private fun showDialog(){
        val dialogBuilder =  AlertDialog.Builder(context)
        val dialogView = this.layoutInflater.inflate(R.layout.rating_dialog, null)
        dialogBuilder.setView(dialogView)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.dialog_ratingbar)
        val rateBtn = dialogView.findViewById<Button>(R.id.rate_button)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancelRate_button)
        val ratingDialog = dialogBuilder.create()

        userViewModel.user.observe(viewLifecycleOwner,{
            if(it != null){
                if(data.rating != null){
                    ratingBar.rating = data.rating!!.toFloat()
                    rateBtn.setOnClickListener {
                        val rating = ratingBar.rating.toInt()
                        placeViewModel.updateRating(data.pid,userViewModel.user.value!!.email, rating)
                        ratingDialog.dismiss()
                    }
                }else{
                    rateBtn.setOnClickListener {
                        val rating = ratingBar.rating.toInt()
                        placeViewModel.submitRating(data.pid,userViewModel.user.value!!.email, rating)
                        ratingDialog.dismiss()
                    }
                }
            }
        })

        cancelBtn.setOnClickListener {
            ratingDialog.cancel()
        }

        ratingDialog.show()
    }

}