package home.samples.shoponline.ui.favourites

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import home.samples.shoponline.R
import home.samples.shoponline.ui.registration.RegistrationVMState
import home.samples.shoponline.ui.registration.RegistrationViewModel
import home.samples.shoponline.ui.registration.RegistrationViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FavouritesFragment"

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    @Inject
    lateinit var registrationViewModelFactory: RegistrationViewModelFactory
    private val viewModel: RegistrationViewModel by viewModels { registrationViewModelFactory }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(home.samples.shoponline.ui.registration.TAG, "Функция onViewCreated() запущена")

        binding.emailEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && viewModel.emailState == null) {
                viewModel.email = binding.emailEditText.text.toString()
                viewModel.handleEnteredEmail()
            }
        }

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.d(
                    home.samples.shoponline.ui.registration.TAG,
                    "emailEditText - afterTextChanged(s) сработала. s = $s"
                )
                if (viewModel.emailState != null) {
                    viewModel.email = s.toString()
                    viewModel.handleEnteredEmail()
                }
            }
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.d(
                    home.samples.shoponline.ui.registration.TAG,
                    "passwordEditText - afterTextChanged(s) сработала. s = $s"
                )
                if (viewModel.passwordState != null) {
                    viewModel.password = s.toString()
                    viewModel.handleEnteredPassword()
                }
            }
        })

        binding.loginButton.setOnClickListener {
            viewModel.password = binding.passwordEditText.text.toString()
            viewModel.handleEnteredPassword()
            binding.emailEditText.clearFocus()
            binding.passwordEditText.clearFocus()
            viewModel.login()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginResult.collect { result ->
                    // result = true - это ошибка входа
                    if (result) {
//                        Toast.makeText(
//                            requireContext(),
//                            getText(R.string.registration_error),
//                            Toast.LENGTH_LONG
//                        ).show()
                    } else {
                        findNavController().navigate(
                            R.id.action_LoginFragment_to_CafeFragment
                        )
                    }
                }
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
                            is RegistrationVMState.WorkingState -> {
                                binding.loginButton.isEnabled = true
                                binding.emailLayout.boxBackgroundColor
                                emailFieldRefresh(state.emailState ?: true)
                                passwordFieldRefresh(state.passwordState ?: true)
                            }

                            RegistrationVMState.LoginProcess -> {
                                binding.loginButton.isEnabled = false
                            }
                        }
                    }
            }
        }
    }

    private fun emailFieldRefresh(state: Boolean) {
        if (state) binding.emailLayout.boxBackgroundColor =
            requireContext().getColor(R.color.transparent)
        else binding.emailLayout.boxBackgroundColor =
            requireContext().getColor(R.color.error_background)
    }

    private fun passwordFieldRefresh(state: Boolean) {
        if (state) binding.passwordLayout.boxBackgroundColor =
            requireContext().getColor(R.color.transparent)
        else binding.passwordLayout.boxBackgroundColor =
            requireContext().getColor(R.color.error_background)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}