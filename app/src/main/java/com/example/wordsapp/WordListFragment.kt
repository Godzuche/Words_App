package com.example.wordsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsapp.databinding.FragmentWordListBinding

class WordListFragment : Fragment() {
    private var _binding: FragmentWordListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentWordListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val letterId = activity?.intent?.extras?.getString(LETTER).toString()
        recyclerView.adapter = WordAdapter(letterId, requireContext())

        recyclerView.addItemDecoration(DividerItemDecoration(context,
            DividerItemDecoration.VERTICAL))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //using companion object to organize constants
    companion object {
        const val LETTER = "letter"
        const val SEARCH_PREFIX = "https://www.google.com/search?q="
    }
}