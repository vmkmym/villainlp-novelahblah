package com.example.villainlp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etQuestion = findViewById<EditText>(R.id.etQuestion)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val txtResponse = findViewById<TextView>(R.id.txtResponse)

        btnSubmit.setOnClickListener {
            val question = etQuestion.text.toString().trim()

            if (question.isNotEmpty()) {
                Toast.makeText(this, question, Toast.LENGTH_SHORT).show()
                getResponse(question) { response ->
                    runOnUiThread {
                        if (response.isNotBlank()) {
                            txtResponse.text = response
                        } else {
                            txtResponse.text = "No answer found."
                        }
                    }
                }
            }
        }
    }

    private fun getResponse(question: String, callback: (String) -> Unit) {
        val apiKey = "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"
        val url = "https://api.openai.com/v1/chat/completions"
        val requestBody = """
            {
                "prompt": "$question",
                "max_tokens": 500,
                "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API failed", e)
                callback("")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.v("data", "API response code: ${response.code}")

                if (!response.isSuccessful) {
                    Log.e("error", "API error: ${response.code}")
                    callback("")
                    return
                }

                val body = response.body?.string()
                if (body != null) {
                    Log.v("data", body)
                } else {
                    Log.v("data", "empty")
                }

                try {
                    val jsonObject = JSONObject(body)
                    val jsonArray = jsonObject.getJSONArray("choices")
                    val textResult = jsonArray.getJSONObject(0).getString("Text")
                    callback(textResult)
                } catch (e: JSONException) {
                    Log.e("error", "No choices found in JSON body", e)
                    callback("")
                }
            }
        })
    }
}


