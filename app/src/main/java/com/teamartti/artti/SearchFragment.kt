package com.teamartti.artti

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.teamartti.artti.databinding.SearchLayoutBinding

class SearchFragment : Fragment() {
    private var _binding:SearchLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter:ItemRecycler
    private lateinit var data:MutableList<ItemModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        setUpAdapter()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SearchLayoutBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recycler.layoutManager = StaggeredGridLayoutManager(requireContext(), StaggeredGridLayoutManager.VERTICAL)
        binding.recycler.adapter = adapter
        handleSearchClick()
        handleSearchFromKeyboard()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun handleSearchClick() {
        binding.goSearch.setOnClickListener {
            val searchText = binding.searchBar?.text.toString().trim()
            if (searchText.isNullOrEmpty() || searchText.isBlank()){
                Snackbar.make(binding.root, "Enter what you'll like to see", Snackbar.LENGTH_SHORT).show()
            }else{
                loadImages(searchText)
            }
        }
    }

    private fun handleSearchFromKeyboard(){
        binding.searchBar.setOnEditorActionListener { textView, actionId, keyEvent ->
            return@setOnEditorActionListener when (actionId){
                EditorInfo.IME_ACTION_SEARCH ->{
                    val searchText = binding.searchBar?.text.toString().trim()
                    if (searchText.isNullOrEmpty() || searchText.isBlank()){
                        Snackbar.make(binding.root, "Enter what you'll like to see", Snackbar.LENGTH_SHORT).show()
                    }else{
                        loadImages(searchText)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun loadImages(searchText: String) {
        TODO("Not yet implemented")
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        _binding == null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setUpAdapter(){
        data = mutableListOf()
        adapter = ItemRecycler(data,
        downloadCallback = {url ->
            downloadImage(url)
        },
            wallpaperCallback = {url ->
                useAsWallpaper(url)
            }
        )
    }

    private fun useAsWallpaper(url: String?) {
        TODO("Not yet implemented")
    }

    private fun downloadImage(url: String?) {
        TODO("Not yet implemented")
    }
}