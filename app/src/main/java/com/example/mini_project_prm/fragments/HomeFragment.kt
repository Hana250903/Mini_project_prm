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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imgBackround = view.findViewById<ImageView>(R.id.imgBackground)
        imgBackround.setImageResource(R.drawable.background_first)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewFigures)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val figures = listOf(
            Figure(
                "Tokisaki Kurumi Nightwear ver. Desktop Cute - Date A Live V | Taito Figure.",
                490000.0,
                R.drawable.kurumi
            ),
            Figure(
                "Sakurajima Mai Bunny Girl Ver. Artist MasterPiece AMP+ - Bunny Girl Senpai | TAITO Figure",
                450000.0,
                R.drawable.mai
            ),
            Figure(
                "Nendoroid 1979 Hoshimachi Suisei - Hololive | Good Smile Company Figure",
                1350000.0,
                R.drawable.suisei
            ),
            Figure(
                "Nakano Miku Bloo-me - Gotoubun no Hanayome | FuRyu Figure",
                490000.0,
                R.drawable.nakano_miku
            ),
            Figure(
                "Ainz Ooal Gown - Overlord | Bandai Spirits Figure",
                690000.0,
                R.drawable.ainz_ooal_gown
            ),
            Figure(
                "Nendoroid 1688 Gawr Gura - hololive production | Good Smile Company Figure",
                2450000.0,
                R.drawable.gawr_gura
            )
        )

        recyclerView.adapter = FigureAdapter(figures)
    }
}