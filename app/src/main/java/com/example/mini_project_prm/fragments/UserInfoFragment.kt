package com.example.mini_project_prm.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import com.example.mini_project_prm.R
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

        edtFullName.setText("Hana Yuki")
        edtEmail.setText("kiritomiku0@gmail.com")

        val countries = listOf("Vietnam", "USA", "Japan")
        val cities = listOf("Hồ Chí Minh", "Hà Nội", "Đà Nẵng")

        spinnerCountry.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, countries)
        spinnerCity.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities)

        // Nút cập nhật
        btnUpdate.setOnClickListener {
            val fullName = edtFullName.text.toString()
            val gender = when (radioGender.checkedRadioButtonId) {
                R.id.radioMale -> "Nam"
                R.id.radioFemale -> "Nữ"
                else -> ""
            }
            val address = edtAddress.text.toString()
            val country = spinnerCountry.selectedItem.toString()
            val city = spinnerCity.selectedItem.toString()
            val email = edtEmail.text.toString()
            val phone = edtPhone.text.toString()
            val dob = edtDob.text.toString()

            Toast.makeText(
                requireContext(),
                "Cập nhật thành công:\n$fullName, $gender, $address, $country, $city, $email, $phone, $dob",
                Toast.LENGTH_LONG
            ).show()
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
}
