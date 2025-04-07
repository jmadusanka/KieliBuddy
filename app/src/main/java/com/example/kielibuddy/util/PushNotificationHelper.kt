package com.example.kielibuddy.util

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

fun sendPushNotification(context: Context, toToken: String, title: String, message: String) {
    val json = JSONObject()
    val notification = JSONObject()
    notification.put("title", title)
    notification.put("body", message)
    json.put("to", toToken)
    json.put("notification", notification)

    val request = object : JsonObjectRequest(
        Request.Method.POST,
        "https://fcm.googleapis.com/fcm/send",
        json,
        { /* success */ },
        { error -> error.printStackTrace() }
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "key=BEdnvHrbvbQGIx7csJtR-rbyht0oqOt1_aVXXJu7SGnkDM6v19oRBHlel1OCYm5dJ_MMuEjlvBOjWljT1KyomK8"
            headers["Content-Type"] = "application/json"
            return headers
        }
    }

    Volley.newRequestQueue(context).add(request)
}
