package com.example.mobiletravel

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobiletravel.baseclass.Place
import com.example.mobiletravel.databinding.FragmentBookmarkBinding
import com.example.mobiletravel.databinding.FragmentExploreBinding
import com.example.mobiletravel.viewmodel.PlaceViewModel
import com.example.mobiletravel.viewmodel.ProfileViewModel

class BookmarkFragment : Fragment(), BookmarkAdapter.CellClickListener {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentBookmarkBinding.inflate(inflater, container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BookmarkAdapter(this)
        binding.bookRecyclerView.adapter = adapter

        userViewModel.user.observe(viewLifecycleOwner,{
            if(it == null){
                binding.bookRecyclerView.visibility = View.GONE
                binding.showText.text = "Please sign in to see saved places"
                binding.navBtn.setOnClickListener {
                    findNavController().navigate(R.id.action_bookmarkFragment_to_signInFragment)
                }
            }else {
                it?.let {
                    placeViewModel.getSaved(it.email)
                }
            }
        })

        placeViewModel.bookmarkPlaces.observe(viewLifecycleOwner,{
            if(it.isNullOrEmpty()){
                if(userViewModel.user.value == null){
                    binding.bookRecyclerView.visibility = View.GONE
                    binding.showText.text = "Please sign in to see saved places"
                    binding.navBtn.text = "Sign In"
                    binding.navBtn.setOnClickListener {
                        findNavController().navigate(R.id.action_bookmarkFragment_to_signInFragment)
                    }
                }else{
                    binding.bookRecyclerView.visibility = View.GONE
                    binding.showText.text = "Explore now to get trip inspiration."
                    binding.navBtn.text = "Explore"
                    binding.navBtn.setOnClickListener {
                        findNavController().navigate(R.id.action_bookmarkFragment_to_exploreFragment)
                    }
                }
            }else{
                it.let{
                    binding.noticeCard.visibility = View.GONE
                    binding.bookRecyclerView.visibility = View.VISIBLE
                    adapter.bookmarkList = it
                }
            }
        })


    }

    override fun onCellClickListener(data: Place) {
        placeViewModel.setSelectedPlace(data)
        findNavController().navigate(R.id.action_bookmarkFragment_to_placeDetailFragment)
    }

}