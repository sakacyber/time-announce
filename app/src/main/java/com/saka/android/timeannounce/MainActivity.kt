package com.saka.android.timeannounce

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.saka.android.timeannounce.databinding.ActivityMainBinding
import com.saka.android.timeannounce.databinding.DialogNameBinding
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var talkTime: TextToSpeech? = null
    private val calendar by lazy {
        Calendar.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        talkTime = TextToSpeech(this) {
            talkTime?.language = Locale("km")
        }

        updateGreeting(Prefs.getGreeting())
        binding.switchTime.setOnCheckedChangeListener { _, isChecked ->
            toggleTimeSpeaking(isChecked)
        }

        binding.isShowName = Prefs.getGreeting() != ""
        binding.switchName.setOnCheckedChangeListener { _, isChecked ->
            toggleShowGreet(isChecked)
        }

        binding.imageSound.setOnClickListener {
            speakTime()
        }
    }

    private fun toggleShowGreet(isShow: Boolean) {
        if (isShow) {
            showInputDialog()
        } else {
            updateGreeting("")
        }
    }

    private fun showInputDialog() {
        val dialogBinding = DialogNameBinding.inflate(layoutInflater)
        dialogBinding.editTextName.setText(Prefs.getGreeting())
        MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { dialog, _ ->
                val username = dialogBinding.editTextName.text.toString().trim()
                updateGreeting(username)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                updateGreeting(Prefs.getGreeting())
                dialog.dismiss()
            }
            .setOnDismissListener {
                updateGreeting(Prefs.getGreeting())
            }
            .setOnCancelListener {
                updateGreeting(Prefs.getGreeting())
            }
            .show()
    }

    private fun updateGreeting(string: String) {
        if (string.isEmpty()) {
            Prefs.setGreeting("")
            binding.isShowName = false
        } else {
            Prefs.setGreeting(string)
            binding.isShowName = true
            binding.textUserName.text = string.capitalize(Locale.ROOT)
        }
    }

    private fun toggleTimeSpeaking(isOn: Boolean) {
        Prefs.setState(isOn)
        if (isOn) {
            val remainMinuteUntilNextHour = 60L - calendar.get(Calendar.MINUTE)
            val talkTimeRequest =
                PeriodicWorkRequestBuilder<TalkTimeWorker>(15, TimeUnit.MINUTES)
//                    .setInitialDelay(remainMinuteUntilNextHour, TimeUnit.MINUTES)
                    .build()
            WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                    "time",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    talkTimeRequest
                )
        } else {
            WorkManager.getInstance(this)
                .cancelAllWork()
        }
    }

    class TalkTimeWorker(
        private val context: Context,
        workerParameters: WorkerParameters
    ) : Worker(context, workerParameters), TextToSpeech.OnInitListener {

        private var textToSpeech: TextToSpeech? = null

        override fun doWork(): Result {
            speakTime(context)
            return Result.success()
        }

        private fun speakTime(context: Context) {
            textToSpeech = TextToSpeech(context, this)
        }

        override fun onStopped() {
            textToSpeech?.shutdown()
            super.onStopped()
        }

        override fun onInit(status: Int) {
            if (status != 0) return
            textToSpeech?.language = Locale.US
            val calendar = Calendar.getInstance()
            val currentTime = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            val username = Prefs.getGreeting()
            val greeting = if (username == "") "" else "Hello $username"
            val speech = "$greeting, The time is $currentTime and $currentMinute minutes"
            textToSpeech?.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null)
            Log.e("TalkTimeWorker", "speakTime: $speech")
        }
    }

    private fun speakTime() {
        calendar.time = Date(System.currentTimeMillis())
        val currentTime = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val username = Prefs.getGreeting()
        val greeting = if (username == "") "" else "សួស្តី $username"
//        val speech = "$greeting, The time is $currentTime and $currentMinute minutes"
        val speechKm = "$greeting, នៅពេលនេះម៉ោង $currentTime និង $currentMinute នាទី"
        val result = talkTime?.speak(speechKm, TextToSpeech.QUEUE_FLUSH, null, null)
        Log.e("MainActivity", "speakTime: $result / $speechKm")
    }

    override fun onDestroy() {
        super.onDestroy()
        talkTime?.shutdown()
    }
}
