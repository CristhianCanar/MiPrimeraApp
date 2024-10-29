package com.cristhian.miprimeraapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cristhian.miprimeraapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            binding.textView.text = "Texto modificado"

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.google.com.co")
            startActivity(intent)
        }


    }
}
