package com.example.coffeeshop.Domain

import java.io.Serializable

data class FavoritesModel(
    val itemId: String = "",               // ID duy nhất cho sản phẩm (nếu có)
    val title: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",             // Dùng 1 ảnh đại diện (thumbnail)
    val timeAdded: Long = System.currentTimeMillis() // Thời gian thêm vào yêu thích
) : Serializable
