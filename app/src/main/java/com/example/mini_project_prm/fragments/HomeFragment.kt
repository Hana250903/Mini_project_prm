package com.example.mini_project_prm.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.adapters.FigureAdapter
import com.example.mini_project_prm.api.RetrofitClient
import com.example.mini_project_prm.models.Figure
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var figureAdapter: FigureAdapter
    private lateinit var searchBox: EditText
    private lateinit var sortSpinner: Spinner

    private var originalFigureList: List<Figure> = emptyList()
    private var currentSortPosition = 0

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

        // Khởi tạo Adapter với một danh sách rỗng trước
        figureAdapter = FigureAdapter(emptyList())
        recyclerView.adapter = figureAdapter

        // Thiết lập các listener
        setupSortSpinner()
        setupSearchListener()

        // Gọi API để lấy dữ liệu và cập nhật Adapter sau
        fetchFiguresFromApi()
    }

    private fun setupSortSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_options,
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
        // Hiển thị một cái gì đó cho người dùng biết đang tải (tùy chọn)
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getFigures()
                // XỬ LÝ KẾT QUẢ API AN TOÀN HƠN
                if (response.isSuccessful) {
                    response.body()?.let { figuresFromApi ->
                        originalFigureList = figuresFromApi
                        // Cập nhật lại giao diện với dữ liệu đầy đủ
                        filterAndSort(searchBox.text.toString())
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi tải dữ liệu: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Lỗi kết nối: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}