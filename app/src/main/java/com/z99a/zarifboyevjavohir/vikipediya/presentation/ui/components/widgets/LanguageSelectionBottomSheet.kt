package com.z99a.zarifboyevjavohir.vikipediya.presentation.ui.components.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.z99a.zarifboyevjavohir.R
import com.z99a.zarifboyevjavohir.databinding.BottomSheetLanguageSelectionBinding
import com.z99a.zarifboyevjavohir.databinding.ItemLanguageBinding
import com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels.GlobalViewModel

class LanguageSelectionBottomSheet : BottomSheetDialogFragment() {

    private val globalViewModel: GlobalViewModel by activityViewModels()
    private lateinit var binding: BottomSheetLanguageSelectionBinding
    private lateinit var languageAdapter: LanguageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetLanguageSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        languageAdapter = LanguageAdapter(
            onLanguageSelected = { language ->
                globalViewModel.changeLanguage(language.code)
                dismiss()
            },
            getCurrentLanguage = { globalViewModel.getCurrentLanguage() ?: "uz" }
        )

        binding.languageList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = languageAdapter
        }

        val languages = listOf(
            Language("en", "English", R.drawable.united_kingdom_flag_icon),
            Language("uz", "O'zbek tili", R.drawable.uzbekistan_flag_icon),
            Language("ru", "Русский", R.drawable.russia_flag_icon)
        )

        languageAdapter.submitList(languages)
    }
}

data class Language(val code: String, val name: String, val flagResId: Int)

class LanguageAdapter(
    private val onLanguageSelected: (Language) -> Unit,
    private val getCurrentLanguage: () -> String
) : ListAdapter<Language, LanguageAdapter.LanguageViewHolder>(LanguageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LanguageViewHolder(private val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(language: Language) {
            binding.flagImage.setImageResource(language.flagResId)
            binding.languageName.text = language.name
            binding.imageView.visibility = if (language.code == getCurrentLanguage()) View.VISIBLE else View.INVISIBLE
            binding.root.setOnClickListener { onLanguageSelected(language) }
        }
    }
}

class LanguageDiffCallback : DiffUtil.ItemCallback<Language>() {
    override fun areItemsTheSame(oldItem: Language, newItem: Language): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean {
        return oldItem == newItem
    }
}