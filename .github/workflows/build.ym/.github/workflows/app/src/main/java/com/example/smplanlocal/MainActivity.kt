package com.example.smplanlocal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var etTask: EditText
    private val SPEECH_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etTask = findViewById(R.id.etTask)
        val btnVoice: ImageButton = findViewById(R.id.btnVoice)
        val btnSave: Button = findViewById(R.id.btnSave)

        createNotificationChannel()

        btnVoice.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث بإدخال المهمة...")
            }
            try {
                startActivityForResult(intent, SPEECH_CODE)
            } catch (e: Exception) {
                Toast.makeText(this, "الإدخال الصوتي غير مدعوم", Toast.LENGTH_SHORT).show()
            }
        }

        btnSave.setOnClickListener {
            val text = etTask.text.toString()
            if (text.isNotEmpty()) {
                sendNotification(text)
                etTask.setText("")
                Toast.makeText(this, "تم إرسال المهمة وتنبيه الساعة!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_CODE && resultCode == RESULT_OK && data != null) {
            val res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!res.isNullOrEmpty()) {
                etTask.setText(res[0])
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("smplan_channel", "تنبيهات SmPlan", NotificationManager.IMPORTANCE_HIGH)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun sendNotification(taskTitle: String) {
        val builder = NotificationCompat.Builder(this, "smplan_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("تذكير SmPlan المحلي")
            .setContentText(taskTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
