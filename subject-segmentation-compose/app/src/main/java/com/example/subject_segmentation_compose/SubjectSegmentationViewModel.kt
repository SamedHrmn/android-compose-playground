package com.example.subject_segmentation_compose

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subject_segmentation_compose.util.ImageUtil
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenter
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.launch


class SubjectSegmentationViewModel : ViewModel() {

    private val _selectedBitmap = mutableStateOf<Bitmap?>(null)
    val selectedBitmap: State<Bitmap?> = _selectedBitmap
    private val _segmentationResult = mutableStateOf<Bitmap?>(null)
    val segmentationBitmap: State<Bitmap?> = _segmentationResult
    private var _subjectSegmenter: SubjectSegmenter

    init {
        val options = SubjectSegmenterOptions.Builder().enableForegroundBitmap().build()
        _subjectSegmenter = SubjectSegmentation.getClient(options)
    }

    fun setSelectedBitmapFromUri(uri: Uri?, context: Context) {
        uri?.let {
            viewModelScope.launch {
                ImageUtil.uriToBitmap(uri, context, viewModelScope, onSuccess = {
                    _selectedBitmap.value = it
                    _segmentationResult.value = null
                })
            }
        }
    }

    fun processImage() {
        selectedBitmap.value?.let {
            viewModelScope.launch {
                _subjectSegmenter.process(
                    InputImage.fromBitmap(it, 0)
                ).addOnSuccessListener { result ->
                    _segmentationResult.value = result.foregroundBitmap

                }.addOnFailureListener { e ->
                    Log.d("Subject Segmentation Fail", "processImage: ${e.localizedMessage}")
                }
                _selectedBitmap.value = null
            }
        }
    }
}