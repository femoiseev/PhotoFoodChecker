package com.abu.photofoodchecker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import org.jetbrains.anko.sdk25.coroutines.onClick
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import org.jetbrains.anko.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream


class MainActivity : Activity() {
    val TAG = "MainActivity"

    private val TAKE_PICTURE = 0
    private val SELECT_FILE = 1
    private val CROP_PICTURE = 2

    private val resultUrl = "result.txt"

    val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        relativeLayout {
            verticalLayout {
                button("Сделать фото") {
                    onClick {
                        if (checkPermissions()) {
                            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            val fileUri = getOutputMediaFileUri()
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                            if (takePictureIntent.resolveActivity(packageManager) != null) {
                                activity.startActivityForResult(takePictureIntent, TAKE_PICTURE)
                            }
                        }
                    }
                }
                button("Выбрать фото") {
                    onClick {
                        if (checkPermissions()) {
                            val pickPictureIntent = Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            if (pickPictureIntent.resolveActivity(packageManager) != null) {
                                activity.startActivityForResult(pickPictureIntent, SELECT_FILE)
                            }
                        }
                    }
                }
            }.lparams {
                width = wrapContent
                height = wrapContent
                centerInParent()
            }
        }
        checkPermissions()
    }

    fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.INTERNET), 0);
        }
        return (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null)
            return

        if (requestCode == TAKE_PICTURE || requestCode == SELECT_FILE) {
            val imageUri : Uri = when (requestCode) {
                TAKE_PICTURE ->  getOutputMediaFileUri()
                SELECT_FILE -> data.data
                else -> Uri.EMPTY
            }

            val intent = Intent("com.android.camera.action.CROP")
            intent.setDataAndType(imageUri, "image/*")
            intent.putExtra("crop", "true")
            intent.putExtra("aspectX", 1)
            intent.putExtra("aspectY", 1)
            intent.putExtra("outputX", 256)
            intent.putExtra("outputY", 256)
            intent.putExtra("noFaceDetection", true)
            intent.putExtra("return-data", true)
            startActivityForResult(intent, CROP_PICTURE)
        } else if (requestCode == CROP_PICTURE) {
            deleteFile(resultUrl)

            val extras = data.extras
            val picture = extras.getParcelable<Bitmap>("data")
            val imageFilePath = getOutputMediaFile()?.getPath()
            try {
                val out = FileOutputStream(imageFilePath)
                picture?.compress(Bitmap.CompressFormat.PNG, 100, out)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            val results = intentFor<AnalysisResultActivity>("IMAGE_PATH" to imageFilePath,
                    "RESULT_PATH" to resultUrl)
            startActivity(results)
        }
    }

    private fun getOutputMediaFile(): File? {

        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ABBYY Cloud OCR SDK Demo App")

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        return File(mediaStorageDir.path + File.separator + "image.jpg")
    }

    private fun getOutputMediaFileUri(): Uri {
        return Uri.fromFile(getOutputMediaFile())
    }
}
