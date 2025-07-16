package com.example.mini_project_prm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.adapters.FigureAdapter
import com.example.mini_project_prm.api.RetrofitClient
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FigureAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // Thêm dòng này là một thói quen tốt

        recyclerView = view.findViewById(R.id.recyclerViewFigures)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Khởi tạo adapter với danh sách rỗng trước
        adapter = FigureAdapter(emptyList())
        recyclerView.adapter = adapter

        // Sau đó mới gọi API để lấy dữ liệu
        fetchFigures()
    }

    // === SỬA LẠI HÀM NÀY ===
    private fun fetchFigures() {
        lifecycleScope.launch {
            try {
                // 1. Nhận về đối tượng Response
                val response = RetrofitClient.instance.getFigures()

                // 2. Kiểm tra xem cuộc gọi có thành công không
                if (response.isSuccessful) {
                    // 3. Mở hộp và lấy danh sách figures (nếu không null)
                    response.body()?.let { figuresFromApi ->
                        // 4. Cập nhật dữ liệu cho adapter
                        adapter.updateData(figuresFromApi)
                    }
                } else {
                    // Xử lý trường hợp API trả về lỗi (vd: 404, 500)
                    Toast.makeText(requireContext(), "Lỗi tải dữ liệu: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Xử lý lỗi kết nối mạng
                e.printStackTrace()
                Toast.makeText(requireContext(), "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}