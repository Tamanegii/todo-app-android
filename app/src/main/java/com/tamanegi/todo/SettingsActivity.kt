package com.tamanegi.todo

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("todo_config", Context.MODE_PRIVATE)

        val urlInput = findViewById<TextInputEditText>(R.id.serverUrlInput)
        val tokenInput = findViewById<TextInputEditText>(R.id.authTokenInput)
        val saveButton = findViewById<MaterialButton>(R.id.saveButton)
        val statusText = findViewById<TextView>(R.id.settingsStatus)

        urlInput.setText(prefs.getString("server_url", "http://todo.tamanegi.xyz/api/v1/messages"))
        tokenInput.setText(prefs.getString("auth_token", ""))

        saveButton.setOnClickListener {
            val url = urlInput.text?.toString()?.trim() ?: ""
            val token = tokenInput.text?.toString()?.trim() ?: ""

            prefs.edit()
                .putString("server_url", url)
                .putString("auth_token", token)
                .apply()

            statusText.setTextColor(0xFF2ecc71.toInt())
            statusText.text = "已保存"
        }
    }
}
