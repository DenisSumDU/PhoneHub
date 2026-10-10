package com.example.phonehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteFragment : Fragment() {

    private var recyclerView: RecyclerView? = null

    private val viewModel: PhoneViewModel by activityViewModels {
        val application = requireActivity().application as PhoneApplication
        PhoneViewModelFactory(application.repository)
    }

    private lateinit var phoneAdapter: PhoneAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvFavoritePhones)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Обов'язково додаємо LayoutManager, інакше елементи не відобразяться на екрані
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        phoneAdapter = PhoneAdapter(
            phones = emptyList(),
            onPhoneClick = { phone ->
                val bundle = Bundle().apply { putString("phone_id", phone.id) }
                try {
                    findNavController().navigate(R.id.detailFragment, bundle)
                } catch (_: Exception) {}
            },
            onCompareClick = { phone ->
                viewModel.toggleCompare(phone)
                Toast.makeText(requireContext(), "Додано до порівняння: ${phone.model}", Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { phone ->
                viewModel.toggleFavorite(phone)
            }
        )
        recyclerView?.adapter = phoneAdapter
    }

    private fun observeViewModel() {
        viewModel.favoritePhones.observe(viewLifecycleOwner) { phones ->
            phoneAdapter.updateData(phones)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView = null
    }
}