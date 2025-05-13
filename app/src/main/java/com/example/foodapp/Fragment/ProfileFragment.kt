package com.example.foodapp.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import org.json.JSONObject
import android.util.Log


class ProfileFragment : Fragment() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var btnSave: Button
    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        edtName = view.findViewById(R.id.editTextName)
        edtEmail = view.findViewById(R.id.editTextEmail)
        btnSave = view.findViewById(R.id.buttonSave)

        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", -1) // Lấy userId từ SharedPreferences

        Log.d("ProfileFragment", "Loaded userId from SharedPreferences: $userId")

        if (userId == -1) {
            Log.e("ProfileFragment", "UserId is invalid or missing!")
        }

        val name = sharedPreferences.getString("name", "") ?: ""
        val email = sharedPreferences.getString("email", "") ?: ""

        edtName.setText(name)
        edtEmail.setText(email)

        btnSave.setOnClickListener {
            updateUser()
        }

        return view
    }

    private fun updateUser() {
        val name = edtName.text.toString().trim()
        val email = edtEmail.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Tên và email không được để trống", Toast.LENGTH_SHORT).show()
            return
        }
        if (userId == -1) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.1.8/get_food/update_user.php"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val jsonObject = JSONObject()
        jsonObject.put("id", userId)
        jsonObject.put("name", name)
        jsonObject.put("email", email)

        Log.d("UpdateUser", "Sending to server: id=$userId, name=$name, email=$email")

        val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            { response ->
                Log.d("UpdateUser", "Response: $response")
                val status = response.getString("status")
                if (status == "success") {
                    val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    if (response.has("id")) {
                        editor.putInt("userId", response.getInt("id"))
                    }
                    if (response.has("name")) {
                        editor.putString("name", response.getString("name"))
                    }
                    if (response.has("email")) {
                        editor.putString("email", response.getString("email"))
                    }
                    editor.apply()

                    Toast.makeText(requireContext(), "Thông tin người dùng đã được cập nhật", Toast.LENGTH_SHORT).show()

                    userId = sharedPreferences.getInt("userId", -1)
                    Log.d("ProfileFragment", "Updated userId: $userId")
                } else {
                    Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("UpdateUser", "Error: ${error.message}")
                Toast.makeText(requireContext(), "Lỗi cập nhật người dùng: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(request)
    }

}
