package com.example.mini_project_prm.models

data class OrderWithItems(
    val order: Order,
    var items: List<OrderItem> = emptyList(),
    var isExpanded: Boolean = false
)
