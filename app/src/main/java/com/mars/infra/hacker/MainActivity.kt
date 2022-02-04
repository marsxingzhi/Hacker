package com.mars.infra.hacker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mars.infra.lib.TestLibActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mBtnStartTestLib: Button
    private lateinit var mBtnStartThread: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnStartTestLib = findViewById(R.id.btn_start_testLib)
        mBtnStartThread = findViewById(R.id.btn_test_thread)

        mBtnStartTestLib.setOnClickListener {
            val intent = Intent(this, TestLibActivity::class.java)
            startActivity(intent)
        }

        mBtnStartThread.setOnClickListener {
            query()
        }
    }

    private fun query() {
        val thread = Thread {
            Log.e("mars","query---线程执行任务, 当前线程 = ${Thread.currentThread()}")
        }
        Log.e("mars","thread = $thread")
        thread.start()
    }
}