package com.example.coffeeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.Adapter.FavoritesAdapter
import com.example.coffeeshop.Domain.FavoritesModel
import com.example.coffeeshop.Domain.ItemsModel
import com.example.coffeeshop.R
import com.google.firebase.database.*

class FavoritesActivity : AppCompatActivity() {

    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var favoritesList: MutableList<FavoritesModel>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val ivBack: ImageView = findViewById(R.id.ivBack)
        val rvFavorites: RecyclerView = findViewById(R.id.rvFavorites)

        favoritesList = mutableListOf()
        favoritesAdapter = FavoritesAdapter(
            favoritesList,
            onDeleteClick = { item -> deleteFavorite(item.itemId) },
            onItemClick = { item ->
                // Sửa lại cách truy vấn: tìm item có field "itemId" khớp với giá trị
                val query = FirebaseDatabase.getInstance().getReference("items")
                    .orderByChild("itemId")
                    .equalTo(item.itemId)

                query.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        // Lấy item đầu tiên tìm thấy (vì itemId là duy nhất)
                        val firstChild = snapshot.children.first()
                        val map = firstChild.getValue<Map<String, Any>>() ?: return@addOnSuccessListener

                        val itemModel = ItemsModel().apply {
                            itemId = map["itemId"] as? String ?: ""
                            title = map["title"] as? String ?: ""
                            description = map["description"] as? String ?: ""
                            price = (map["price"] as? Number)?.toDouble() ?: 0.0
                            rating = (map["rating"] as? Number)?.toDouble() ?: 0.0
                            extra = map["extra"] as? String ?: ""

                            // Xử lý ảnh: thêm tất cả các URL ảnh
                            map.keys.filter { it.startsWith("picUrl") }.sorted().forEach { key ->
                                (map[key] as? String)?.let { picUrl.add(it) }
                            }
                        }

                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putExtra("object", itemModel)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )

        rvFavorites.layoutManager = LinearLayoutManager(this)
        rvFavorites.adapter = favoritesAdapter

        databaseRef = FirebaseDatabase.getInstance().reference.child("favorites")

        loadFavorites()

        ivBack.setOnClickListener {
            finish()
        }
    }

    private fun loadFavorites() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favoritesList.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(FavoritesModel::class.java)
                    item?.let { favoritesList.add(it) }
                }
                favoritesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FavoritesActivity, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteFavorite(itemId: String) {
        databaseRef.child(itemId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Đã xoá khỏi yêu thích", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Xoá thất bại", Toast.LENGTH_SHORT).show()
            }
    }
}