package com.example.coffeeshop.Activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.Adapter.CoffeeAdapter
import com.example.coffeeshop.Domain.ItemsModel
import com.example.coffeeshop.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private lateinit var searchInput: EditText
    private lateinit var btnSearch: ImageButton
    private lateinit var btnBack: ImageButton
    private lateinit var rvCoffee: RecyclerView
    private lateinit var rvFeatured: RecyclerView
    private lateinit var categoryLayout: LinearLayout
    private lateinit var database: DatabaseReference
    private lateinit var allItems: ArrayList<ItemsModel>
    private lateinit var filteredItems: ArrayList<ItemsModel>
    private lateinit var featuredItems: ArrayList<ItemsModel>
    private lateinit var adapter: CoffeeAdapter
    private lateinit var featuredAdapter: CoffeeAdapter
    private val categoryList = listOf("Espresso", "Cappuccino", "Latte", "Mocha", "Americano")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        loadItems()
        setupCategoryButtons()
        setupSearch()
    }

    private fun initViews() {
        searchInput = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSubmitSearch)
        btnBack = findViewById(R.id.btnBack)
        rvCoffee = findViewById(R.id.rvCoffeeList)
        rvFeatured = findViewById(R.id.rvFeaturedCoffee)
        categoryLayout = findViewById(R.id.layoutCategory)

        btnBack.setOnClickListener { finish() }

        // Khởi tạo danh sách và adapter
        allItems = ArrayList()
        filteredItems = ArrayList()
        featuredItems = ArrayList()
        adapter = CoffeeAdapter(filteredItems)
        featuredAdapter = CoffeeAdapter(featuredItems)

        // Cấu hình RecyclerView
        rvCoffee.layoutManager = GridLayoutManager(this, 2)
        rvCoffee.adapter = adapter
        rvFeatured.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFeatured.adapter = featuredAdapter
    }

    private fun loadItems() {
        database = FirebaseDatabase.getInstance().getReference("Items")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allItems.clear()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(ItemsModel::class.java)
                    item?.let {
                        it.itemId = itemSnapshot.key ?: "" // Gán itemId từ key của Firebase
                        allItems.add(it)
                    }
                }

                // Cập nhật danh sách lọc
                filteredItems.clear()
                filteredItems.addAll(allItems)
                adapter.notifyDataSetChanged()

                // Cập nhật danh sách nổi bật (tối đa 6 món, sắp xếp theo rating)
                featuredItems.clear()
                val sortedItems = allItems.sortedByDescending { it.rating }
                featuredItems.addAll(sortedItems.take(6))
                featuredAdapter.notifyDataSetChanged()

                if (allItems.isEmpty()) {
                    Toast.makeText(this@SearchActivity, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchActivity, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearch() {
        btnSearch.setOnClickListener {
            val query = searchInput.text.toString().trim()
            filterByKeyword(query)
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterByKeyword(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterByKeyword(keyword: String) {
        filteredItems.clear()
        if (keyword.isEmpty()) {
            filteredItems.addAll(allItems)
        } else {
            for (item in allItems) {
                if (item.title.contains(keyword, ignoreCase = true) || item.description.contains(keyword, ignoreCase = true)) {
                    filteredItems.add(item)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun setupCategoryButtons() {
        categoryLayout.removeAllViews()
        for (category in categoryList) {
            val btn = Button(this).apply {
                text = category
                textSize = 14f
                setBackgroundResource(R.drawable.bg_category_button)
                setPadding(20, 10, 20, 10)
                setTextColor(resources.getColor(android.R.color.white))
                backgroundTintList = resources.getColorStateList(R.color.brown)
                setOnClickListener { filterByCategory(category) }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            categoryLayout.addView(btn, params)
        }
    }

    private fun filterByCategory(category: String) {
        filteredItems.clear()
        for (item in allItems) {
            if (item.title.contains(category, ignoreCase = true)) {
                filteredItems.add(item)
            }
        }

        if (filteredItems.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy sản phẩm trong thể loại \"$category\"", Toast.LENGTH_SHORT).show()
        }

        adapter.notifyDataSetChanged()
    }
}