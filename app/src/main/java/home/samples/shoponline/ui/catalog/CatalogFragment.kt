package home.samples.shoponline.ui.catalog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import home.samples.shoponline.databinding.FragmentCatalogBinding
import home.samples.shoponline.ui.ViewModelState
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
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
                                binding.catalogRecycler.isVisible = false
                            }
                            ViewModelState.Loaded -> {
                                binding.progress.isVisible = false
                                binding.catalogRecycler.isVisible = true
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}