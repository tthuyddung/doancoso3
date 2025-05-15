package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.databinding.FragmentCongratsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CongratsBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCongratsBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCongratsBottomSheetBinding.inflate(inflater, container, false)

        // Sự kiện khi nhấn nút "Trở về"
        binding.goHome.setOnClickListener {
            clearCartAndReturnHome()
        }

        // Sự kiện khi nhấn "message" để chat
        binding.message.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun clearCartAndReturnHome() {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val idUser = sharedPref.getInt("userId", -1)

        if (idUser != -1) {
            val url = "http://192.168.1.18/get_food/clear_cart.php"
            val request = object : StringRequest(Method.POST, url,
                { response ->
                    Log.d("ClearCart", "Đã xóa giỏ hàng: $response")
                    Toast.makeText(requireContext(), "Đợi nhận đơn", Toast.LENGTH_SHORT).show()

                    // Chuyển về MainActivity (mặc định mở HomeFragment)
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    requireActivity().finish()
                },
                { error ->
                    Log.e("ClearCart", "Lỗi xóa giỏ hàng: ${error.message}")
                    Toast.makeText(requireContext(), "Lỗi khi xóa giỏ hàng", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf("id_user" to idUser.toString())
                }
            }

            Volley.newRequestQueue(requireContext()).add(request)
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(): CongratsBottomSheet {
            return CongratsBottomSheet()
        }
    }
}
