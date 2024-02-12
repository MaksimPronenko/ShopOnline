package home.samples.shoponline.ui.catalog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import home.samples.shoponline.R
import home.samples.shoponline.databinding.FragmentCatalogBinding
import home.samples.shoponline.ui.ViewModelState
import home.samples.shoponline.ui.adapters.ProductAdapter
import home.samples.shoponline.utils.ARG_ID
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CatalogFragment"

@AndroidEntryPoint
class CatalogFragment : Fragment() {

    @Inject
    lateinit var catalogViewModelFactory: CatalogViewModelFactory
    private val viewModel: CatalogViewModel by viewModels { catalogViewModelFactory }

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productAdapter = ProductAdapter(
            context = requireContext(),
            onItemClick = { id -> onItemClick(id) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = false

        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Функция onViewCreated() запущена")

        binding.catalogRecycler.adapter = productAdapter

        val dividerItemDecorationVertical = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val dividerItemDecorationHorizontal =
            DividerItemDecoration(context, RecyclerView.HORIZONTAL)
        context?.let { ContextCompat.getDrawable(it, R.drawable.divider_drawable) }
            ?.let {
                dividerItemDecorationVertical.setDrawable(it)
                dividerItemDecorationHorizontal.setDrawable(it)
            }
        binding.catalogRecycler.addItemDecoration(dividerItemDecorationVertical)
        binding.catalogRecycler.addItemDecoration(dividerItemDecorationHorizontal)

        val catalogSortingAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sorting_types,
            R.layout.catalog_sorting_spinner_item
        )
        catalogSortingAdapter.setDropDownViewResource(R.layout.catalog_sorting_spinner_dropdown_item)
        binding.sorting.adapter = catalogSortingAdapter

        binding.sorting.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "error", Toast.LENGTH_LONG).show()
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val type = parent?.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), position.toString(), Toast.LENGTH_LONG).show()
                viewModel.sortProducts(position)
            }
        }

        binding.catalogFilterGroup.setOnCheckedStateChangeListener { _, _ ->
            if (binding.chipWatchAll.isChecked) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.watch_all),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.filterProducts(productType = 0)
            } else if (binding.chipFace.isChecked) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.face),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.filterProducts(productType = 1)
            } else if (binding.chipBody.isChecked) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.body),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.filterProducts(productType = 2)
            } else if (binding.chipSuntan.isChecked) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.suntan),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.filterProducts(productType = 3)
            } else if (binding.chipMask.isChecked) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.mask),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.filterProducts(productType = 4)
            } else {
                Toast.makeText(requireContext(), "Чипы не выбраны", Toast.LENGTH_LONG).show()
                viewModel.filterProducts(productType = 0)
            }
        }

        statesProcessing()
    }

    private fun statesProcessing() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                binding.progress.isVisible = true
                                binding.catalogRecycler.isVisible = false
                            }

                            ViewModelState.Loaded -> {
                                binding.progress.isVisible = false
                                viewModel.productsFlow.onEach { product ->
                                    productAdapter.setData(product)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                                binding.catalogRecycler.isVisible = true
                                refreshChipGroup()
                            }

                            ViewModelState.Error -> {
                                binding.progress.isVisible = false
                                binding.catalogRecycler.isVisible = false
                            }
                        }
                    }
            }
        }
    }

    private fun refreshChipGroup() {
        when (viewModel.chosenProductType) {
            0 -> {
                binding.chipWatchAll.isChecked = true
                binding.chipWatchAll.isCloseIconVisible = true
                binding.chipFace.isCloseIconVisible = false
                binding.chipBody.isCloseIconVisible = false
                binding.chipSuntan.isCloseIconVisible = false
                binding.chipMask.isCloseIconVisible = false
            }

            1 -> {
                binding.chipFace.isChecked = true
                binding.chipWatchAll.isCloseIconVisible = false
                binding.chipFace.isCloseIconVisible = true
                binding.chipBody.isCloseIconVisible = false
                binding.chipSuntan.isCloseIconVisible = false
                binding.chipMask.isCloseIconVisible = false
            }

            2 -> {
                binding.chipBody.isChecked = true
                binding.chipWatchAll.isCloseIconVisible = false
                binding.chipFace.isCloseIconVisible = false
                binding.chipBody.isCloseIconVisible = true
                binding.chipSuntan.isCloseIconVisible = false
                binding.chipMask.isCloseIconVisible = false
            }

            3 -> {
                binding.chipSuntan.isChecked = true
                binding.chipWatchAll.isCloseIconVisible = false
                binding.chipFace.isCloseIconVisible = false
                binding.chipBody.isCloseIconVisible = false
                binding.chipSuntan.isCloseIconVisible = true
                binding.chipMask.isCloseIconVisible = false
            }

            4 -> {
                binding.chipMask.isChecked = true
                binding.chipWatchAll.isCloseIconVisible = false
                binding.chipFace.isCloseIconVisible = false
                binding.chipBody.isCloseIconVisible = false
                binding.chipSuntan.isCloseIconVisible = false
                binding.chipMask.isCloseIconVisible = true
            }

            else -> {
                binding.chipWatchAll.isCloseIconVisible = false
                binding.chipFace.isCloseIconVisible = false
                binding.chipBody.isCloseIconVisible = false
                binding.chipSuntan.isCloseIconVisible = false
                binding.chipMask.isCloseIconVisible = false
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
            R.id.action_CatalogFragment_to_ProductFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}