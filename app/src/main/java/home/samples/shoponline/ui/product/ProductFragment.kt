package home.samples.shoponline.ui.product

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
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import home.samples.shoponline.R
import home.samples.shoponline.databinding.FragmentProductBinding
import home.samples.shoponline.ui.ViewModelState
import home.samples.shoponline.ui.adapters.ProductAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.id = arguments?.getString(ARG_ID) ?: ""
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
                            }
                            ViewModelState.Loaded -> {
                                binding.progress.isVisible = false
                            }
                            ViewModelState.Error -> {
                                binding.progress.isVisible = false
                            }
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}