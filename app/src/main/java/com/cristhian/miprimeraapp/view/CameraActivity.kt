package com.cristhian.miprimeraapp.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.cristhian.miprimeraapp.Constants
import com.cristhian.miprimeraapp.R
import com.cristhian.miprimeraapp.databinding.ActivityCameraBinding
import java.io.File

class CameraActivity : AppCompatActivity() {
    lateinit var binding: ActivityCameraBinding

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var camera: Camera? = null

    private lateinit var outputDirectory: File
    private lateinit var timer: CountDownTimer

    var photoFile:File ?= null

    var savedUri: Uri ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()

        if (allPermissionsGranted()){
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, Constants.REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS
            )
        }

        outputDirectory = getOutputDirectory()

        binding.ivBtnCamera.setOnClickListener {
            timerTakePhoto()
        }

        binding.ivBtnSave.setOnClickListener {
            val intent = Intent(this, ImagenCapturadaActivity::class.java)
            intent.putExtra("uri", savedUri.toString())
            startActivity(intent)
        }
    }

    private fun checkPermissions() {
        if (
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            )
            != PackageManager.PERMISSION_GRANTED
            ||
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
            ||
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ), 0
            )
            startCamera()
        }
    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable{
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder()
                .setTargetResolution(Size(1280, 720))
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            try {
                cameraProvider.unbindAll()
                imageCapture= ImageCapture.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .build()

                camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
                preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)

            } catch (e: Exception) {
                Log.e(Constants.TAG, "Caso de uso falla", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun timerTakePhoto(){
        timer = object : CountDownTimer(6_000, 1_000) {
            override fun onTick(remaining: Long) {
                binding.ivBtnCamera.isVisible = false
                binding.tvTimer.isVisible = true
                binding.tvTimer.text = (remaining/1000).toString()
                if (remaining < 1_000)
                    binding.tvTimer.text = "\uD83D\uDE09"
            }

            override fun onFinish() {
                takePhoto()
                binding.tvTimer.isVisible = false
                binding.ivBtnSave.isVisible = true
            }
        }
        timer.start()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { mFile ->
            File(mFile, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun takePhoto() {
        val randomNumber = (100..999).shuffled().last()
        val imageCapture = imageCapture?: null
        photoFile = File(outputDirectory, "camara_personalizada_$randomNumber.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile!!).build()
        imageCapture?.takePicture(
            outputOptions,ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback{
                override fun onError(exception: ImageCaptureException) {
                    Log.d(Constants.TAG, "Fallo al guardar", exception)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)
                    showToastDialog(binding.root.context,"Se ha guardado en $savedUri")
                    Log.d(Constants.TAG, "Guardado en la uri -> $savedUri")

                }
            }
        )
    }

    fun showToastDialog(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}