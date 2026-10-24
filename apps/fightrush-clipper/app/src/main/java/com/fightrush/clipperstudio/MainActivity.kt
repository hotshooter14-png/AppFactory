package com.fightrush.clipperstudio

import android.graphics.Bitmap
import android.content.ContentValues
import android.provider.MediaStore
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.ClippingConfiguration
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Composition
import androidx.media3.transformer.Transformer
import java.io.File
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MainActivity : ComponentActivity() {
    private val executor = Executors.newSingleThreadExecutor()
    private var sourceUri: Uri? = null
    private var sourceDurationMs = 0L
    private var selectedSegments: List<Pair<Long, Long>> = emptyList()
    private var introSeconds = 35
    private var maxSeconds = 150
    private var walkouts = true
    private var faceOff = true
    private lateinit var status: TextView
    private lateinit var progress: ProgressBar
    private lateinit var scanButton: Button
    private lateinit var exportButton: Button
    private lateinit var introLabel: TextView
    private lateinit var maxLabel: TextView

    private val pickVideo = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try { contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
            sourceUri = uri
            status.text = "Video selected. Tap Scan for action."
            exportButton.isEnabled = false
            scanButton.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(16, 17, 20)
        window.navigationBarColor = android.graphics.Color.rgb(16, 17, 20)
        buildUi()
    }

    private fun buildUi() {
        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(28))
            setBackgroundColor(android.graphics.Color.rgb(16, 17, 20))
        }
        scroll.addView(root)
        setContentView(scroll)

        root.addView(label("FIGHTRUSH", 13, "#F04438", true))
        root.addView(label("CLIPPER STUDIO", 27, "#FFFFFF", true))
        root.addView(label("MMA • BOXING • HIGHLIGHTS", 12, "#A8ABB4", true))
        root.addView(space(22))
        root.addView(label("MAKE A HIGHLIGHT", 18, "#FFFFFF", true))
        root.addView(label("Choose a video from your phone. FightRush scans sampled frames for high-motion action and exports the strongest section without changing the source aspect ratio.", 14, "#C8CAD0", false))
        root.addView(space(14))

        val choose = button("1  •  Choose video from phone")
        choose.setOnClickListener { pickVideo.launch(arrayOf("video/*")) }
        root.addView(choose)

        scanButton = button("2  •  Scan for action").apply { isEnabled = false }
        scanButton.setOnClickListener { scanVideo() }
        root.addView(scanButton)

        root.addView(space(12))
        introLabel = label("INTRO DURATION  •  $introSeconds seconds (saved preference)", 14, "#FFFFFF", true)
        root.addView(introLabel)
        val introSeek = SeekBar(this).apply { max = 60; progress = 35 }
        introSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
                introSeconds = max(5, value)
                introLabel.text = "INTRO DURATION  •  $introSeconds seconds (setting only in this build)"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        root.addView(introSeek)

        maxLabel = label("MAX OUTPUT LENGTH  •  $maxSeconds seconds", 14, "#FFFFFF", true)
        root.addView(maxLabel)
        val maxSeek = SeekBar(this).apply { max = 150; progress = 150 }
        maxSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
                maxSeconds = max(30, value)
                maxLabel.text = "MAX OUTPUT LENGTH  •  $maxSeconds seconds"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        root.addView(maxSeek)
        root.addView(space(10))

        val toggles = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val walk = android.widget.CheckBox(this).apply {
            text = "Include walkouts"
            isChecked = true
            setTextColor(android.graphics.Color.WHITE)
            buttonTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.rgb(240,68,56))
            setOnCheckedChangeListener { _, checked -> walkouts = checked }
        }
        val face = android.widget.CheckBox(this).apply {
            text = "Include face-off"
            isChecked = true
            setTextColor(android.graphics.Color.WHITE)
            buttonTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.rgb(240,68,56))
            setOnCheckedChangeListener { _, checked -> faceOff = checked }
        }
        toggles.addView(walk, LinearLayout.LayoutParams(0, dp(48), 1f))
        toggles.addView(face, LinearLayout.LayoutParams(0, dp(48), 1f))
        root.addView(toggles)
        root.addView(label("Note: intro assembly and automatic walkout/face-off detection are not active in this first build. These controls are shown as your requested settings, not as completed processing features.", 12, "#E4B86A", false))
        root.addView(space(18))

        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100; progress = 0; visibility = View.GONE
        }
        root.addView(progress)
        status = label("Ready. Choose a video to begin.", 14, "#C8CAD0", false)
        root.addView(status)
        root.addView(space(12))

        exportButton = button("3  •  Export highlight MP4").apply { isEnabled = false }
        exportButton.setOnClickListener { exportHighlight() }
        root.addView(exportButton)
        root.addView(space(14))
        root.addView(label("Output folder: Gallery / Movies / FightRush", 12, "#8F939D", false))
    }

    private fun scanVideo() {
        val uri = sourceUri ?: return
        scanButton.isEnabled = false
        exportButton.isEnabled = false
        progress.visibility = View.VISIBLE
        progress.progress = 5
        status.text = "Reading video duration and scanning motion…"
        executor.execute {
            var retriever: MediaMetadataRetriever? = null
            try {
                retriever = MediaMetadataRetriever()
                retriever.setDataSource(this, uri)
                sourceDurationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
                if (sourceDurationMs < 1000L) throw IllegalStateException("Could not read video duration.")
                val durationSec = (sourceDurationMs / 1000L).toInt()
                val sampleStep = if (durationSec > 900) 3 else 1
                val scores = ArrayList<Pair<Int, Double>>()
                var previous: IntArray? = null
                var second = 0
                while (second < durationSec) {
                    val frame = retriever.getFrameAtTime(second * 1_000_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    if (frame != null) {
                        val tiny = Bitmap.createScaledBitmap(frame, 32, 18, true)
                        val pixels = IntArray(32 * 18)
                        tiny.getPixels(pixels, 0, 32, 0, 0, 32, 18)
                        val gray = IntArray(pixels.size)
                        for (i in pixels.indices) {
                            val c = pixels[i]
                            gray[i] = (((c shr 16) and 255) * 30 + ((c shr 8) and 255) * 59 + (c and 255) * 11) / 100
                        }
                        val old = previous
                        var diff = 0.0
                        if (old != null) for (i in gray.indices) diff += abs(gray[i] - old[i])
                        if (old != null) scores.add(second to (diff / gray.size))
                        previous = gray
                        tiny.recycle()
                        if (frame !== tiny && !frame.isRecycled) frame.recycle()
                    }
                    second += sampleStep
                    val pct = min(90, 5 + (second * 85 / max(1, durationSec)))
                    runOnUiThread { progress.progress = pct }
                }
                if (scores.isEmpty()) throw IllegalStateException("No readable video frames were found.")
                val ranked = ArrayList<Triple<Int, Int, Double>>()
                val windowSec = 10
                for (i in scores.indices) {
                    val start = scores[i].first
                    val end = min(durationSec, start + windowSec)
                    if (end - start < 5) continue
                    val values = scores.filter { it.first >= start && it.first < end }
                    if (values.isNotEmpty()) ranked.add(Triple(start, end, values.map { it.second }.average()))
                }
                ranked.sortByDescending { it.third }
                val chosen = ArrayList<Pair<Long, Long>>()
                var total = 0
                for (c in ranked) {
                    val start = c.first
                    val end = min(c.second, start + windowSec)
                    if (end <= start || total + end - start > maxSeconds) continue
                    if (chosen.any { start < it.second / 1000L + 3 && end > it.first / 1000L - 3 }) continue
                    chosen.add(Pair(start * 1000L, end * 1000L))
                    total += end - start
                    if (total >= maxSeconds) break
                }
                selectedSegments = chosen.sortedBy { it.first }
                if (selectedSegments.isEmpty()) throw IllegalStateException("No highlight windows selected.")
                runOnUiThread {
                    progress.progress = 100
                    status.text = "Scan complete: ${selectedSegments.size} separate action windows, ${total}s total. Export to join them."
                    exportButton.isEnabled = true
                    scanButton.isEnabled = true
                }
            } catch (e: Exception) {
                runOnUiThread {
                    status.text = "Scan failed: ${e.message ?: "unknown error"}"
                    scanButton.isEnabled = true
                }
            } finally {
                try { retriever?.release() } catch (_: Exception) {}
            }
        }
    }

    private fun exportHighlight() {
        val uri = sourceUri ?: return
        if (selectedSegments.isEmpty()) {
            Toast.makeText(this, "Scan a video first.", Toast.LENGTH_SHORT).show()
            return
        }
        val dir = File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "FightRush")
        if (!dir.exists()) dir.mkdirs()
        val output = File(dir, "FightRush_${System.currentTimeMillis()}.mp4")
        val editedItems = selectedSegments.map { segment ->
            val clip = MediaItem.Builder().setUri(uri).setClippingConfiguration(
                ClippingConfiguration.Builder().setStartPositionMs(segment.first).setEndPositionMs(segment.second).build()
            ).build()
            EditedMediaItem.Builder(clip).build()
        }
        val composition = Composition.Builder(EditedMediaItemSequence.Builder(editedItems).build()).build()
        progress.visibility = View.VISIBLE
        progress.progress = 0
        status.text = "Exporting MP4… keep FightRush open until finished."
        exportButton.isEnabled = false
        Transformer.Builder(this).addListener(object : Transformer.Listener {
            override fun onCompleted(composition: androidx.media3.transformer.Composition, exportResult: ExportResult) {
                runOnUiThread {
                    progress.progress = 100
                    try {
                        val values = ContentValues().apply {
                            put(MediaStore.Video.Media.DISPLAY_NAME, output.name)
                            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/FightRush")
                            put(MediaStore.Video.Media.IS_PENDING, 1)
                        }
                        val savedUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                            ?: throw IllegalStateException("Could not create a gallery video entry.")
                        try {
                            contentResolver.openOutputStream(savedUri, "w")?.use { destination ->
                                output.inputStream().use { source -> source.copyTo(destination) }
                            } ?: throw IllegalStateException("Could not open gallery output stream.")
                            val done = ContentValues().apply { put(MediaStore.Video.Media.IS_PENDING, 0) }
                            contentResolver.update(savedUri, done, null, null)
                            output.delete()
                            status.text = "Export complete: ${output.name}\nSaved to Gallery / Movies / FightRush."
                            Toast.makeText(this@MainActivity, "Highlight saved to Gallery: Movies/FightRush", Toast.LENGTH_LONG).show()
                        } catch (copyError: Exception) {
                            contentResolver.delete(savedUri, null, null)
                            throw copyError
                        }
                    } catch (saveError: Exception) {
                        status.text = "Exported, but gallery save failed: ${saveError.message ?: "unknown error"}."
                        Toast.makeText(this@MainActivity, "Could not save to gallery. Check storage space.", Toast.LENGTH_LONG).show()
                    }
                    exportButton.isEnabled = true
                }
            }
            override fun onError(composition: androidx.media3.transformer.Composition, exportResult: ExportResult, exportException: ExportException) {
                runOnUiThread {
                    status.text = "Export failed: ${exportException.message ?: "unsupported video format"}"
                    exportButton.isEnabled = true
                }
            }
        }).build().start(composition, output.absolutePath)
    }

    private fun label(text: String, size: Int, color: String, bold: Boolean): TextView =
        TextView(this).apply {
            this.text = text
            textSize = size.toFloat()
            setTextColor(android.graphics.Color.parseColor(color))
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, dp(4), 0, dp(4))
        }

    private fun button(text: String) = Button(this).apply {
        this.text = text
        isAllCaps = false
        setTextColor(android.graphics.Color.WHITE)
        backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.rgb(240, 68, 56))
        minHeight = dp(52)
    }

    private fun space(h: Int) = View(this).apply { layoutParams = LinearLayout.LayoutParams(1, dp(h)) }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}
