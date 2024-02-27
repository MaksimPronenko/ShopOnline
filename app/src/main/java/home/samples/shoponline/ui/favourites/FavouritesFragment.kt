package home.samples.shoponline.ui.favourites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import home.samples.shoponline.R
import home.samples.shoponline.databinding.FragmentFavouritesBinding
import home.samples.shoponline.ui.ViewModelState
import home.samples.shoponline.ui.adapters.ProductAdapter
import home.samples.shoponline.utils.ARG_ID
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FavouritesFragment"

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    @Inject
    lateinit var favouritesViewModelFactory: FavouritesViewModelFactory
    private val viewModel: FavouritesViewModel by viewModels { favouritesViewModelFactory }

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productAdapter = ProductAdapter(
            context = requireContext(),
            onItemClick = { id -> onItemClick(id) },
            addToFavorites = { id, _ -> viewModel.removeFavourite(id) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = false

        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Функция onViewCreated() запущена")

        binding.favouritesRecycler.adapter = productAdapter

        viewModel.loadFavouritesData()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.favouritesChipGroup.setOnCheckedStateChangeListener { _, _ ->
            if (binding.chipProducts.isChecked) viewModel.handleChipChoice(type = 0)
            else if (binding.chipBrands.isChecked) viewModel.handleChipChoice(type = 1)
        }

        channelProcessing()
        statesProcessing()
    }

    private fun channelProcessing() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favouriteProductsChannel.collect { productList ->
                    Log.d(TAG, "productsChannel.collect Product = $productList")
                    productAdapter.setData(productList)
                }
            }
        }
    }

    private fun statesProcessing() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                Log.d(TAG, "ViewModelState.Loading")
                                binding.progress.isVisible = true
                                binding.favouritesRecycler.isVisible = false
                            }

                            ViewModelState.Loaded -> {
                                Log.d(TAG, "ViewModelState.Loaded")
                                binding.progress.isVisible = false
                                binding.favouritesRecycler.isVisible = true
                                viewModel.favouriteProductsFlow.onEach { favouriteProductList ->
                                    productAdapter.setData(favouriteProductList)
                                    Log.d(TAG, "Size of list = ${favouriteProductList.size}")
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                                refreshChipGroup()
                            }

                            ViewModelState.Error -> {
                                Log.d(TAG, "ViewModelState.Error")
                                binding.progress.isVisible = false
                                binding.favouritesRecycler.isVisible = false
                            }
                        }
                    }
            }
        }
    }

    private fun onItemClick(id: String) {
        val bundle =
            Bundle().apply {
                putString(
                    ARG_ID,
                    id
                )
            }
        findNavController().navigate(
            R.id.action_FavouritesFragment_to_ProductFragment,
            bundle
        )
    }

    private fun refreshChipGroup() {
        when (viewModel.chosenChip) {
            0 -> binding.chipProducts.isChecked = true
            1 -> binding.chipBrands.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}