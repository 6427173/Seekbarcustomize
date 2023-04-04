package com.example.seekbarexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.seekbarexample.VerticalSlider.OnProgressChangeListener
import com.example.seekbarexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.verticalSlider.setOnSliderProgressChangeListener(object : OnProgressChangeListener {
            override fun onProgress(progress: Float) {
                binding.textView.setText(String.format("Progress: %.1f%%", progress * 100f))
                // mTextView.setText(String.format("Progress: %.1f%%", progress * 100f))
            }
        })
    }
}