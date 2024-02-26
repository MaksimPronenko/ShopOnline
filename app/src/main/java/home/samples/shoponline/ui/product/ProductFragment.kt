package home.samples.shoponline.ui.product

import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.content.res.AppCompatResources
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
import home.samples.shoponline.databinding.FragmentProductBinding
import home.samples.shoponline.ui.ViewModelState
import home.samples.shoponline.ui.adapters.ImageAdapterBig
import home.samples.shoponline.ui.adapters.ProductInfoAdapter
import home.samples.shoponline.utils.ARG_ID
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
        val receivedId = arguments?.getString(ARG_ID) ?: ""
        Log.d(
            TAG,
            "Функция onCreate() запущена. Получен id = $receivedId. viewModel.id = ${viewModel.id}"
        )
        if (receivedId != viewModel.id && receivedId.isNotBlank())
            viewModel.loadProductData(receivedId)
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

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addToFavoritesButton.setOnClickListener {
            viewModel.changeFavouriteStatus()
        }

        binding.hideDescriptionButton.setOnClickListener {
            viewModel.showHideDescription()
        }

        binding.hideIngredientsButton.setOnClickListener {
            viewModel.showHideIngredients()
        }

        binding.ingredients.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout() binding.ingredients.lineCount = ${binding.ingredients.layout.lineCount}")
                if (binding.ingredients.layout.lineCount >= 1) {
                    viewModel.ingredientsTextLinesCount = binding.ingredients.layout.lineCount
                    Log.d(
                        TAG,
                        "lines = ${viewModel.ingredientsTextLinesCount}"
                    )
                    binding.hideIngredientsButton.isGone = viewModel.ingredientsTextLinesCount <= 2
                    binding.ingredients.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })

        channelProcessing()
        statesProcessing()
    }

    private fun channelProcessing() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favouriteChannel.collect {
                    setFavoriteState(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showHideDescriptionChannel.collect { isVisible ->
                    Log.d(TAG, "Канал видимости описания = $isVisible")
                    binding.brandButton.isGone = !isVisible
                    binding.description.isGone = !isVisible
                    binding.hideDescriptionButton.text =
                        requireContext().getString(if (isVisible) R.string.hide else R.string.detail)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showHideIngredientsChannel.collect { isVisible ->
                    Log.d(TAG, "Канал видимости состава = $isVisible")
                    binding.ingredients.maxLines =
                        if (isVisible) binding.ingredients.lineCount else 2
                    binding.hideIngredientsButton.text =
                        requireContext().getString(if (isVisible) R.string.hide else R.string.detail)
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
                                binding.progress.isVisible = true
                                binding.scrollView.isVisible = false
                            }

                            ViewModelState.Loaded -> {
                                binding.progress.isVisible = false
                                binding.scrollView.isVisible = true
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
        Log.d(TAG, "Загружаем в адаптер картинки = ${productImages.toList()}")
        productImageAdapterBig.setData(productImages.toList())

        setFavoriteState(data.favourite)

        Log.d(TAG, "Загружаем title = ${data.productTable.productDataTable.title}")
        binding.title.text = data.productTable.productDataTable.title

        binding.subtitle.text = data.productTable.productDataTable.subtitle

        binding.available.text = getAvailableText(data.productTable.productDataTable.available)

        setRatingStars(data.productTable.productDataTable.rating)

        binding.rating.text = data.productTable.productDataTable.rating.toString()

        binding.feedbackCount.text =
            getFeedbackCountText(data.productTable.productDataTable.feedbackCount)

        Log.d(TAG, "unit = ${data.productTable.productDataTable.unit}")
        val newPriceText =
            "${data.productTable.productDataTable.priceWithDiscount} ${data.productTable.productDataTable.unit}"
        binding.newPrice.text = newPriceText
        binding.newPriceInButton.text = newPriceText

        val oldPriceText =
            SpannableString("${data.productTable.productDataTable.price} ${data.productTable.productDataTable.unit}")
        oldPriceText.setSpan(StrikethroughSpan(), 0, oldPriceText.length, 0)
        binding.oldPrice.text = oldPriceText
        binding.oldPriceInButton.text = oldPriceText

        val priceDiscountText = "-${data.productTable.productDataTable.discount}%"
        binding.priceDiscount.text = priceDiscountText

        binding.brandButton.text = data.productTable.productDataTable.title

        binding.description.text = data.productTable.productDataTable.description

        productInfoAdapter.setData(data.productTable.info)

        binding.ingredients.text = data.productTable.productDataTable.ingredients
    }

    private fun setFavoriteState(state: Boolean) {
        binding.addToFavoritesButton.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                if (state) R.drawable.heart_filled else R.drawable.heart_empty
            )
        )
    }

    private fun getAvailableText(available: Int) =
        requireContext().getString(R.string.available) + " $available " +
                when {
                    available % 10 == 1 && available % 100 != 11 -> requireContext().getString(R.string.piece_1)
                    available % 10 in 2..4 && available % 100 !in 12..14 -> requireContext().getString(
                        R.string.piece_3
                    )
                    else -> requireContext().getString(R.string.piece_2)
                }

    private fun getFeedbackCountText(feedbackCount: Int) = "$feedbackCount ${
        when {
            feedbackCount % 10 == 1 && feedbackCount % 100 != 11 -> requireContext().getString(R.string.feedback_1)
            feedbackCount % 10 in 2..4 && feedbackCount % 100 !in 12..14 -> requireContext().getString(
                R.string.feedback_2
            )
            else -> requireContext().getString(R.string.feedback_3)
        }
    }"

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