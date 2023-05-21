package com.em.findaword.ui

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.em.findaword.R
import com.em.findaword.core.Resource
import com.em.findaword.databinding.FragmentGameBinding
import com.em.findaword.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class GameFragment : Fragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_game, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchAllWords(requireContext()).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Loading -> {
                    progressBarVisible(true)
                }
                is Resource.Success -> {
                    progressBarVisible(false)
                    viewModel.words = result.data
                    updateView()
                }
                is Resource.Failure -> {
                    progressBarVisible(false)
                    Toast.makeText(
                        requireContext(), "Error: ${result.exception.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        buttonActions()
        viewModel.setupVolume(binding.volumeButton)
    }

    private fun progressBarVisible(visible: Boolean) {
        if (visible) {
            binding.progressBar.visibility = View.VISIBLE
            binding.imageView.visibility = View.GONE
            binding.scoreText.visibility = View.GONE
            binding.content.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.imageView.visibility = View.VISIBLE
            binding.scoreText.visibility = View.VISIBLE
            binding.content.visibility = View.VISIBLE
        }
    }

    private fun buttonsClickable(value: Boolean) {
        binding.checkButton.isClickable = value
        binding.giveUpButton.isClickable = value
    }

    private fun buttonActions() {
        binding.checkButton.setOnClickListener {
            if (binding.wordEditText.text.isNullOrEmpty() || binding.wordEditText.text.isBlank())
                return@setOnClickListener

            val word = binding.wordEditText.text.toString().lowercase(Locale.ROOT)
            if ((word.contains(viewModel.currentSubString, ignoreCase = true)
                        || word.startsWith(viewModel.currentSubString, ignoreCase = true)
                        || word.endsWith(viewModel.currentSubString, ignoreCase = true))
                && !word.equals(viewModel.currentSubString, ignoreCase = true)
                && viewModel.checkWordExistence(word)
            ) {
                viewModel.success.start()
                increaseScore()
                updateView()
            } else {
                hideKeyboard(requireView())
                viewModel.failure.start()
                gameOver()
            }
        }

        binding.volumeButton.setOnClickListener {
            viewModel.tap.start()
            viewModel.manageVolume(binding.volumeButton)
        }

        binding.giveUpButton.setOnClickListener {
            viewModel.tap.start()
            binding.giveUpConfirmation.visibility = View.VISIBLE
            hideKeyboard(requireView())
            buttonsClickable(false)
        }

        binding.replayButton.setOnClickListener {
            viewModel.tap.start()
            replayGame()
        }

        binding.mainMenuButton.setOnClickListener {
            viewModel.tap.start()
            findNavController().navigateUp()
        }

        //Confirmation
        binding.yesButton.setOnClickListener {
            viewModel.failure.start()
            binding.giveUpConfirmation.visibility = View.GONE
            gameOver()
        }

        binding.cancelButton.setOnClickListener {
            viewModel.tap.start()
            binding.giveUpConfirmation.visibility = View.GONE
            buttonsClickable(true)
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun gameOver() {
        buttonsClickable(false)
        binding.gameOverPanel.visibility = View.VISIBLE
        binding.finalScoreText.text = binding.scoreText.text
        binding.correctAnswerText.text = viewModel.currentWord

        val prefs = requireActivity().getSharedPreferences(
            resources.getString(R.string.prefs_file), MODE_PRIVATE
        )

        val highScore = prefs.getString("highScore", null)
        if (highScore != null) {
            val currentScore = binding.scoreText.text.toString()
            if (highScore.toInt() < currentScore.toInt()) {
                binding.newMaxScoreText.visibility = View.VISIBLE
                val sPrefs = requireActivity().getSharedPreferences(
                    resources.getString(R.string.prefs_file), MODE_PRIVATE
                ).edit()
                sPrefs.putString("highScore", currentScore)
                sPrefs.apply()
            } else {
                binding.newMaxScoreText.visibility = View.GONE
            }
        }
    }

    private fun replayGame() {
        binding.gameOverPanel.visibility = View.GONE
        buttonsClickable(true)
        binding.finalScoreText.text = "0"
        binding.scoreText.text = "0"
        updateView()
    }

    private fun increaseScore() {
        val score = binding.scoreText.text.toString().toInt()
        binding.scoreText.text = score.plus(1).toString()
    }

    private fun updateView() {
        val sub = viewModel.selectRandomSubStr()
        binding.wordEditText.text.clear()
        binding.subString.text = sub
    }
}