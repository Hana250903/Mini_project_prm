package com.example.mini_project_prm.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.adapters.FigureAdapter
import com.example.mini_project_prm.api.RetrofitClient
import com.example.mini_project_prm.models.Figure
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    // HÃY CHẮC CHẮN BẠN CÓ ĐẦY ĐỦ 6 DÒNG KHAI BÁO NÀY.
    // BẠN ĐANG THIẾU DÒNG KHAI BÁO "sortSpinner".

    private lateinit var recyclerView: RecyclerView
    private lateinit var figureAdapter: FigureAdapter
    private lateinit var searchBox: EditText
    private lateinit var sortSpinner: Spinner  // <-- DÒNG NÀY SẼ SỬA LỖI CỦA BẠN
    private var originalFigureList: List<Figure> = emptyList()
    private var currentSortPosition = 0

    // ... các hàm onCreateView, onViewCreated... của bạn bắt đầu từ đây

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ánh xạ View
        val imgBackground: ImageView = view.findViewById(R.id.imgBackground)
        recyclerView = view.findViewById(R.id.recyclerViewFigures)
        sortSpinner = view.findViewById(R.id.sortSpinner)
        searchBox = requireActivity().findViewById(R.id.searchBox)

        // Thiết lập RecyclerView
        imgBackground.setImageResource(R.drawable.background_first)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.setHasFixedSize(true)

        // Khởi tạo dữ liệu và Adapter
        fetchFiguresFromApi()
        figureAdapter = FigureAdapter(originalFigureList)
        recyclerView.adapter = figureAdapter

        // Gọi hàm thiết lập listener
        setupSortSpinner()
        setupSearchListener()
    }

    private fun setupSortSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_options, // Dòng này sẽ hết lỗi sau khi bạn tạo arrays.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sortSpinner.adapter = adapter
        }

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentSortPosition = position
                filterAndSort(searchBox.text.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSearchListener() {
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterAndSort(s.toString())
            }
        })
    }

    private fun filterAndSort(text: String) {
        val filteredList = if (text.isBlank()) {
            originalFigureList
        } else {
            originalFigureList.filter {
                it.name.lowercase().contains(text.lowercase())
            }
        }

        val sortedList = when (currentSortPosition) {
            1 -> filteredList.sortedBy { it.price }
            2 -> filteredList.sortedByDescending { it.price }
            3 -> filteredList.sortedBy { it.name }
            4 -> filteredList.sortedByDescending { it.name }
            else -> filteredList
        }

        figureAdapter.updateData(sortedList)
    }

    private fun fetchFiguresFromApi() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getFigures()
                originalFigureList = response
                figureAdapter.updateData(originalFigureList)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Lỗi khi tải dữ liệu: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}