package com.example.mini_project_prm.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.adapters.FigureAdapter
import com.example.mini_project_prm.api.RetrofitClient
import com.example.mini_project_prm.models.Figure
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imgBackround = view.findViewById<ImageView>(R.id.imgBackground)
        imgBackround.setImageResource(R.drawable.background_first)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewFigures)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val figures = listOf(
            Figure(
                id = 1,
                name = "Tokisaki Kurumi Nightwear ver. Desktop Cute - Date A Live V | Taito Figure.",
                description = "PVC figure with detailed sculpt and color. Approx 18cm tall.",
                price = 490000.0,
                brand = "Taito",
                releaseDate = "2024-04-01",
                category = "Nightwear",
                series = "Date A Live",
                stock = 10,
                imageUrl = R.drawable.kurumi
            ),
            Figure(
                id = 2,
                name = "Sakurajima Mai Bunny Girl Ver. Artist MasterPiece AMP+ - Bunny Girl Senpai | TAITO Figure",
                description = "From the popular anime Bunny Girl Senpai. Highly detailed sculpt.",
                price = 450000.0,
                brand = "Taito",
                releaseDate = "2023-12-15",
                category = "Bunny Girl",
                series = "Rascal Does Not Dream",
                stock = 8,
                imageUrl = R.drawable.mai
            ),
            Figure(
                id = 3,
                name = "Nendoroid 1979 Hoshimachi Suisei - Hololive | Good Smile Company Figure",
                description = "Hololive idol Suisei in cute Nendoroid form. Includes accessories.",
                price = 1350000.0,
                brand = "Good Smile Company",
                releaseDate = "2023-09-10",
                category = "Nendoroid",
                series = "Hololive",
                stock = 15,
                imageUrl = R.drawable.suisei
            ),
            Figure(
                id = 4,
                name = "Nakano Miku Bloo-me - Gotoubun no Hanayome | FuRyu Figure",
                description = "Miku in Bloo-me version from The Quintessential Quintuplets.",
                price = 490000.0,
                brand = "FuRyu",
                releaseDate = "2024-01-20",
                category = "Romantic",
                series = "Gotoubun no Hanayome",
                stock = 12,
                imageUrl = R.drawable.nakano_miku
            ),
            Figure(
                id = 5,
                name = "Ainz Ooal Gown - Overlord | Bandai Spirits Figure",
                description = "Powerful Ainz from Overlord. Great detail and paint finish.",
                price = 690000.0,
                brand = "Bandai Spirits",
                releaseDate = "2024-02-05",
                category = "Fantasy",
                series = "Overlord",
                stock = 6,
                imageUrl = R.drawable.ainz_ooal_gown
            ),
            Figure(
                id = 6,
                name = "Nendoroid 1688 Gawr Gura - hololive production | Good Smile Company Figure",
                description = "Super popular VTuber Gura as an adorable Nendoroid.",
                price = 2450000.0,
                brand = "Good Smile Company",
                releaseDate = "2023-08-01",
                category = "VTuber",
                series = "Hololive",
                stock = 5,
                imageUrl = R.drawable.gawr_gura
            )
        )
        recyclerView.adapter = FigureAdapter(figures)

        super.onViewCreated(view, savedInstanceState)

        // Gọi API bằng coroutine
        lifecycleScope.launch {
            try {
                val figures = RetrofitClient.instance.getFigures()
                // dùng data như gán adapter, cập nhật UI
                Log.d("API_RESULT", "Số figure: ${figures.size}")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi API: ${e.message}")
            }
        }
    }
}