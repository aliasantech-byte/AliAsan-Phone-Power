
package com.aliasan.phonepower

import android.app.Activity
import android.os.Bundle
import android.os.Build
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.io.File

class MainActivity : Activity() {

    private val blue = Color.rgb(25, 80, 160)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this)
        scrollView.setBackgroundColor(Color.rgb(245, 247, 250))

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 25, 20, 25)

        val title = TextView(this)
        title.text = "توان‌سنج گوشی علی آسان"
        title.textSize = 25f
        title.setTextColor(blue)
        title.gravity = Gravity.CENTER
        title.setPadding(0, 10, 0, 8)

        val subtitle = TextView(this)
        subtitle.text = "مشخصات سخت‌افزاری گوشی"
        subtitle.textSize = 16f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 0, 0, 25)

        layout.addView(title)
        layout.addView(subtitle)

        addInfo(layout, "برند گوشی", Build.MANUFACTURER)
        addInfo(layout, "مدل گوشی", Build.MODEL)
        addInfo(layout, "نسخه اندروید", Build.VERSION.RELEASE)
        addInfo(layout, "شماره API", Build.VERSION.SDK_INT.toString())
        addInfo(layout, "تعداد هسته پردازنده",
            Runtime.getRuntime().availableProcessors().toString())
        addInfo(layout, "نام پردازنده", getCpuName())
        addInfo(layout, "معماری پردازنده",
            System.getProperty("os.arch") ?: "نامشخص")
        addInfo(layout, "حافظه RAM کل", getTotalRam())

        val footer = TextView(this)
        footer.text = "سریع و آسان | علی آسان"
        footer.textSize = 14f
        footer.setTextColor(blue)
        footer.gravity = Gravity.CENTER
        footer.setPadding(0, 25, 0, 10)
        layout.addView(footer)

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun addInfo(
        layout: LinearLayout,
        label: String,
        value: String
    ) {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(16, 12, 16, 12)
        card.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = label
        title.textSize = 14f
        title.setTextColor(Color.GRAY)

        val result = TextView(this)
        result.text = value
        result.textSize = 18f
        result.setTextColor(Color.rgb(35, 45, 60))
        result.setPadding(0, 5, 0, 0)

        card.addView(title)
        card.addView(result)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = 12

        layout.addView(card, params)
    }

    private fun getCpuName(): String {
        return try {
            val lines = File("/proc/cpuinfo").readLines()

            val cpuLine = lines.firstOrNull {
                it.startsWith("model name") ||
                it.startsWith("Hardware") ||
                it.startsWith("Processor")
            }

            cpuLine?.substringAfter(":")?.trim()
                ?: Build.HARDWARE
        } catch (e: Exception) {
            Build.HARDWARE
        }
    }

    private fun getTotalRam(): String {
        return try {
            val line = File("/proc/meminfo").readLines()
                .firstOrNull { it.startsWith("MemTotal:") }

            val kb = line?.filter { it.isDigit() }?.toLongOrNull()
                ?: return "نامشخص"

            val gb = kb / 1024.0 / 1024.0

            String.format("%.2f GB", gb)
        } catch (e: Exception) {
            "نامشخص"
        }
    }
}
