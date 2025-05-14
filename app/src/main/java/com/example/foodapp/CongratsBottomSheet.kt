package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.example.foodapp.Fragment.HomeFragment
import com.example.foodapp.databinding.FragmentCongratsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CongratsBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCongratsBottomSheetBinding

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        binding = FragmentCongratsBottomSheetBinding.inflate(inflater, container, false)

        binding.goHome.setOnClickListener {
            val intent = Intent(requireContext(), HomeFragment::class.java)
            startActivity(intent)
        }

        binding.message.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    companion object {
        fun newInstance(): CongratsBottomSheet {
            return CongratsBottomSheet()
        }
    }
}
