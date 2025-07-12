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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.adapters.FigureAdapter
import com.example.mini_project_prm.models.Figure

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
        originalFigureList = createDummyData()
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

    private fun createDummyData(): List<Figure> {
        return listOf(
            Figure(id = 1, name = "Tokisaki Kurumi Nightwear ver. Desktop Cute - Date A Live V | Taito Figure.", description = "Mô hình Tokisaki Kurumi...", price = 490000.0, brand = "Taito", releaseDate = "2024-10-01", category = "Desktop Cute", series = "Date A Live V", stock = 15, imageUrl = R.drawable.kurumi),
            Figure(id = 2, name = "Sakurajima Mai Bunny Girl Ver. Artist MasterPiece AMP+ - Bunny Girl Senpai | TAITO Figure", description = "Phiên bản đặc biệt Artist MasterPiece+...", price = 450000.0, brand = "Taito", releaseDate = "2024-08-15", category = "AMP+", series = "Bunny Girl Senpai", stock = 10, imageUrl = R.drawable.mai),
            Figure(id = 3, name = "Nendoroid 1979 Hoshimachi Suisei - Hololive | Good Smile Company Figure", description = "Nendoroid đáng yêu của VTuber Hoshimachi Suisei...", price = 1350000.0, brand = "Good Smile Company", releaseDate = "2025-01-20", category = "Nendoroid", series = "Hololive", stock = 5, imageUrl = R.drawable.suisei),
            Figure(id = 4, name = "Nakano Miku Bloo-me - Gotoubun no Hanayome | FuRyu Figure", description = "Mô hình Nakano Miku trong dòng sản phẩm Bloo-me...", price = 490000.0, brand = "FuRyu", releaseDate = "2024-11-30", category = "Scale Figure", series = "Gotoubun no Hanayome", stock = 12, imageUrl = R.drawable.nakano_miku),
            Figure(id = 5, name = "Ainz Ooal Gown - Overlord | Bandai Spirits Figure", description = "Mô hình mạnh mẽ và uy nghi của Ainz Ooal Gown...", price = 690000.0, brand = "Bandai Spirits", releaseDate = "2024-12-10", category = "Scale Figure", series = "Overlord", stock = 8, imageUrl = R.drawable.ainz_ooal_gown),
            Figure(id = 6, name = "Nendoroid 1688 Gawr Gura - hololive production | Good Smile Company Figure", description = "Nendoroid 'cá mập' Gawr Gura siêu cấp đáng yêu...", price = 2450000.0, brand = "Good Smile Company", releaseDate = "2024-09-01", category = "Nendoroid", series = "Hololive", stock = 3, imageUrl = R.drawable.gawr_gura)
        )
    }
}