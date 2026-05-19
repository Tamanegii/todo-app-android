package com.tamanegi.todo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val serverUrl = "http://todo.tamanegi.xyz/api/v1/messages"
    private val authToken = BuildConfig.AUTH_TOKEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val projectInput = findViewById<TextInputEditText>(R.id.projectInput)
        val contentInput = findViewById<TextInputEditText>(R.id.contentInput)
        val sendButton = findViewById<MaterialButton>(R.id.sendButton)
        val statusText = findViewById<TextView>(R.id.statusText)

        sendButton.setOnClickListener {
            val text = contentInput.text?.toString()?.trim() ?: ""
            if (text.isEmpty()) return@setOnClickListener

            val project = projectInput.text?.toString()?.trim() ?: ""
            sendButton.isEnabled = false
            statusText.text = ""

            sendMessage(text, project,
                onSuccess = {
                    runOnUiThread {
                        statusText.setTextColor(0xFF2ecc71.toInt())
                        statusText.text = "已发送"
                        contentInput.text?.clear()
                        sendButton.isEnabled = true
                    }
                },
                onError = { msg ->
                    runOnUiThread {
                        statusText.setTextColor(0xFFe74c3c.toInt())
                        statusText.text = msg
                        sendButton.isEnabled = true
                    }
                }
            )
        }
    }

    private fun sendMessage(text: String, project: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val json = JSONObject().apply {
            put("text", text)
            put("session_id", "android")
            if (project.isNotEmpty()) put("project_id", project)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(serverUrl)
            .addHeader("Authorization", "Bearer $authToken")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError("网络错误")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 202 || response.isSuccessful) {
                    onSuccess()
                } else if (response.code == 401) {
                    onError("认证失败")
                } else {
                    onError("发送失败")
                }
            }
        })
    }
}