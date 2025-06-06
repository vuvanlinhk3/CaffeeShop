package com.example.coffeeshop.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.Adapter.CartAdapter
import com.example.coffeeshop.Helper.ChangeNumberItemsListener
import com.example.coffeeshop.Helper.ManagmentCart
import com.example.coffeeshop.R
import com.example.coffeeshop.databinding.ActivityCartBinding
import com.example.coffeeshop.databinding.ActivityItemsListBinding
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {
    lateinit var binding: ActivityCartBinding
    lateinit var managmentCart: ManagmentCart
    private  var tax: Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart= ManagmentCart(this)

        calculateCart()
        setVariable()
        initCartList()

    }

    private fun initCartList() {
        binding.apply {
            listView.layoutManager=
                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL,false)
            listView.adapter= CartAdapter(
                managmentCart.getListCart(),
                this@CartActivity,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        calculateCart()
                    }

                }
            )
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun calculateCart() {
        val percentTax = 0.02
        val delivery = 15000  // Đổi sang VNĐ
        val currencyVN = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

        val itemTotal = managmentCart.getTotalFee()
        tax = itemTotal * percentTax
        val total = itemTotal + tax + delivery

        binding.apply {
            totalFeeTxt.text = currencyVN.format(itemTotal)
            totalTaxTxt.text = currencyVN.format(tax)
            deliveryTxt.text = currencyVN.format(delivery)
            totalTxt.text = currencyVN.format(total)
        }
    }

}