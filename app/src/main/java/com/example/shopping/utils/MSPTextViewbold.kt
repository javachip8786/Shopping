package com.example.shopping.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView

class MSPTextViewbold(context: Context, attributeset : AttributeSet) : AppCompatTextView(context,attributeset){
    init{
        applyFont()
    }
    private fun applyFont(){
        val typeface: Typeface = Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
        setTypeface(typeface)
    }
}