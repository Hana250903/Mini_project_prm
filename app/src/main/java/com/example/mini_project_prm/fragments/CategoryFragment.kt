package com.example.mini_project_prm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        recyclerView = view.findViewById(R.id.recyclerViewFigures)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        fetchFigures()
    }

    private fun fetchFigures() {
        lifecycleScope.launch {
            try {
                val figures = RetrofitClient.instance.getFigures()
                adapter = FigureAdapter(figures)
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                // Có thể show lỗi ở TextView hoặc Toast
            }
        }
    }
}
