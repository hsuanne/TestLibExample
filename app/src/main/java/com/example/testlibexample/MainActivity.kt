package com.example.testlibexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.toasterlibrary.ToasterMessage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ToasterMessage.toastMyMessage(this, "Successfully import library!!!")
        ToasterMessage.logMyMessage("Successfully import library!!!")
    }
}