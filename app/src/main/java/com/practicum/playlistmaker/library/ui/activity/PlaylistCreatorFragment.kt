package com.practicum.playlistmaker.library.ui.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistCreatorViewModel
import com.practicum.playlistmaker.root.ui.viewmodel.SharedViewModel
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.practicum.playlistmaker.util.DrawingTools
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.qualifier
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

class PlaylistCreatorFragment  : Fragment() {

    companion object {
        private const val roundRadiusDp: Float = 8f

        // private const val NUMBER = "number"

        fun newInstance(number: Int) = PlaylistCreatorFragment().apply {
            arguments = Bundle().apply {
                // putInt(NUMBER, number)
            }
        }
    }

    private val sharedViewModel: SharedViewModel by activityViewModel()
    private lateinit var binding: FragmentPlaylistCreatorBinding
    private val viewModel: PlaylistCreatorViewModel by viewModel {
        parametersOf(SavedStateHandle())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistCreatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настраиваем отступы
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Восстанавливаем имя
        if (viewModel.name.isNotEmpty()) {
            binding.inputName.editText?.setText(viewModel.name)
        }

        // Восстанавливаем описание
        if (viewModel.description.isNotEmpty()) {
            binding.inputDescription.editText?.setText(viewModel.description)
        }

        // Восстанавливаем изображение
        viewModel.uri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .transform(CenterCrop(), RoundedCorners(DrawingTools.dpToPx(roundRadiusDp, requireContext())))
                .into(binding.coverImageView)
        }

        // Доступность кнопки "Создать" и создание плейлиста
        viewModel.observeIsSaveButtonEnabled().observe(viewLifecycleOwner) {
            binding.btnSave.isEnabled = it
        }

        binding.btnSave.setOnClickListener {
            Log.d("NewPlaylist", "Вызов viewModel.createNewPlaylist()")
            viewModel.createNewPlaylist(sharedViewModel)
        }

        viewModel.observeIsSavingCompleted().observe(viewLifecycleOwner){
            if (it) {
                Log.d("NewPlaylist", "Закрытие фрагмента")
                findNavController().navigateUp()
            }
        }

        // Ввод текста
        val nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.inputName.hasFocus()) {
                    viewModel.handleNameInput(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        binding.inputName.editText?.addTextChangedListener(nameTextWatcher)

        val descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.inputDescription.hasFocus()) {
                    viewModel.handleDescriptionInput(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        binding.inputDescription.editText?.addTextChangedListener(descriptionTextWatcher)


        // Выбор картинки
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Glide.with(this)
                        .load(uri)
                        .transform(CenterCrop(), RoundedCorners(DrawingTools.dpToPx(roundRadiusDp, requireContext())))
                        .into(binding.coverImageView)
                }
                viewModel.handleImageURI(uri)
            }

        binding.coverImageView.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        // Выход с экрана создания плейлиста
        // Создаём callback для системной кнопки «Назад»
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBack()
            }
        }
        // Привязываем callback к жизненному циклу фрагмента
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,   // автоматически удалится при уничтожении View
            backCallback
        )
        // Назначаем обработчик для UI-кнопки «Назад»
        binding.btnGoBack.setOnClickListener {
            handleBack()
        }

    }


    // Общая логика при нажатии «Назад».
    private fun handleBack() {
        if (!viewModel.formIsEmpty()) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }
    // Подтверждающий диалог
    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.do_you_want_to_finish_creating_the_playlist))
            .setMessage(getString(R.string.all_unsaved_data_will_be_lost))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                // Ничего не делаем
            }
            .setPositiveButton(getString(R.string.finish)) { dialog, which ->
                findNavController().navigateUp()
            }
            .show()
    }

}