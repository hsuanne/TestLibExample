package com.example.testlibexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.btlibrary.Toaster

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toaster.showToast(this, "Successfully import btlibrary!")
    }
}