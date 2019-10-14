package com.noble.activity.RAZA_3.etc_process
import android.annotation.SuppressLint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.Size
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup


// 카메라에서 가져온 영상을 보여주는 카메라 프리뷰 클래스
internal class CameraPreview(
        context: Context,
        private val mActivity: AppCompatActivity,
        private val mCameraID: Int,
        private val mSurfaceView: SurfaceView
) : ViewGroup(context), SurfaceHolder.Callback {

    private val TAG = "CameraPreview"
    private val mHolder: SurfaceHolder
    private var mCamera: Camera? = null
    private var mCameraInfo: Camera.CameraInfo? = null
    private var mDisplayOrientation: Int = 0
    private var mSupportedPreviewSizes: List<Size>? = null
    private var mPreviewSize: Size? = null
    private var isPreview = false


    var shutterCallback: Camera.ShutterCallback = Camera.ShutterCallback { }

    var rawCallback: Camera.PictureCallback = Camera.PictureCallback { data, camera -> }


    //참고 : http://stackoverflow.com/q/37135675
    var jpegCallback: Camera.PictureCallback = Camera.PictureCallback { data, camera ->
        //이미지의 너비와 높이 결정
        val w = camera.parameters.pictureSize.width
        val h = camera.parameters.pictureSize.height
        val orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation)


        //byte array를 bitmap으로 변환
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size, options)


        //이미지를 디바이스 방향으로 회전
        val matrix = Matrix()
        matrix.postRotate(orientation.toFloat())
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)

        //bitmap을 byte array로 변환
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val currentData = stream.toByteArray()

        //파일로 저장
        SaveImageTask().execute(currentData)
    }


    init {


        Log.d("@@@", "Preview")


        mSurfaceView.visibility = View.VISIBLE


        // SurfaceHolder.Callback를 등록하여 surface의 생성 및 해제 시점을 감지
        mHolder = mSurfaceView.holder
        mHolder.addCallback(this)

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = View.resolveSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height)
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed && childCount > 0) {
            val child = getChildAt(0)

            val width = r - l
            val height = b - t

            var previewWidth = width
            var previewHeight = height
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize!!.width
                previewHeight = mPreviewSize!!.height
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                val scaledChildWidth = previewWidth * height / previewHeight
                child.layout(
                        (width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height
                )
            } else {
                val scaledChildHeight = previewHeight * width / previewWidth
                child.layout(
                        0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2
                )
            }
        }
    }


    // Surface가 생성되었을 때 어디에 화면에 프리뷰를 출력할지 알려줘야 한다.
    override fun surfaceCreated(holder: SurfaceHolder) {

        // Open an instance of the camera
        try {
            mCamera = Camera.open(mCameraID) // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "Camera " + mCameraID + " is not available: " + e.message)
        }


        // retrieve camera's info.
        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(mCameraID, cameraInfo)

        mCameraInfo = cameraInfo
        mDisplayOrientation = mActivity.windowManager.defaultDisplay.rotation

        val orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation)
        mCamera!!.setDisplayOrientation(orientation)



        mSupportedPreviewSizes = mCamera!!.parameters.supportedPreviewSizes
        requestLayout()

        // get Camera parameters
        val params = mCamera!!.parameters

        val focusModes = params.supportedFocusModes
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            // set the focus mode
            params.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            // set Camera parameters
            mCamera!!.parameters = params
        }


        try {

            mCamera!!.setPreviewDisplay(holder)


            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            mCamera!!.startPreview()
            isPreview = true
            Log.d(TAG, "Camera preview started.")
        } catch (e: IOException) {
            Log.d(TAG, "Error setting camera preview: " + e.message)
        }

    }


    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Release the camera for other applications.
        if (mCamera != null) {
            if (isPreview)
                mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
            isPreview = false
        }

    }


    private fun getOptimalPreviewSize(sizes: List<Size>?, w: Int, h: Int): Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = w.toDouble() / h
        if (sizes == null) return null

        var optimalSize: Size? = null
        var minDiff = java.lang.Double.MAX_VALUE

// Try to find an size match aspect ratio and size
        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - h).toDouble()
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - h).toDouble()
                }
            }
        }
        return optimalSize
    }


    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {

        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.surface == null) {
            // preview surface does not exist
            Log.d(TAG, "Preview surface does not exist")
            return
        }


        // stop preview before making changes
        try {
            mCamera!!.stopPreview()
            Log.d(TAG, "Preview stopped.")
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }

        val orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation)
        mCamera!!.setDisplayOrientation(orientation)

        try {
            mCamera!!.setPreviewDisplay(mHolder)
            mCamera!!.startPreview()
            Log.d(TAG, "Camera preview started.")
        } catch (e: Exception) {
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }

    }


    fun takePicture() {

        mCamera!!.takePicture(shutterCallback, rawCallback, jpegCallback)
    }


    private inner class SaveImageTask : AsyncTask<ByteArray, Void, Void>() {

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg data: ByteArray): Void? {
            var outStream: FileOutputStream? = null


            try {

                val path = File(Environment.getExternalStorageDirectory().absolutePath + "/camtest")
                if (!path.exists()) {
                    path.mkdirs()
                }

                val fileName = String.format("%d.jpg", System.currentTimeMillis())
                val outputFile = File(path, fileName)

                outStream = FileOutputStream(outputFile)
                outStream.write(data[0])
                outStream.flush()
                outStream.close()

                Log.d(
                        TAG, "onPictureTaken - wrote bytes: " + data.size + " to "
                        + outputFile.absolutePath
                )


                mCamera!!.startPreview()


                // 갤러리에 반영
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = Uri.fromFile(outputFile)
                context.sendBroadcast(mediaScanIntent)



                try {
                    mCamera!!.setPreviewDisplay(mHolder)
                    mCamera!!.startPreview()
                    Log.d(TAG, "Camera preview started.")
                } catch (e: Exception) {
                    Log.d(TAG, "Error starting camera preview: " + e.message)
                }


            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

    }

    companion object {


        /**
         * 안드로이드 디바이스 방향에 맞는 카메라 프리뷰를 화면에 보여주기 위해 계산합니다.
         */
        fun calculatePreviewOrientation(info: Camera.CameraInfo?, rotation: Int): Int {
            var degrees = 0

            when (rotation) {
                Surface.ROTATION_0 -> degrees = 0
                Surface.ROTATION_90 -> degrees = 90
                Surface.ROTATION_180 -> degrees = 180
                Surface.ROTATION_270 -> degrees = 270
            }

            var result: Int
            if (info!!.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360
                result = (360 - result) % 360  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360
            }

            return result
        }
    }
}