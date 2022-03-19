package com.example.foctrainer.exercise

import android.app.Application
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException

class CameraXViewModel
/**
 * Create an instance which interacts with the camera service via the given application context.
 */(@NonNull application: Application) : AndroidViewModel(application) {

    private val TAG = "CameraXViewModel"
    private var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>? = null

    fun getProcessCameraProvider(): LiveData<ProcessCameraProvider>? {
        Log.d(TAG,"listening on cameras..")
        if (cameraProviderLiveData == null) {
            Log.d(TAG,"provider live data")
            cameraProviderLiveData = MutableLiveData()
            val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(getApplication())
            cameraProviderFuture.addListener(
                {
                    try {
                        cameraProviderLiveData!!.setValue(cameraProviderFuture.get())
                    } catch (e: ExecutionException) {
                        // Handle any errors (including cancellation) here.
                        Log.e(TAG, "Unhandled exception", e)
                    } catch (e: InterruptedException) {
                        Log.e(TAG, "Unhandled exception", e)
                    }
                },
                ContextCompat.getMainExecutor(getApplication())
            )
        }
        Log.d(TAG,"provider live data is null")
        return cameraProviderLiveData
    }
}