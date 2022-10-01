package com.teamartti.artti

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {
    private lateinit var adapter: ItemRecycler
    private lateinit var data: MutableList<ItemModel>
    private lateinit var imageTitles: MutableList<String>
    private var mView: View? = null
    private var progressBar: CircularProgressIndicator? = null
    private val dateFormatter = SimpleDateFormat(
        "yyyy.MM.dd 'at' HH:mm:ss z",
        Locale.getDefault()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        initImageTitles()
        setUpAdapter()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.search_layout, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        progressBar = view.findViewById<CircularProgressIndicator>(R.id.progress_circular)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
        handleSearchClick()
        handleSearchFromKeyboard()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun handleSearchClick() {
        val goSearch = mView?.findViewById<ShapeableImageView>(R.id.go_search)!!
        val searchBar = mView?.findViewById<TextInputEditText>(R.id.search_bar)!!
        goSearch.setOnClickListener {
            val searchText = searchBar?.text.toString().trim()
            if (searchText.isNullOrEmpty() || searchText.isBlank()) {
                Snackbar.make(requireView(), "Enter what you'll like to see", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                loadImages(searchText)
            }
        }
    }

    private fun handleSearchFromKeyboard() {
        val searchBar = mView?.findViewById<TextInputEditText>(R.id.search_bar)!!
        searchBar.setOnEditorActionListener { textView, actionId, keyEvent ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val searchText = searchBar?.text.toString().trim()
                    if (searchText.isNullOrEmpty() || searchText.isBlank()) {
                        Snackbar.make(
                            requireView(),
                            "Enter what you'll like to see",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        loadImages(searchText)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun loadImages(searchText: String) {
        progressBar?.isGone = false
        val result = imageTitles.filter {
            it.contains(searchText, true)
        }
        if (result.isNotEmpty()) {
            var bitmaps: List<Bitmap>
            data.clear()
            lifecycleScope.launch {
                result.onEachIndexed  {index, it ->
                    val a = resources.assets.list(it)
                    bitmaps = a?.map { filename ->
                        BitmapFactory.decodeStream(resources.assets.open("${result[index]}/${filename}"))
                    }!!
                    val img= bitmaps.map { bitmap ->
                        ItemModel(bitmap)
                    }
                    data.addAll(img)
                }
                adapter.notifyDataSetChanged()
            }
            progressBar?.isGone = true
        } else {
            progressBar?.isGone = true
            Snackbar.make(requireView(), "No image found", Snackbar.LENGTH_SHORT).show()
        }
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

        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setUpAdapter() {
        data = mutableListOf()
        adapter = ItemRecycler(data,
            downloadCallback = { bitmap ->
                downloadImage(bitmap)
            },
            wallpaperCallback = { bitmap ->
                useAsWallpaper(bitmap)
            }
        )
    }

    private fun useAsWallpaper(url: Bitmap?) {
//      do nothing yet
    }

    private fun downloadImage(bitmap: Bitmap?) {
        lifecycleScope.launch(Dispatchers.IO) {
            val resolver = requireContext().applicationContext.contentResolver
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Find all image files on the primary external storage device.
                    val imageCollection =
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                    val newImageDetails = ContentValues().apply {
                        put(
                            MediaStore.MediaColumns.DISPLAY_NAME,
                            "IMG-${System.currentTimeMillis()}.jpg"
                        )
                        put(MediaStore.MediaColumns.DATE_ADDED, dateFormatter.format(Date()))
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            "${Environment.DIRECTORY_PICTURES}/Artti"
                        )
                    }
                    with(resolver) {
                        val imageUri = insert(imageCollection, newImageDetails)
                        resolver.openOutputStream(imageUri!!)?.use { stream ->
                            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            stream.close()
                        }
                    }
                } else {
                    val directory =
                        requireContext().getExternalFilesDir("${Environment.DIRECTORY_PICTURES}/Artti")
                    if (!directory?.exists()!!) directory.mkdir()
                    val newImageDetails = ContentValues().apply {
                        put(
                            MediaStore.Images.Media.DISPLAY_NAME,
                            "IMG-${System.currentTimeMillis()}.jpg"
                        )
                        put(MediaStore.Images.Media.DATE_ADDED, dateFormatter.format(Date()))
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                    }
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newImageDetails)
                    val file = File(directory, "IMG-${System.currentTimeMillis()}.jpg")
                    val fileOutputStream = FileOutputStream(file)
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                    fileOutputStream.close()
                }
                Snackbar.make(requireView(), "Image Saved", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Snackbar.make(requireView(), "Failed to save image", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    private fun initImageTitles() {
        imageTitles = mutableListOf()
        imageTitles.addAll(resources.getStringArray(R.array.images_name))
    }

}