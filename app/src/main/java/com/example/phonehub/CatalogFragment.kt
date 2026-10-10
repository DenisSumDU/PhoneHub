package com.example.phonehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.phonehub.databinding.FragmentCatalogBinding

class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhoneViewModel by activityViewModels {
        val application = requireActivity().application as PhoneApplication
        PhoneViewModelFactory(application.repository)
    }

    private lateinit var phoneAdapter: PhoneAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerView()
        setupSearchView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.catalog_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_favorites -> {
                        findNavController().navigate(R.id.favoriteFragment)
                        true
                    }
                    R.id.action_compare -> {
                        findNavController().navigate(R.id.compareFragment)
                        true
                    }
                    R.id.sort_price_asc -> {
                        viewModel.setSortOrder(PhoneViewModel.SortOrder.PRICE_ASC)
                        true
                    }
                    R.id.sort_price_desc -> {
                        viewModel.setSortOrder(PhoneViewModel.SortOrder.PRICE_DESC)
                        true
                    }
                    R.id.sort_default -> {
                        viewModel.setSortOrder(PhoneViewModel.SortOrder.DEFAULT)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        phoneAdapter = PhoneAdapter(
            phones = emptyList(),
            onPhoneClick = { phone ->
                val bundle = Bundle().apply { putString("phone_id", phone.id) }
                findNavController().navigate(R.id.detailFragment, bundle)
            },
            onCompareClick = { phone ->
                viewModel.toggleCompare(phone)
                Toast.makeText(requireContext(), "Додано до порівняння: ${phone.model}", Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { phone ->
                viewModel.toggleFavorite(phone)
            }
        )
        binding.rvPhones.adapter = phoneAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchQueryChanged(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun observeViewModel() {
        viewModel.phones.observe(viewLifecycleOwner) { phones ->
            phoneAdapter.updateData(phones)
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}