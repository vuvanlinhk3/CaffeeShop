package com.example.coffeeshop.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeshop.R
import com.example.coffeeshop.Domain.FavoritesModel
import java.text.NumberFormat
import java.util.Locale
class FavoritesAdapter(
    private val items: List<FavoritesModel>,
    private val onDeleteClick: (FavoritesModel) -> Unit,
    private val onItemClick: (FavoritesModel) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    inner class FavoritesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivProductImage)
        val name: TextView = view.findViewById(R.id.tvProductName)
        val price: TextView = view.findViewById(R.id.tvProductPrice)
        val deleteBtn: ImageView = view.findViewById(R.id.ivDeleteFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_product, parent, false)
        return FavoritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val item = items[position]

        holder.name.text = item.title
        val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        holder.price.text = format.format(item.price)


        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.white_bg)
            .into(holder.image)

        holder.deleteBtn.setOnClickListener { onDeleteClick(item) }
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
