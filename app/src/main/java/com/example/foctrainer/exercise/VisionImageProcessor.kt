package com.example.foctrainer.exercise

import android.graphics.Bitmap;
import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.common.MlKitException;
import java.nio.ByteBuffer;

interface VisionImageProcessor {

    /** An interface to process the images with different vision detectors and custom image models. */

        /** Processes a bitmap image. */
        fun processBitmap(bitmap:Bitmap , graphicOverlay:GraphicOverlay )

        /** Processes ImageProxy image data, e.g. used for CameraX live preview case. */
        @RequiresApi(VERSION_CODES.KITKAT)
        @Throws(MlKitException::class) fun processImageProxy( image:ImageProxy, graphicOverlay:GraphicOverlay )

        /** Stops the underlying machine learning model and release resources. */
        fun stop();
    }