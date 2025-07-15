package com.example.mini_project_prm.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mini_project_prm.R
import com.example.mini_project_prm.api.RetrofitClient
import com.example.mini_project_prm.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class UserInfoFragment : Fragment() {

    private lateinit var edtFullName: EditText
    private lateinit var radioGender: RadioGroup
    private lateinit var edtAddress: EditText
    private lateinit var spinnerCountry: Spinner
    private lateinit var spinnerCity: Spinner
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtDob: EditText
    private lateinit var btnUpdate: Button

    private var userId: Int = -1
    private val TAG = "UserInfoFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ánh xạ view
        edtFullName = view.findViewById(R.id.edtFullName)
        radioGender = view.findViewById(R.id.radioGender)
        edtAddress = view.findViewById(R.id.edtAddress)
        spinnerCountry = view.findViewById(R.id.spinnerCountry)
        spinnerCity = view.findViewById(R.id.spinnerCity)
        edtEmail = view.findViewById(R.id.edtEmail)
        edtPhone = view.findViewById(R.id.edtPhone)
        edtDob = view.findViewById(R.id.edtDob)
        btnUpdate = view.findViewById(R.id.btnUpdate)

        edtDob.setOnClickListener {
            showDatePickerDialog()
        }

        // Lấy dữ liệu truyền sang
        userId = arguments?.getInt("userId", -1) ?: -1
        val fullName = arguments?.getString("fullName") ?: ""
        val email = arguments?.getString("email") ?: ""

        edtFullName.setText(fullName)
        edtEmail.setText(email)

        val countries = listOf("Vietnam", "USA", "Japan")
        val cities = listOf("Hồ Chí Minh", "Hà Nội", "Đà Nẵng")

        spinnerCountry.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, countries)
        spinnerCity.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities)

        // Nút cập nhật
        btnUpdate.setOnClickListener {
            updateUser()
        }


    }

    private fun updateUser() {
        if (userId == -1) {
            Toast.makeText(requireContext(), "Không tìm thấy User ID để cập nhật", Toast.LENGTH_SHORT).show()
            return
        }

        val fullName = edtFullName.text.toString()
        val gender = when (radioGender.checkedRadioButtonId) {
            R.id.radioMale -> "Nam"
            R.id.radioFemale -> "Nữ"
            else -> null
        }
        val address = edtAddress.text.toString()
        val country = spinnerCountry.selectedItem.toString()
        val city = spinnerCity.selectedItem.toString()
        val email = edtEmail.text.toString()
        val phone = edtPhone.text.toString()
        val dob = edtDob.text.toString()

        val updatedUser = User(
            id = userId,
            fullName = fullName,
            email = email,
            gender = gender,
            address = address,
            countries = country,
            city = city,
            dob = dob,
            phone = phone,
            passwordHash = null,
            role = "customer",
            createdAt = null // nếu server không yêu cầu, có thể để trống
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateUser("eq.$userId", updatedUser)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        val error = response.errorBody()?.string()
                        Log.e(TAG, "Lỗi cập nhật: $error")
                        Toast.makeText(requireContext(), "Lỗi cập nhật: $error", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Lỗi kết nối API", e)
                    Toast.makeText(requireContext(), "Lỗi kết nối: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                edtDob.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    companion object {
        fun newInstance(userId: Int, fullName: String, email: String): UserInfoFragment {
            val fragment = UserInfoFragment()
            val args = Bundle().apply {
                putInt("userId", userId)
                putString("fullName", fullName)
                putString("email", email)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
