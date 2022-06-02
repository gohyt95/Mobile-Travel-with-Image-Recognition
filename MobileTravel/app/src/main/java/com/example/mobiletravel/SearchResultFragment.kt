package com.example.mobiletravel

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mobiletravel.baseclass.Place
import com.example.mobiletravel.databinding.FragmentSearchResultBinding
import com.example.mobiletravel.viewmodel.PlaceViewModel
import com.example.mobiletravel.viewmodel.ProfileViewModel


class SearchResultFragment : Fragment(), SearchResultAdapter.CellClickListener  {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val args: SearchResultFragmentArgs by navArgs()
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
        (activity as AppCompatActivity).supportActionBar?.title = "Search result"
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        /*if(userViewModel.user.value != null)
            placeViewModel.getSearchResult(args.searchQuery,userViewModel.user.value!!.email)
        else
            placeViewModel.getSearchResult(args.searchQuery,"")*/

        userViewModel.user.observe(viewLifecycleOwner,{
            if(it != null){
                placeViewModel.getSearchResult(args.searchQuery,it.email)
            }else{
                placeViewModel.getSearchResult(args.searchQuery,"")
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SearchResultAdapter(this)
        binding.searchRecyclerView.adapter = adapter
        placeViewModel.searchResult.observe(viewLifecycleOwner, Observer{
            it?.let{
                adapter.backupList = it

                val halfTitle = if(adapter.itemCount.equals(1)) adapter.itemCount.toString()+ " result for " + args.searchQuery
                else adapter.itemCount.toString()+ " results for " + args.searchQuery

                binding.searchTitle.text = halfTitle
            }
        })

        binding.attractionFilterBtn.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {
                if(binding.accomodationFilterBtn.isChecked){
                    binding.accomodationFilterBtn.isChecked = false
                    adapter.searchResultList = adapter.backupList
                }
                if(binding.restaurantFilterBtn.isChecked){
                    binding.restaurantFilterBtn.isChecked = false
                    adapter.searchResultList = adapter.backupList
                }
                binding.attractionFilterBtn.backgroundTintList = getColorStateList(requireContext(),android.R.color.black)
                binding.attractionFilterBtn.setTextColor(Color.parseColor("#ffffff"))
                adapter.filter.filter("Attraction")
            } else {
                binding.attractionFilterBtn.backgroundTintList = getColorStateList(requireContext(),android.R.color.white)
                binding.attractionFilterBtn.setTextColor(Color.parseColor("#000000"))
                adapter.filter.filter("")
            }

        }

        binding.accomodationFilterBtn.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {
                if(binding.attractionFilterBtn.isChecked){
                    binding.attractionFilterBtn.isChecked = false
                    adapter.searchResultList = adapter.backupList
                }
                if(binding.restaurantFilterBtn.isChecked){
                    binding.restaurantFilterBtn.isChecked = false
                    adapter.searchResultList = adapter.backupList
                }
                binding.accomodationFilterBtn.backgroundTintList = getColorStateList(requireContext(),android.R.color.black)
                binding.accomodationFilterBtn.setTextColor(Color.parseColor("#ffffff"))
                adapter.filter.filter("Accommodation")
            } else {
                binding.accomodationFilterBtn.backgroundTintList = getColorStateList(requireContext(),android.R.color.white)
                binding.accomodationFilterBtn.setTextColor(Color.parseColor("#000000"))
                adapter.filter.filter("")
            }

        }

        binding.restaurantFilterBtn.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                if(binding.attractionFilterBtn.isChecked){
                    binding.attractionFilterBtn.isChecked = false
                    adapter.searchResultList = adapter.backupList
                }
                if(binding.accomodationFilterBtn.isChecked){
                    binding.accomodationFilterBtn.isChecked = false
                    adapter.searchResultList = adapter.backupList
                }
                binding.restaurantFilterBtn.backgroundTintList = getColorStateList(requireContext(),android.R.color.black)
                binding.restaurantFilterBtn.setTextColor(Color.parseColor("#ffffff"))
                adapter.filter.filter("Food")
            } else {
                binding.restaurantFilterBtn.backgroundTintList = getColorStateList(requireContext(),android.R.color.white)
                binding.restaurantFilterBtn.setTextColor(Color.parseColor("#000000"))
                adapter.filter.filter("")
            }

        }
    }

    override fun onCellClickListener(data: Place) {
        placeViewModel.setSelectedPlace(data)
        findNavController().navigate(R.id.action_searchResultFragment_to_placeDetailFragment)
    }




}