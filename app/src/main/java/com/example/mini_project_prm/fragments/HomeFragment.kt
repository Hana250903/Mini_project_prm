package com.example.mini_project_prm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.adapters.FigureAdapter
import com.example.mini_project_prm.models.Figure

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dòng này không cần thiết nữa nếu bạn đã set ảnh trong XML của CoordinatorLayout
        // Nhưng nếu bạn muốn thay đổi ảnh động thì có thể giữ lại
        val imgBackground = view.findViewById<ImageView>(R.id.imgBackground)
        imgBackground.setImageResource(R.drawable.background_first)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewFigures)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // === PHẦN THAY ĐỔI CHÍNH ===
        // Tạo danh sách figures với đầy đủ các thuộc tính như trong model
        // Điều này rất quan trọng để có thể truyền dữ liệu sang ProductDetailActivity
        val figures = listOf(
            Figure(
                id = 1,
                name = "Tokisaki Kurumi Nightwear ver. Desktop Cute - Date A Live V | Taito Figure.",
                description = "Mô hình Tokisaki Kurumi trong trang phục nightwear đáng yêu từ series Date A Live V, sản xuất bởi Taito.",
                price = 490000.0,
                brand = "Taito",
                releaseDate = "2024-10-01",
                category = "Desktop Cute",
                series = "Date A Live V",
                stock = 15,
                imageUrl = R.drawable.kurumi
            ),
            Figure(
                id = 2,
                name = "Sakurajima Mai Bunny Girl Ver. Artist MasterPiece AMP+ - Bunny Girl Senpai | TAITO Figure",
                description = "Phiên bản đặc biệt Artist MasterPiece+ của Sakurajima Mai trong trang phục bunny girl, sản xuất bởi Taito.",
                price = 450000.0,
                brand = "Taito",
                releaseDate = "2024-08-15",
                category = "AMP+",
                series = "Bunny Girl Senpai",
                stock = 10,
                imageUrl = R.drawable.mai
            ),
            Figure(
                id = 3,
                name = "Nendoroid 1979 Hoshimachi Suisei - Hololive | Good Smile Company Figure",
                description = "Nendoroid đáng yêu của VTuber Hoshimachi Suisei từ Hololive, đi kèm nhiều phụ kiện và biểu cảm.",
                price = 1350000.0,
                brand = "Good Smile Company",
                releaseDate = "2025-01-20",
                category = "Nendoroid",
                series = "Hololive",
                stock = 5,
                imageUrl = R.drawable.suisei
            ),
            Figure(
                id = 4,
                name = "Nakano Miku Bloo-me - Gotoubun no Hanayome | FuRyu Figure",
                description = "Mô hình Nakano Miku trong dòng sản phẩm Bloo-me của FuRyu, tái hiện vẻ đẹp dịu dàng.",
                price = 490000.0,
                brand = "FuRyu",
                releaseDate = "2024-11-30",
                category = "Scale Figure",
                series = "Gotoubun no Hanayome",
                stock = 12,
                imageUrl = R.drawable.nakano_miku
            ),
            Figure(
                id = 5,
                name = "Ainz Ooal Gown - Overlord | Bandai Spirits Figure",
                description = "Mô hình mạnh mẽ và uy nghi của Ainz Ooal Gown từ series Overlord, sản xuất bởi Bandai Spirits.",
                price = 690000.0,
                brand = "Bandai Spirits",
                releaseDate = "2024-12-10",
                category = "Scale Figure",
                series = "Overlord",
                stock = 8,
                imageUrl = R.drawable.ainz_ooal_gown
            ),
            Figure(
                id = 6,
                name = "Nendoroid 1688 Gawr Gura - hololive production | Good Smile Company Figure",
                description = "Nendoroid 'cá mập' Gawr Gura siêu cấp đáng yêu từ Hololive English, sản xuất bởi Good Smile Company.",
                price = 2450000.0,
                brand = "Good Smile Company",
                releaseDate = "2024-09-01",
                category = "Nendoroid",
                series = "Hololive",
                stock = 3,
                imageUrl = R.drawable.gawr_gura
            )
        )

        recyclerView.adapter = FigureAdapter(figures)
    }
}