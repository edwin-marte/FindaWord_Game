package com.em.findaword.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.em.findaword.R
import com.em.findaword.databinding.FragmentMenuBinding
import com.em.findaword.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_menu, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setupMediaPlayers(requireContext())

        val prefs = requireActivity().getSharedPreferences(
            resources.getString(R.string.prefs_file), Context.MODE_PRIVATE
        )

        val highScore = prefs.getString("highScore", null)
        if (highScore == null) {
            val sPrefs = requireActivity().getSharedPreferences(
                resources.getString(R.string.prefs_file),
                Context.MODE_PRIVATE
            ).edit()
            sPrefs.putString("highScore", "0")
            sPrefs.apply()
        }

        if (highScore != null) {
            binding.scoreText.text = highScore
        }

        buttonActions()
        viewModel.setupVolume(binding.volumeButton)
    }

    private fun closeGame() {
        activity?.finish()
        exitProcess(0)
    }

    private fun buttonActions() {
        binding.playButton.setOnClickListener {
            viewModel.tap.start()
            findNavController().navigate(R.id.action_menuFragment_to_gameFragment)
        }

        binding.volumeButton.setOnClickListener {
            viewModel.tap.start()
            viewModel.manageVolume(binding.volumeButton)
        }

        binding.exitButton.setOnClickListener {
            viewModel.tap.start()
            closeGame()
        }
    }
}