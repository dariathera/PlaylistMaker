package com.practicum.playlistmaker.library.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistFormViewModel
import com.practicum.playlistmaker.root.ui.viewmodel.SharedViewModel
import com.practicum.playlistmaker.util.DrawingTools
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFormFragment  : Fragment() {

    companion object {
        private const val roundRadiusDp: Float = 8f

        private const val ARGS_PLAYLIST_ID = "playlist_id_key"
        const val MODE_EDIT = "edit"
        const val MODE_CREATE = "create"
        const val NO_PLAYLIST_ID = -1L

        fun createArgs(playlistId: Long = NO_PLAYLIST_ID): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)

    }

    private val sharedViewModel: SharedViewModel by activityViewModel()
    private lateinit var binding: FragmentPlaylistCreatorBinding
    private val viewModel: PlaylistFormViewModel by viewModel {
        parametersOf(
            SavedStateHandle(),
            requireArguments().getLong(ARGS_PLAYLIST_ID)
        )
    }
    private val playlistId: Long by lazy {
        requireArguments().getLong(ARGS_PLAYLIST_ID, NO_PLAYLIST_ID)
    }
    private val mode: String by lazy {
        if (playlistId == NO_PLAYLIST_ID) {
            MODE_CREATE
        } else {
            MODE_EDIT
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistCreatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (mode) {
            MODE_CREATE -> {
                binding.title.text = getString(R.string.new_playlist)
                binding.btnSave.text = getString(R.string.create)
            }
            MODE_EDIT -> {
                binding.title.text = getString(R.string.edit)
                binding.btnSave.text = getString(R.string.save)
            }
        }

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
            when (mode) {
                MODE_CREATE -> {
                    viewModel.createNewPlaylist(sharedViewModel)
                }
                MODE_EDIT -> {
                    viewModel.updatePlaylist(sharedViewModel)
                }
            }
        }

        viewModel.observeIsSavingCompleted().observe(viewLifecycleOwner){
            if (it) {
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

        // Загрузка данных плейлиста в режиме MODE_EDIT
        viewModel.observeLoadedData().observe(viewLifecycleOwner) {
            binding.apply {
                Glide.with(coverImageView)
                    .load(it.coverUri)
                    .placeholder(R.drawable.ic_artwork_placeholder_45)
                    .transform(
                        CenterCrop()
                    ).into(coverImageView)
                inputName.editText?.setText(it.name)
                inputDescription.editText?.setText(it.description)
            }
        }
    }

    // Общая логика при нажатии «Назад».
    private fun handleBack() {
        if (viewModel.formIsChanged()) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }
    // Подтверждающий диалог
    private fun showExitConfirmationDialog() {
        var title = ""
        when (mode) {
            MODE_CREATE ->
                title = getString(R.string.do_you_want_to_finish_creating_the_playlist)
            MODE_EDIT ->
                title = getString(R.string.do_you_want_to_finish_editing_the_playlist)
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
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