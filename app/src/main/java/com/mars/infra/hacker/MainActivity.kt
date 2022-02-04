package com.mars.infra.hacker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.mars.infra.lib.TestLibActivity

class MainActivity : AppCompatActivity() {

    lateinit var mBtnStartTestLib: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnStartTestLib = findViewById(R.id.btn_start_testLib)

        mBtnStartTestLib.setOnClickListener {
            val intent = Intent(this, TestLibActivity::class.java)
            startActivity(intent)
        }
    }
}