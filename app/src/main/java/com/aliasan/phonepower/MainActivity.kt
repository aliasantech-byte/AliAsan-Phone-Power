
package com.aliasan.phonepower

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.io.File
import java.util.Locale

class MainActivity : Activity() {

    private val blue = Color.rgb(25, 80, 160)
    private val dark = Color.rgb(35, 45, 60)

    data class Game(
        val name: String,
        val android: Int? = null,
        val ram: Double? = null,
        val storage: Double? = null,
        val gl: Int? = null,
        val cores: Int? = null,
        val bit64: Boolean = false,
        val extra: String = ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        scroll.setBackgroundColor(Color.rgb(245, 247, 250))

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(18, 22, 18, 22)

        addTitle(root, "توان‌سنج گوشی علی آسان")
        addText(root, "شناسنامه کامل گوشی و بررسی سازگاری بازی‌ها")

        addSection(root, "مشخصات عمومی")
        addRow(root, "برند", Build.MANUFACTURER)
        addRow(root, "مدل", Build.MODEL)
        addRow(root, "نام دستگاه", Build.DEVICE)
        addRow(root, "نام محصول", Build.PRODUCT)
        addRow(root, "شناسه سخت‌افزار", Build.HARDWARE)
        addRow(root, "نسخه اندروید", Build.VERSION.RELEASE)
        addRow(root, "سطح API", Build.VERSION.SDK_INT.toString())
        addRow(root, "نسخه بیلد", Build.DISPLAY)

        addSection(root, "پردازنده و حافظه")
        addRow(
            root,
            "تعداد هسته‌های قابل استفاده",
            Runtime.getRuntime().availableProcessors().toString()
        )
        addRow(root, "معماری پردازنده", getAbi())
        addRow(root, "پشتیبانی ۶۴ بیتی", if (is64Bit()) "بله" else "خیر یا نامشخص")
        addRow(root, "حافظه RAM کل", formatGb(getRamGb()))
        addRow(root, "فضای ذخیره‌سازی آزاد", formatGb(getFreeStorageGb()))

        addSection(root, "صفحه‌نمایش و گرافیک")
        val metrics = resources.displayMetrics
        addRow(
            root,
            "وضوح صفحه",
            "${metrics.widthPixels} × ${metrics.heightPixels}"
        )
        addRow(root, "تراکم صفحه", "${metrics.densityDpi} DPI")
        addRow(root, "اندازه چگالی", String.format(Locale.US, "%.2f", metrics.density))
        addRow(root, "نسخه OpenGL ES", getGlVersion())
        addRow(root, "مدل دقیق GPU", "در این نسخه هنوز شناسایی نشده")

        addSection(root, "باتری و تجهیزات")
        addRow(root, "وضعیت باتری", getBatteryInfo())
        addRow(root, "تعداد دوربین‌ها", getCameraCount())
        addRow(root, "تعداد سنسورها", getSensorCount())
        addRow(root, "نام سنسورها", getSensorNames())
        addRow(root, "Wi-Fi", supported("android.hardware.wifi"))
        addRow(root, "بلوتوث", supported("android.hardware.bluetooth"))
        addRow(root, "شبکه موبایل", supported("android.hardware.telephony"))

        addSection(root, "بررسی سازگاری بازی‌ها")
        addText(
            root,
            "نتایج بر اساس حداقل نیازمندی‌های منتشرشده است؛ " +
            "سازگاری نهایی و سرعت اجرای بازی باید جداگانه بررسی شود."
        )

        addGameCategory(root, "تیراندازی و آنلاین", listOf(
            Game("PUBG Mobile", extra = "نیازمند بررسی مشخصات رسمی نسخه فعلی"),
            Game("Free Fire", android = 4, ram = 1.0, storage = 1.5),
            Game("Call of Duty Mobile", android = 6, ram = 2.0),
            Game("Standoff 2", ram = 2.0, storage = 2.0,
                extra = "رم پیشنهادی ۴ گیگابایت یا بیشتر")
        ))

        addGameCategory(root, "فوتبال", listOf(
            Game("eFootball", android = 7, ram = 2.0,
                cores = 4, bit64 = true),
            Game("EA SPORTS FC Mobile", android = 6, ram = 1.7,
                cores = 4, extra = "مدل GPU و سرعت CPU هم باید بررسی شود"),
            Game("Dream League Soccer",
                extra = "نیازمند بررسی مشخصات رسمی نسخه فعلی")
        ))

        addGameCategory(root, "مسابقه‌ای و رانندگی", listOf(
            Game("Asphalt Legends", android = 9,
                extra = "سخت‌افزار گرافیکی هم باید بررسی شود"),
            Game("CarX Street",
                extra = "نیازمند بررسی مشخصات رسمی نسخه فعلی"),
            Game("Real Racing 3",
                extra = "نیازمند بررسی مشخصات رسمی نسخه فعلی")
        ))

        addGameCategory(root, "ماجراجویی و جهان‌باز", listOf(
            Game("Genshin Impact", android = 10, ram = 4.0,
                storage = 40.0, bit64 = true,
                extra = "GPU غیر PowerVR و چیپست مناسب نیز لازم است"),
            Game("Minecraft", android = 9, ram = 1.1,
                storage = 1.0, gl = 31),
            Game("Roblox", android = 8, gl = 30)
        ))

        addSection(root, "علی آسان")
        addText(root, "سریع و آسان | شناسایی و بررسی گوشی")

        scroll.addView(root)
        setContentView(scroll)
    }

