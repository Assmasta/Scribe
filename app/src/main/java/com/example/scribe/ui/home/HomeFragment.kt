package com.example.scribe.ui.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scribe.PhraseAdapter
import com.example.scribe.databinding.FragmentHomeBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.scribe.MainActivity
import com.example.scribe.customToast
import com.example.scribe.readCSV

class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var gDataSet: List<String>
    private val sharedViewModel by lazy {
        (activity as MainActivity).gSharedViewModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val applicationContext = requireContext()

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // home fragment text
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        // initialize recyclerview and adapter
        val phraseAdapter = PhraseAdapter(sharedViewModel.gData, sharedViewModel)
//        val phraseAdapter = PhraseAdapter(sharedViewModel.gDataSet) // TODO new livedata
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = phraseAdapter

        // observe ViewModel data and update recyclerView
        sharedViewModel.gData.observe(viewLifecycleOwner) {
            // Update RecyclerView data when LiveData changes
            phraseAdapter.notifyDataSetChanged() // TODO replace with more efficient one later
            recyclerView.smoothScrollToPosition(sharedViewModel.gDataSize())
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}