package com.example.foctrainer.exercise

import android.graphics.Bitmap
import android.graphics.Canvas

class CameraImageGraphic: GraphicOverlay.Graphic {

    private var bitmap: Bitmap? = null

    constructor(overlay: GraphicOverlay?, bitmap: Bitmap?):super(overlay) {
        this.bitmap = bitmap
    }

    override fun draw(canvas: Canvas?) {
        if (bitmap != null) {
            canvas?.drawBitmap(bitmap!!, getTransformationMatrix(), null)
        }
    }
}