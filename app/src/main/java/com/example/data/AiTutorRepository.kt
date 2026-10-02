package com.example.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class AiTutorRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.applicationContext.getSharedPreferences("linguasphere_ai_tutor_prefs", Context.MODE_PRIVATE)

  fun loadMessages(targetLanguageId: String): List<ChatMessage> {
    val jsonString = prefs.getString("chat_history_$targetLanguageId", null) ?: return emptyList()
    return try {
      val jsonArray = JSONArray(jsonString)
      val list = mutableListOf<ChatMessage>()
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          ChatMessage(
            id = obj.optString("id", System.currentTimeMillis().toString()),
            sender = MessageSender.valueOf(obj.optString("sender", MessageSender.USER.name)),
            text = obj.optString("text", ""),
            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
            targetLanguageId = obj.optString("targetLanguageId", targetLanguageId),
            isError = obj.optBoolean("isError", false),
            canRetry = obj.optBoolean("canRetry", false)
          )
        )
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  fun saveMessages(targetLanguageId: String, messages: List<ChatMessage>) {
    try {
      val jsonArray = JSONArray()
      // Keep up to 50 most recent messages locally
      messages.takeLast(50).forEach { msg ->
        val obj = JSONObject()
        obj.put("id", msg.id)
        obj.put("sender", msg.sender.name)
        obj.put("text", msg.text)
        obj.put("timestamp", msg.timestamp)
        obj.put("targetLanguageId", msg.targetLanguageId)
        obj.put("isError", msg.isError)
        obj.put("canRetry", msg.canRetry)
        jsonArray.put(obj)
      }
      prefs.edit().putString("chat_history_$targetLanguageId", jsonArray.toString()).apply()
    } catch (_: Exception) {
      // Safe fallback
    }
  }

  fun clearMessages(targetLanguageId: String) {
    prefs.edit().remove("chat_history_$targetLanguageId").apply()
  }

  fun getTutorLevel(): TutorLevel {
    val levelName = prefs.getString("tutor_level", TutorLevel.BEGINNER.name) ?: TutorLevel.BEGINNER.name
    return try {
      TutorLevel.valueOf(levelName)
    } catch (_: Exception) {
      TutorLevel.BEGINNER
    }
  }

  fun setTutorLevel(level: TutorLevel) {
    prefs.edit().putString("tutor_level", level.name).apply()
  }

  fun saveTutorLevel(level: TutorLevel) {
    setTutorLevel(level)
  }

  fun getExplainInNative(): Boolean {
    return prefs.getBoolean("explain_in_native", true)
  }

  fun setExplainInNative(enabled: Boolean) {
    prefs.edit().putBoolean("explain_in_native", enabled).apply()
  }

  fun saveExplainInNative(enabled: Boolean) {
    setExplainInNative(enabled)
  }
}
