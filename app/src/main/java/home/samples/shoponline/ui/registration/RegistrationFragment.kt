package home.samples.shoponline.ui.registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import home.samples.shoponline.R
import home.samples.shoponline.databinding.FragmentRegistrationBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RegistrationFragment"

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    @Inject
    lateinit var registrationViewModelFactory: RegistrationViewModelFactory
    private val viewModel: RegistrationViewModel by viewModels { registrationViewModelFactory }

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = true

        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "Функция onViewCreated() запущена")

        binding.firstNameLayout.setEndIconOnClickListener {
            binding.firstNameEditText.setText("")
        }

        binding.firstNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "firstNameEditText - afterTextChanged(s) сработала. s = $s")
                viewModel.handleEnteredFirstName(s.toString())
                binding.firstNameLayout.isEndIconVisible = !s.isNullOrEmpty()
            }
        })

        binding.surnameLayout.setEndIconOnClickListener {
            binding.surnameEditText.setText("")
        }

        binding.surnameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "surnameEditText - afterTextChanged(s) сработала. s = $s")
                viewModel.handleEnteredSurname(s.toString())
                binding.surnameLayout.isEndIconVisible = !s.isNullOrEmpty()
            }
        })

        binding.phoneNumberLayout.setEndIconOnClickListener {
            binding.phoneNumberEditText.setText("")
        }

        binding.phoneNumberEditText.addTextChangedListener(object : TextWatcher {
            private var mSelfChange = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null || mSelfChange) {
                    return
                }

                // Маска без префикса "+7 ("
                var resultWithMask = requireContext().getString(R.string.phone_number_mask_short)
                // Индексы цифр в записи номера: 0, 1, 2, 5, 6, 7, 9, 10, 12, 13

                // Принятые цифры из поля без префикса "+7 ("
                viewModel.receivedDigits = s.replace(Regex("(\\D*)"), "")
                if (viewModel.receivedDigits.length > 10)
                    viewModel.receivedDigits = viewModel.receivedDigits.substring(0, 10)
                Log.d(TAG, "receivedDigits = ${viewModel.receivedDigits}")

                var cursorPosition = 0

                for (i in viewModel.receivedDigits.indices) {
                    when (i) {
                        in (0..2) -> {
                            resultWithMask =
                                resultWithMask.replaceRange(
                                    i,
                                    i + 1,
                                    viewModel.receivedDigits[i].toString()
                                )
                            cursorPosition = i + 1
                        }

                        in (3..5) -> {
                            resultWithMask = resultWithMask.replaceRange(
                                i + 2,
                                i + 3,
                                viewModel.receivedDigits[i].toString()
                            )
                            cursorPosition = i + 3
                        }

                        6, 7 -> {
                            resultWithMask = resultWithMask.replaceRange(
                                i + 3,
                                i + 4,
                                viewModel.receivedDigits[i].toString()
                            )
                            cursorPosition = i + 4
                        }

                        8, 9 -> {
                            resultWithMask = resultWithMask.replaceRange(
                                i + 4,
                                i + 5,
                                viewModel.receivedDigits[i].toString()
                            )
                            cursorPosition = i + 5
                        }
                    }
                }

                val resultStr = resultWithMask
                Log.d(TAG, "resultStr = $resultStr")

                mSelfChange = true
                binding.phoneNumberEditText.setText(resultStr)
                binding.phoneNumberEditText.setSelection(cursorPosition)
                mSelfChange = false
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "phoneNumberEditText - afterTextChanged(s) сработала. s = $s")
                viewModel.handleEnteredPhoneNumber()
                binding.phoneNumberLayout.isEndIconVisible = !s.isNullOrEmpty()
            }
        })

        binding.registerButton.setOnClickListener {
            viewModel.register()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registrationResult.collect { result ->
                    if (result) {
                        findNavController().navigate(
                            R.id.action_RegistrationFragment_to_MainFragment
//                            R.id.action_RegistrationFragment_to_CatalogFragment
                        )
                    } else {
                        findNavController().navigate(
                            R.id.action_RegistrationFragment_to_MainFragment
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
                                binding.registerButton.isEnabled =
                                    ((state.firstNameState ?: true) && (state.surnameState
                                        ?: true) && (state.phoneState ?: true))
                                firstnameFieldRefresh(state.firstNameState ?: true)
                                surnameFieldRefresh(state.surnameState ?: true)
                                phoneNumberFieldRefresh(state.phoneState ?: true)
                            }

                            RegistrationVMState.RegistrationProcess -> {
                                binding.registerButton.isEnabled = false
                            }
                        }
                    }
            }
        }
    }

    private fun firstnameFieldRefresh(state: Boolean) {
        if (state) binding.firstNameLayout.boxBackgroundColor =
            requireContext().getColor(R.color.grey_background)
        else binding.firstNameLayout.boxBackgroundColor =
            requireContext().getColor(R.color.error_background)
    }

    private fun surnameFieldRefresh(state: Boolean) {
        if (state) binding.surnameLayout.boxBackgroundColor =
            requireContext().getColor(R.color.grey_background)
        else binding.surnameLayout.boxBackgroundColor =
            requireContext().getColor(R.color.error_background)
    }

    private fun phoneNumberFieldRefresh(state: Boolean) {
        if (state) binding.phoneNumberLayout.boxBackgroundColor =
            requireContext().getColor(R.color.grey_background)
        else binding.phoneNumberLayout.boxBackgroundColor =
            requireContext().getColor(R.color.error_background)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}