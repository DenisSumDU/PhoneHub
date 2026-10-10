package com.example.phonehub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.phonehub.databinding.FragmentPhoneDetailBinding
import java.util.Locale

class DetailFragment : Fragment(R.layout.fragment_phone_detail) {

    private var _binding: FragmentPhoneDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhoneViewModel by viewModels {
        val application = requireActivity().application as PhoneApplication
        PhoneViewModelFactory(application.repository)
    }

    private var phoneId: String? = null
    private var currentPhone: PhoneEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phoneId = arguments?.getString("phone_id")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPhoneDetailBinding.bind(view)

        viewModel.phones.observe(viewLifecycleOwner) { phones ->
            val phone = phones.find { it.id == phoneId }
            if (phone != null) {
                currentPhone = phone
                bindPhoneData(phone)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindPhoneData(phone: PhoneEntity) {
        binding.tvDetailTitle.text = "${phone.brand} ${phone.model}"

        binding.tvDetailPrice.text = String.format(Locale.getDefault(), "%.2f грн", phone.price)

        binding.tvFullSpecs.text = "• Оперативна пам'ять: ${phone.ramGb} GB\n" +
                "• Вбудована пам'ять: ${phone.storageGb} GB\n" +
                "• Ємність акумулятора: ${phone.batteryCapacity} mAh"

        updateFavoriteButtonUI(phone.isFavorite)

        binding.btnFavorite.setOnClickListener {
            currentPhone?.let { p ->
                viewModel.toggleFavorite(p)

                val newFavoriteStatus = !p.isFavorite
                updateFavoriteButtonUI(newFavoriteStatus)

                val msg = if (newFavoriteStatus) {
                    "Додано в обране"
                } else {
                    "Видалено з обраного"
                }
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoriteButtonUI(isFav: Boolean) {
        if (isFav) {
            binding.btnFavorite.text = getString(R.string.btn_favorite_remove)
        } else {
            binding.btnFavorite.text = getString(R.string.btn_favorite_add)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}