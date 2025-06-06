package com.example.coffeeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.coffeeshop.Adapter.CategoryAdapter
import com.example.coffeeshop.Adapter.PopularAdapter
import com.example.coffeeshop.Activity.ProfileActivity
import com.example.coffeeshop.ViewModel.MainViewModel
import com.example.coffeeshop.ViewModel.UserViewModel
import com.example.coffeeshop.databinding.ActivityMainBinding
import com.example.coffeeshop.R

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel= MainViewModel()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadUserData()
        initBanner()
        initCategory()
        initPopular()
        initBottomMenu()
    }
    // Hàm mới để tải dữ liệu người dùng
    private fun loadUserData() {
        userViewModel.loadUserData()

        // Quan sát dữ liệu người dùng
        userViewModel.userData.observe(this) { user ->
            if (user != null) {
                // Hiển thị tên người dùng
                binding.textView3.text = user.name

                // Hiển thị ảnh đại diện: nếu có avatarUrl thì tải bằng Glide
                if (user.avatarUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(user.avatarUrl)
                        .circleCrop()
                        .placeholder(R.drawable.profile) // Ảnh mặc định trong lúc tải
                        .error(R.drawable.profile) // Ảnh mặc định nếu lỗi
                        .into(binding.imageView2)
                } else {
                    // Nếu không có URL ảnh, sử dụng ảnh mặc định
                    binding.imageView2.setImageResource(R.drawable.profile)
                }
            } else {
                // Nếu không có dữ liệu người dùng, đặt tên mặc định
                binding.textView3.text = "Khách"
                binding.imageView2.setImageResource(R.drawable.profile)
            }
        }
    }
    private fun initBottomMenu() {
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility=View.VISIBLE
            viewModel.loadPopular().observeForever {
                binding.recyclerViewPopular.layoutManager= GridLayoutManager(this,2)
                binding.recyclerViewPopular.adapter= PopularAdapter(it)
                binding.progressBarPopular.visibility=View.GONE
            }
        viewModel.loadPopular()
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility= View.VISIBLE
        viewModel.loadCategory().observeForever {
            binding.categoryView.layoutManager= LinearLayoutManager(
                this@MainActivity, LinearLayoutManager.HORIZONTAL, false
            )
            binding.categoryView.adapter= CategoryAdapter(it)
            binding.progressBarCategory.visibility= View.GONE
        }
        viewModel.loadCategory()
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility= View.VISIBLE
        viewModel.loadBanneer().observeForever {
            Glide.with(this@MainActivity)
                .load(it[0].url)
                .into(binding.banner)
            binding.progressBarBanner.visibility= View.GONE
        }
        viewModel.loadBanneer()
    }
}