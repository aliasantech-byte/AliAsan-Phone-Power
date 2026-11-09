
package com.aliasan.phonepower

import android.app.Activity
import android.os.Bundle
import android.os.Build
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Button
import java.io.File
import java.util.Locale

class MainActivity : Activity() {

    private val blue = Color.rgb(25, 80, 160)
    private lateinit var testButton: Button
    private lateinit var testResult: TextView

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
        subtitle.text = "مشخصات و تست قدرت گوشی"
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

        addInfo(
            layout,
            "تعداد هسته پردازنده",
            Runtime.getRuntime().availableProcessors().toString()
        )

        addInfo(layout, "نام پردازنده", getCpuName())
        addInfo(
            layout,
            "معماری پردازنده",
            System.getProperty("os.arch") ?: "نامشخص"
        )
        addInfo(layout, "حافظه RAM کل", getTotalRam())

        val sectionTitle = TextView(this)
        sectionTitle.text = "تست قدرت پردازنده CPU"
        sectionTitle.textSize = 21f
        sectionTitle.setTextColor(blue)
        sectionTitle.gravity = Gravity.CENTER
        sectionTitle.setPadding(0, 25, 0, 12)

        layout.addView(sectionTitle)

        testButton = Button(this)
        testButton.text = "شروع تست پردازنده"
        testButton.textSize = 17f
        testButton.isAllCaps = false

        layout.addView(testButton)

        testResult = TextView(this)
        testResult.text = "برای شروع، دکمه بالا را لمس کن."
        testResult.textSize = 17f
        testResult.setTextColor(Color.DKGRAY)
        testResult.gravity = Gravity.CENTER
        testResult.setPadding(10, 20, 10, 20)

        layout.addView(testResult)

        val footer = TextView(this)
        footer.text = "سریع و آسان | علی آسان"
        footer.textSize = 14f
        footer.setTextColor(blue)
        footer.gravity = Gravity.CENTER
        footer.setPadding(0, 25, 0, 10)

        layout.addView(footer)

        testButton.setOnClickListener {
            startCpuTest()
        }

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

    private fun startCpuTest() {
        testButton.isEnabled = false
        testButton.text = "در حال تست..."
        testResult.text = "لطفاً صبر کن؛ پردازنده در حال آزمایش است."

        Thread {
            val iterations = 2_000_000
            var value = 0L

            val startTime = System.nanoTime()

            for (i in 1..iterations) {
                value = value xor (i.toLong() * 31L)
                value = (value shl 7) or (value ushr 57)
                value += i.toLong()
            }

            val elapsedTime =
                (System.nanoTime() - startTime) / 1_000_000.0

            val seconds = elapsedTime / 1000.0
            val speed = iterations / seconds / 1_000_000.0

            runOnUiThread {
                testResult.text = String.format(
                    Locale.US,
                    "تست با موفقیت انجام شد!\n\n" +
                    "زمان محاسبات: %.2f میلی‌ثانیه\n\n" +
                    "سرعت محاسبات: %.2f میلیون عملیات در ثانیه\n\n" +
                    "امتیاز آزمایشی: %d",
                    elapsedTime,
                    speed,
                    (speed * 1000).toInt()
                )

                testButton.isEnabled = true
                testButton.text = "تکرار تست پردازنده"
            }
        }.start()
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

            String.format(Locale.US, "%.2f GB", gb)
        } catch (e: Exception) {
            "نامشخص"
        }
    }
}