    private fun addTitle(root: LinearLayout, text: String) {
        val view = TextView(this)
        view.text = text
        view.textSize = 25f
        view.setTextColor(blue)
        view.gravity = Gravity.CENTER
        view.setPadding(0, 12, 0, 10)
        root.addView(view)
    }

    private fun addSection(root: LinearLayout, text: String) {
        val view = TextView(this)
        view.text = text
        view.textSize = 20f
        view.setTextColor(blue)
        view.setPadding(0, 24, 0, 12)
        root.addView(view)
    }

    private fun addText(root: LinearLayout, text: String) {
        val view = TextView(this)
        view.text = text
        view.textSize = 15f
        view.setTextColor(dark)
        view.setPadding(4, 8, 4, 12)
        root.addView(view)
    }

    private fun addRow(
        root: LinearLayout,
        label: String,
        value: String
    ) {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(14, 10, 14, 10)
        card.setBackgroundColor(Color.WHITE)

        val heading = TextView(this)
        heading.text = label
        heading.textSize = 13f
        heading.setTextColor(Color.GRAY)

        val result = TextView(this)
        result.text = value
        result.textSize = 16f
        result.setTextColor(dark)
        result.setPadding(0, 4, 0, 0)

        card.addView(heading)
        card.addView(result)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = 8

        root.addView(card, params)
    }

    private fun addGameCategory(
        root: LinearLayout,
        category: String,
        games: List<Game>
    ) {
        addSection(root, category)

        for (game in games) {
            val result = checkGame(game)
            addRow(
                root,
                game.name,
                result.first + "\n" + result.second
            )
        }
    }

    private fun checkGame(game: Game): Pair<String, String> {
        val failures = mutableListOf<String>()

        if (game.android != null &&
            Build.VERSION.SDK_INT < game.android
        ) failures.add("نسخه اندروید کافی نیست")

        val ram = getRamGb()
        if (game.ram != null && ram != null && ram < game.ram) {
            failures.add("RAM کمتر از حداقل اعلام‌شده است")
        }

        val storage = getFreeStorageGb()
        if (game.storage != null &&
            storage != null && storage < game.storage
        ) failures.add("فضای آزاد کافی نیست")

        val gl = getGlNumber()
        if (game.gl != null && gl != null && gl < game.gl) {
            failures.add("نسخه OpenGL ES کافی نیست")
        }

        if (game.cores != null &&
            Runtime.getRuntime().availableProcessors() < game.cores
        ) failures.add("تعداد هسته‌های قابل استفاده کافی نیست")

        if (game.bit64 && !is64Bit()) {
            failures.add("پشتیبانی ۶۴ بیتی تأیید نشده است")
        }

        if (failures.isNotEmpty()) {
            return Pair(
                "برخی حداقل‌ها تأمین نشده",
                failures.joinToString("، ")
            )
        }

        if (game.extra.isNotEmpty()) {
            return Pair(
                "نیازمند بررسی بیشتر",
                game.extra
            )
        }

        return Pair(
            "حداقل‌های اعلام‌شده را دارد",
            "اجرای روان تضمین نمی‌شود."
        )
    }

