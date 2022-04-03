package com.example.shopping.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shopping.R
import com.example.shopping.utils.constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(constants.MYSHOPPAL_PREF, Context.MODE_PRIVATE)

        val username = sharedPreferences.getString(constants.LOGGED_IN_USERNAME, "")!!
        tv_main.text = "hello $username"

    }
}