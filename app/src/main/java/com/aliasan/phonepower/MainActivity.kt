package com.aliasan.phonepower

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "توان‌سنج گوشی علی آسان"
        title.textSize = 25f
        title.setTextColor(Color.rgb(25, 80, 160))
        title.gravity = Gravity.CENTER

        val subtitle = TextView(this)
        subtitle.text = "سریع و آسان"
        subtitle.textSize = 18f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER

        layout.addView(title)
        layout.addView(subtitle)

        setContentView(layout)
    }
}
