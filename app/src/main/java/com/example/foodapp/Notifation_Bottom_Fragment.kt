package com.example.foodapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.adapter.NotificationAdapter
import com.example.foodapp.databinding.FragmentNotifationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Notifation_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotifationBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotifationBottomBinding.inflate(layoutInflater, container, false)
        val notifications = listOf("Bạn đã đặt hàng thành công", "Đơn hàng đã được tài xế tiếp nhận", "Đơn hàng đã được giao đến")
        val notificationImages = listOf(R.drawable.smile, R.drawable.truck_solid, R.drawable.success)
        val adapter = NotificationAdapter(
            ArrayList(notifications),
            ArrayList(notificationImages)
        )
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter
        return binding.root
    }

    companion object {

    }
}