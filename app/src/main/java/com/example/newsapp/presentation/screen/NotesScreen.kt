package com.example.newsapp.presentation.screen
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.databinding.ScreenNotesBinding
import com.example.newsapp.presentation.adapters.SectionAdapter
import com.example.newsapp.presentation.viewModels.WikipediaViewModel
import com.example.newsapp.presentation.viewModels.impl.WikipediaViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesScreen : Fragment() {

    private var _binding: ScreenNotesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WikipediaViewModelImpl by viewModels()
    private lateinit var adapter: SectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SectionAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.searchButton.setOnClickListener {
            val query = binding.inputEditText.text.toString()
            if (query.isNotEmpty()) {
                viewModel.fetchSections(query)
            }
        }

        viewModel.sections.observe(viewLifecycleOwner, Observer { sections ->
            adapter.submitList(sections)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
