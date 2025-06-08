package com.example.coffeeshop.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.coffeeshop.Domain.FavoritesModel
import com.example.coffeeshop.Domain.ItemsModel
import com.example.coffeeshop.Helper.ManagmentCart
import com.example.coffeeshop.R
import com.example.coffeeshop.databinding.ActivityDetailBinding
import com.example.coffeeshop.databinding.ActivityItemsListBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    private var isFavorite = false
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart= ManagmentCart(this)

        bundle()
        initSizeList()
        val database = FirebaseDatabase.getInstance().reference
        val itemId = item.itemId ?: return
        val title = item.title ?: ""
        val price = item.price ?: 0.0
        val imageUrl = item.picUrl?.getOrNull(0) ?: ""

        val favoriteItem = FavoritesModel(itemId = itemId, title = title, price = price, imageUrl = imageUrl)

        binding.favBtn.setOnClickListener {
            isFavorite = !isFavorite

            if (isFavorite) {
                binding.favBtn.setImageResource(R.drawable.baseline_favorite_24)
                val favoriteRef = database.child("favorites").child(itemId)
                favoriteRef.setValue(favoriteItem)
                    .addOnSuccessListener {
                        Toast.makeText(this@DetailActivity, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@DetailActivity, "Thêm thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                        isFavorite = false
                        binding.favBtn.setImageResource(R.drawable.favorite_white)
                    }
            } else {
                binding.favBtn.setImageResource(R.drawable.favorite_white)
                val favoriteRef = database.child("favorites").child(itemId)
                favoriteRef.removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this@DetailActivity, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@DetailActivity, "Xóa thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                        isFavorite = true
                        binding.favBtn.setImageResource(R.drawable.baseline_favorite_24)
                    }
            }
        }


    }

    private fun initSizeList() {
        binding.apply {
            smallBtn.setOnClickListener {
                smallBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(0)
            }

            mediumBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
                largeBtn.setBackgroundResource(0)
            }

            largeBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
            }
        }
    }

    private fun bundle() {
        binding.apply {
            item=intent.getSerializableExtra("object") as ItemsModel

            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(binding.picMain)

            titleTxt.text=item.title
            descriptionTxt.text=item.description
            priceTxt.text = formatCurrencyVND(item.price)
            ratingTxt.text=item.rating.toString()

            addToCartBtn.setOnClickListener {
                item.numberInCart=Integer.valueOf(
                    numberInCartTxt.text.toString()
                )
                managmentCart.insertItems(item)
            }

            backBtn.setOnClickListener { finish() }

            plusBtn.setOnClickListener {
                numberInCartTxt.text=(item.numberInCart+1).toString()
                item.numberInCart++
            }

            minusBtn.setOnClickListener {
                if(item.numberInCart>0){
                    numberInCartTxt.text=(item.numberInCart-1).toString()
                    item.numberInCart--
                }
            }
        }
    }
    private fun formatCurrencyVND(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return format.format(amount)
    }

}