    private fun getRamGb(): Double? {
        return try {
            val line = File("/proc/meminfo").readLines()
                .firstOrNull { it.startsWith("MemTotal:") }
                ?: return null

            val kb = line.filter { it.isDigit() }.toLongOrNull()
                ?: return null

            kb / 1024.0 / 1024.0
        } catch (e: Exception) {
            null
        }
    }

    private fun getFreeStorageGb(): Double? {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val bytes = stat.availableBlocks.toLong() * stat.blockSize.toLong()
            bytes / 1024.0 / 1024.0 / 1024.0
        } catch (e: Exception) {
            null
        }
    }

    private fun formatGb(value: Double?): String {
        return if (value == null) {
            "نامشخص"
        } else {
            String.format(Locale.US, "%.2f GB", value)
        }
    }

    private fun getAbi(): String {
        return if (Build.VERSION.SDK_INT >= 21) {
            Build.SUPPORTED_ABIS.joinToString(", ")
        } else {
            Build.CPU_ABI
        }
    }

    private fun is64Bit(): Boolean {
        return if (Build.VERSION.SDK_INT >= 21) {
            Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()
        } else {
            false
        }
    }

    private fun getGlNumber(): Int? {
        return try {
            val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val version = manager.deviceConfigurationInfo.reqGlEsVersion
            if (version == 0) null else version
        } catch (e: Exception) {
            null
        }
    }

    private fun getGlVersion(): String {
        val version = getGlNumber() ?: return "نامشخص"
        val major = version shr 16
        val minor = version and 0xffff
        return "$major.$minor"
    }

    private fun getBatteryInfo(): String {
        return try {
            val intent = registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            ) ?: return "نامشخص"

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            if (level < 0 || scale <= 0) {
                "نامشخص"
            } else {
                "${level * 100 / scale} درصد"
            }
        } catch (e: Exception) {
            "نامشخص"
        }
    }

    private fun getCameraCount(): String {
        return try {
            if (Build.VERSION.SDK_INT >= 21) {
                val manager = getSystemService(CAMERA_SERVICE) as CameraManager
                manager.cameraIdList.size.toString()
            } else {
                if (packageManager.hasSystemFeature(
                    PackageManager.FEATURE_CAMERA
                )) "حداقل ۱" else "ندارد"
            }
        } catch (e: Exception) {
            "نامشخص"
        }
    }

    private fun getSensorCount(): String {
        return try {
            val manager = getSystemService(SENSOR_SERVICE) as SensorManager
            manager.getSensorList(Sensor.TYPE_ALL).size.toString()
        } catch (e: Exception) {
            "نامشخص"
        }
    }

    private fun getSensorNames(): String {
        return try {
            val manager = getSystemService(SENSOR_SERVICE) as SensorManager
            val names = manager.getSensorList(Sensor.TYPE_ALL)
                .take(10)
                .map { it.name }

            if (names.isEmpty()) "نامشخص"
            else names.joinToString("، ")
        } catch (e: Exception) {
            "نامشخص"
        }
    }

    private fun supported(feature: String): String {
        return if (packageManager.hasSystemFeature(feature)) {
            "پشتیبانی می‌شود"
        } else {
            "شناسایی نشد"
        }
    }
}
