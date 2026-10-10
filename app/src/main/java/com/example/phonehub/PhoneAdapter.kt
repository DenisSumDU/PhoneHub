package com.example.phonehub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load // Імпортуємо бібліотеку Coil

class PhoneAdapter(
    private var phones: List<PhoneEntity>,
    private val onPhoneClick: (PhoneEntity) -> Unit,
    private val onCompareClick: (PhoneEntity) -> Unit,
    private val onFavoriteClick: (PhoneEntity) -> Unit
) : RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder>() {

    class PhoneViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPhone: ImageView = view.findViewById(R.id.ivPhone)
        val tvBrand: TextView = view.findViewById(R.id.tvBrand)
        val tvModel: TextView = view.findViewById(R.id.tvModel)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val btnFavorite: ImageButton = view.findViewById(R.id.btnFavorite)
        val btnCompare: Button = view.findViewById(R.id.btnCompare)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_phone, parent, false)
        return PhoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhoneViewHolder, position: Int) {
        val phone = phones[position]
        holder.tvBrand.text = phone.brand
        holder.tvModel.text = phone.model
        holder.tvPrice.text = "${phone.price} грн"

        // Завантаження фото за посиланням з інтернету за допомогою Coil
        holder.ivPhone.load(phone.imageUrl) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery) // Поки завантажується
            error(android.R.drawable.ic_menu_gallery)     // Якщо посилання не працює
        }

        // Іконка обраного
        holder.btnFavorite.setImageResource(
            if (phone.isFavorite) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            }
        )

        holder.itemView.setOnClickListener {
            onPhoneClick(phone)
        }

        holder.btnFavorite.setOnClickListener {
            onFavoriteClick(phone)
        }

        holder.btnCompare.setOnClickListener {
            onCompareClick(phone)
        }
    }

    override fun getItemCount(): Int = phones.size

    fun updateData(newPhones: List<PhoneEntity>) {
        phones = newPhones
        notifyDataSetChanged()
    }
}