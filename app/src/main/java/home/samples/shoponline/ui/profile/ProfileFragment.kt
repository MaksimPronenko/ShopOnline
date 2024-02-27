package home.samples.shoponline.ui.profile

import android.os.Bundle
import android.view.Gravity
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
import home.samples.shoponline.databinding.FragmentProfileBinding
import home.samples.shoponline.ui.ViewModelState
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModelFactory
    private val viewModel: ProfileViewModel by viewModels { profileViewModelFactory }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = false

        viewModel.loadProfileData()

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favouritesButton.setOnClickListener {
            findNavController().navigate(R.id.action_ProfileFragment_to_FavouritesFragment)
        }

        binding.exitButton.setOnClickListener {
            viewModel.clearDataOnExit()
            findNavController().navigate(R.id.action_ProfileFragment_to_RegistrationFragment)
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
                            }
                            ViewModelState.Loaded -> {
                                binding.progress.isVisible = false
                                binding.userButton.text = viewModel.nameAndSurname
                                binding.phoneNumber.text = viewModel.phoneNumber
                                if (viewModel.favouriteProductCount > 0) {
                                    binding.productQuantity.text = getProductCountText(viewModel.favouriteProductCount)
                                    binding.productQuantity.isVisible = true
                                    binding.favouritesButton.gravity = Gravity.START
                                } else {
                                    binding.productQuantity.isVisible = false
                                    binding.favouritesButton.gravity = Gravity.CENTER_VERTICAL
                                }
                            }
                            ViewModelState.Error -> {
                                binding.progress.isVisible = false
                            }
                        }
                    }
            }
        }
    }

    private fun getProductCountText(productCount: Int) = "$productCount ${
        when {
            productCount % 10 == 1 && productCount % 100 != 11 -> requireContext().getString(R.string.product_1)
            productCount % 10 in 2..4 && productCount % 100 !in 12..14 -> requireContext().getString(
                R.string.product_2
            )
            else -> requireContext().getString(R.string.product_3)
        }
    }"

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}