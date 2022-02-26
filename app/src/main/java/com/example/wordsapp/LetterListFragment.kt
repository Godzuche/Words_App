package com.example.wordsapp

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsapp.data.SettingsDataStore
import com.example.wordsapp.databinding.FragmentLetterListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LetterListFragment : androidx.fragment.app.Fragment() {
    //the underscore means it's supposed to be private to  this class
    private var _binding: FragmentLetterListBinding? = null

    //limiting access to the nullable above
    //the binding variable is get-only... you can get its value but once assigned, you can't assign it to something else
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private var isLinearLayoutManager = true

    private lateinit var SettingsDataStore: SettingsDataStore

    //instantiation of the fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //enable options menu
        setHasOptionsMenu(true)
    }

    //inflate the layout, set value of _binding and return the root view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLetterListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.recyclerView
        // Initialize SettingsDataStore
        SettingsDataStore = SettingsDataStore(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                SettingsDataStore.preferencesFlow.stateIn(viewLifecycleOwner.lifecycleScope)
                    .collectLatest { value ->
                        isLinearLayoutManager = value
                        // Set the layout
                        chooseLayout()
                        // Redraw the menu
                        activity?.invalidateOptionsMenu()
                    }
            }
        }
    }

    private fun chooseLayout() {
        when (isLinearLayoutManager) {
            true -> {
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = LetterAdapter()
            }
            false -> {
                recyclerView.layoutManager = GridLayoutManager(context, 4)
                recyclerView.adapter = LetterAdapter()
            }
        }
    }

    private fun setIcon(menuItem: MenuItem?) {
        if (menuItem == null) {
            //break out of the function
            return
        }

        menuItem.icon = if (isLinearLayoutManager)
            ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_linear_layout)
        else
            ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_grid_layout)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_layout -> {
                //alternate the isLinearLayoutManager
                isLinearLayoutManager = !isLinearLayoutManager
                viewLifecycleOwner.lifecycleScope.launch {
                    SettingsDataStore.saveLayoutToPreferencesStore(isLinearLayoutManager,
                        requireContext())
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // Called right before the menu is visible
    override fun onPrepareOptionsMenu(menu: Menu) {
        //reference to the menuItem
        val layoutButton = menu.findItem(R.id.action_switch_layout)
        //changing the options menu icon
        setIcon(layoutButton)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //reset _binding to null as the view no longer exists
        _binding = null
    }
}