package home.samples.shoponline.ui.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import home.samples.shoponline.R
import home.samples.shoponline.databinding.FragmentProductBinding
import home.samples.shoponline.ui.ViewModelState
import home.samples.shoponline.ui.adapters.ImageAdapterBig
import home.samples.shoponline.ui.adapters.ProductInfoAdapter
import home.samples.shoponline.utils.ARG_ID
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ProductFragment"

@AndroidEntryPoint
class ProductFragment : Fragment() {

    @Inject
    lateinit var productViewModelFactory: ProductViewModelFactory
    private val viewModel: ProductViewModel by viewModels { productViewModelFactory }

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var productImageAdapterBig: ImageAdapterBig

    private var productInfoAdapter: ProductInfoAdapter = ProductInfoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.id = arguments?.getString(ARG_ID) ?: ""
        productImageAdapterBig = ImageAdapterBig(context = requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = true

        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Функция onViewCreated() запущена")
        binding.productImagePager.adapter = productImageAdapterBig
        binding.productInfoRecycler.adapter = productInfoAdapter

        binding.addToFavoritesButton.setOnClickListener {
            viewModel.changeFavouriteStatus()
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
                                binding.scrollView.isVisible = true
                            }

                            ViewModelState.Loaded -> {
                                binding.progress.isVisible = false
                                binding.scrollView.isVisible = false
                                showProductData()
                            }

                            ViewModelState.Error -> {
                                binding.progress.isVisible = false
                                binding.scrollView.isVisible = false
                            }
                        }
                    }
            }
        }
    }

    private fun showProductData() {
        val data = viewModel.loadingDataResult!!
        val productImages: MutableList<String> = mutableListOf()
        data.productTable.images.forEach {
            productImages.add(it.imageURIString)
        }
        productImageAdapterBig.setData(productImages.toList())

        binding.addToFavoritesButton.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                if (data.favourite) R.drawable.heart_filled else R.drawable.heart_empty
            )
        )

        binding.title.text = data.productTable.productDataTable.title
        binding.subtitle.text = data.productTable.productDataTable.subtitle
        binding.available.text = getAvailableText(data.productTable.productDataTable.available)

        setRatingStars(data.productTable.productDataTable.rating)

        viewModel.productInfoFlow.onEach { productList ->
            productInfoAdapter.setData(productList)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.newPrice.text =
            data.productTable.productDataTable.priceWithDiscount
        binding.newPriceInButton.text =
            data.productTable.productDataTable.priceWithDiscount
        binding.oldPrice.text = data.productTable.productDataTable.price
        binding.oldPrice.text = data.productTable.productDataTable.price
        binding.priceDiscount.text = data.productTable.productDataTable.discount.toString()
    }

    private fun getAvailableText(available: Int): String {
        val part1 = requireContext().getString(R.string.available) + " $available "
        val part2 = when (available % 10) {
            1 -> requireContext().getString(R.string.piece_1)
            in 2..4 -> requireContext().getString(R.string.piece_3)
            else -> requireContext().getString(R.string.piece_2)
        }
        return part1 + part2
    }

    private fun setRatingStars(rating: Float) {
        binding.star0.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                when {
                    rating < 0.25 -> R.drawable.star_empty
                    rating in 0.25..0.75 -> R.drawable.star_half
                    else -> R.drawable.star_full
                }
            )
        )
        binding.star1.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                when {
                    rating < 1.25 -> R.drawable.star_empty
                    rating in 1.25..1.75 -> R.drawable.star_half
                    else -> R.drawable.star_full
                }
            )
        )
        binding.star2.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                when {
                    rating < 2.25 -> R.drawable.star_empty
                    rating in 2.25..2.75 -> R.drawable.star_half
                    else -> R.drawable.star_full
                }
            )
        )
        binding.star3.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                when {
                    rating < 3.25 -> R.drawable.star_empty
                    rating in 3.25..3.75 -> R.drawable.star_half
                    else -> R.drawable.star_full
                }
            )
        )
        binding.star4.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                when {
                    rating < 4.25 -> R.drawable.star_empty
                    rating in 4.25..4.75 -> R.drawable.star_half
                    else -> R.drawable.star_full
                }
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}