package com.example.foodapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodapp.MenuBootomSheetFragment
import com.example.foodapp.R
import com.example.foodapp.adapter.PopularAdapter
import com.example.foodapp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewAllMenu.setOnClickListener{
            val bootomSheetDialog = MenuBootomSheetFragment()
            bootomSheetDialog.show(parentFragmentManager, "Test")
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.CENTER_INSIDE))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.CENTER_INSIDE))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.CENTER_INSIDE))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        imageSlider.setItemClickListener(object :ItemClickListener{
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int){
                val itemPosition = imageList[position]
                val itemMessage = "Chọn Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
        val foodName = listOf("Burger", "Sandwich", "momo", "item")
        val Price = listOf("4.60", "5.70", "5.60", "5.50")
        val popularImages =
            listOf(R.drawable.menu1, R.drawable.menu2, R.drawable.menu3, R.drawable.menu4)
        val adapter = PopularAdapter(foodName, Price, popularImages, requireContext())
        binding.PopularRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopularRecycleView.adapter = adapter



    }

    companion object {

    }
}