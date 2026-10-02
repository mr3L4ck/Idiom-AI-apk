package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiResult
import com.example.data.AiTutorRepository
import com.example.data.ChatMessage
import com.example.data.GeminiService
import com.example.data.Language
import com.example.data.MessageSender
import com.example.data.TutorLevel
import com.example.data.UserPreferences
import com.example.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiTutorViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = AiTutorRepository(application)
  private val userPrefsRepository = UserPreferencesRepository(application)
  private val geminiService = GeminiService()

  val userPreferences: StateFlow<UserPreferences> = userPrefsRepository.preferences

  private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
  val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _messageDraft = MutableStateFlow("")
  val messageDraft: StateFlow<String> = _messageDraft.asStateFlow()

  private val _tutorLevel = MutableStateFlow(repository.getTutorLevel())
  val tutorLevel: StateFlow<TutorLevel> = _tutorLevel.asStateFlow()

  private val _explainInNative = MutableStateFlow(repository.getExplainInNative())
  val explainInNative: StateFlow<Boolean> = _explainInNative.asStateFlow()

  private val _isApiKeyAvailable = MutableStateFlow(geminiService.isApiKeyConfigured())
  val isApiKeyAvailable: StateFlow<Boolean> = _isApiKeyAvailable.asStateFlow()

  private var currentLanguageId: String = ""

  fun initForLanguage(targetLanguage: Language, nativeLanguage: Language) {
    if (currentLanguageId == targetLanguage.id && _messages.value.isNotEmpty()) {
      return
    }
    currentLanguageId = targetLanguage.id
    val loaded = repository.loadMessages(targetLanguage.id)
    if (loaded.isEmpty()) {
      val welcomeMessage = createDefaultWelcomeMessage(targetLanguage, nativeLanguage)
      _messages.value = listOf(welcomeMessage)
      repository.saveMessages(targetLanguage.id, _messages.value)
    } else {
      _messages.value = loaded
    }
  }

  fun setDraft(text: String) {
    _messageDraft.value = text
  }

  fun clearDraft() {
    _messageDraft.value = ""
  }

  fun sendMessage(userText: String, targetLanguage: Language, nativeLanguage: Language) {
    val trimmed = userText.trim()
    if (trimmed.isBlank() || _isLoading.value) return

    val priorHistory = _messages.value

    val userMsg = ChatMessage(
      id = "msg_${System.currentTimeMillis()}",
      sender = MessageSender.USER,
      text = trimmed,
      targetLanguageId = targetLanguage.id
    )

    val updatedList = priorHistory + userMsg
    _messages.value = updatedList
    repository.saveMessages(targetLanguage.id, updatedList)

    _isLoading.value = true

    viewModelScope.launch {
      try {
        val result = geminiService.sendChatMessage(
          history = priorHistory,
          newUserMessage = trimmed,
          targetLanguage = targetLanguage,
          nativeLanguage = nativeLanguage,
          level = _tutorLevel.value,
          explainInNativeLanguage = _explainInNative.value
        )

        _isLoading.value = false

        val responseMessage = when (result) {
          is AiResult.Success -> {
            _messageDraft.value = "" // Clear draft on confirmed success
            ChatMessage(
              id = "tutor_${System.currentTimeMillis()}",
              sender = MessageSender.TUTOR,
              text = result.text,
              targetLanguageId = targetLanguage.id
            )
          }
          is AiResult.MissingApiKey -> {
            _messageDraft.value = trimmed // Preserve draft for user
            ChatMessage(
              id = "err_${System.currentTimeMillis()}",
              sender = MessageSender.SYSTEM,
              text = result.message,
              targetLanguageId = targetLanguage.id,
              isError = true,
              canRetry = false
            )
          }
          is AiResult.TransientError -> {
            _messageDraft.value = trimmed // Preserve draft for user
            ChatMessage(
              id = "err_${System.currentTimeMillis()}",
              sender = MessageSender.SYSTEM,
              text = result.message,
              targetLanguageId = targetLanguage.id,
              isError = true,
              canRetry = true
            )
          }
          is AiResult.RateLimited -> {
            _messageDraft.value = trimmed
            ChatMessage(
              id = "err_${System.currentTimeMillis()}",
              sender = MessageSender.SYSTEM,
              text = result.message,
              targetLanguageId = targetLanguage.id,
              isError = true,
              canRetry = true
            )
          }
          is AiResult.Error -> {
            _messageDraft.value = trimmed
            ChatMessage(
              id = "err_${System.currentTimeMillis()}",
              sender = MessageSender.SYSTEM,
              text = result.message,
              targetLanguageId = targetLanguage.id,
              isError = true,
              canRetry = true
            )
          }
        }

        val finalList = _messages.value + responseMessage
        _messages.value = finalList
        repository.saveMessages(targetLanguage.id, finalList)
      } catch (e: Exception) {
        _isLoading.value = false
        _messageDraft.value = trimmed
        val errMessage = ChatMessage(
          id = "err_${System.currentTimeMillis()}",
          sender = MessageSender.SYSTEM,
          text = "Unable to complete AI tutor request (${e.localizedMessage ?: "Unexpected error"}). Please tap Retry.",
          targetLanguageId = targetLanguage.id,
          isError = true,
          canRetry = true
        )
        val finalList = _messages.value + errMessage
        _messages.value = finalList
        repository.saveMessages(targetLanguage.id, finalList)
      }
    }
  }

  fun retryLastUserMessage(targetLanguage: Language, nativeLanguage: Language) {
    if (_isLoading.value) return
    val current = _messages.value
    val lastUserMsg = current.lastOrNull { it.sender == MessageSender.USER } ?: return

    // Clean away trailing error messages without touching user messages
    val cleanedList = current.filterNot { it.isError }
    _messages.value = cleanedList

    // Prior history is everything before this last user message
    val userIndex = cleanedList.indexOf(lastUserMsg)
    val priorHistory = if (userIndex >= 0) cleanedList.subList(0, userIndex) else emptyList()

    _isLoading.value = true

    viewModelScope.launch {
      try {
        val result = geminiService.sendChatMessage(
          history = priorHistory,
          newUserMessage = lastUserMsg.text,
          targetLanguage = targetLanguage,
          nativeLanguage = nativeLanguage,
          level = _tutorLevel.value,
          explainInNativeLanguage = _explainInNative.value
        )

        _isLoading.value = false

        val responseMessage = when (result) {
          is AiResult.Success -> {
            _messageDraft.value = ""
            ChatMessage(
              id = "tutor_${System.currentTimeMillis()}",
              sender = MessageSender.TUTOR,
              text = result.text,
              targetLanguageId = targetLanguage.id
            )
          }
          is AiResult.TransientError -> {
            ChatMessage(
              id = "err_${System.currentTimeMillis()}",
              sender = MessageSender.SYSTEM,
              text = result.message,
              targetLanguageId = targetLanguage.id,
              isError = true,
              canRetry = true
            )
          }
          is AiResult.RateLimited -> {
            ChatMessage(
              id = "err_${System.currentTimeMillis()}",
              sender = MessageSender.SYSTEM,
              text = result.message,
              targetLanguageId = targetLanguage.id,
              isError = true,
              canRetry = true
            )
          }
          is AiResult.MissingApiKey -> {
            ChatMessage(
              id = "err_${System.currentTimeMillis()}",
              sender = MessageSender.SYSTEM,
              text = result.message,
              targetLanguageId = targetLanguage.id,
              isError = true,
              canRetry = false
            )
          }
          is AiResult.Error -> {
            ChatMessage(
              id = "err_${System.currentTimeMillis()}",
              sender = MessageSender.SYSTEM,
              text = result.message,
              targetLanguageId = targetLanguage.id,
              isError = true,
              canRetry = true
            )
          }
        }

        val finalList = _messages.value + responseMessage
        _messages.value = finalList
        repository.saveMessages(targetLanguage.id, finalList)
      } catch (e: Exception) {
        _isLoading.value = false
        val errMessage = ChatMessage(
          id = "err_${System.currentTimeMillis()}",
          sender = MessageSender.SYSTEM,
          text = "Retry attempt failed (${e.localizedMessage ?: "Network error"}). Please tap Retry to try again.",
          targetLanguageId = targetLanguage.id,
          isError = true,
          canRetry = true
        )
        val finalList = _messages.value + errMessage
        _messages.value = finalList
        repository.saveMessages(targetLanguage.id, finalList)
      }
    }
  }

  fun setTutorLevel(level: TutorLevel) {
    _tutorLevel.value = level
    repository.saveTutorLevel(level)
  }

  fun setExplainInNative(enabled: Boolean) {
    _explainInNative.value = enabled
    repository.saveExplainInNative(enabled)
  }

  fun clearChatHistory(targetLanguage: Language, nativeLanguage: Language) {
    val welcome = createDefaultWelcomeMessage(targetLanguage, nativeLanguage)
    _messages.value = listOf(welcome)
    repository.clearMessages(targetLanguage.id)
    repository.saveMessages(targetLanguage.id, _messages.value)
  }

  fun clearConversation(targetLanguage: Language, nativeLanguage: Language) {
    clearChatHistory(targetLanguage, nativeLanguage)
  }

  fun saveVoiceSettings(
    engineMode: String,
    geminiVoice: String,
    speechRate: Float,
    autoPlay: Boolean
  ) {
    userPrefsRepository.setTutorVoiceSettings(
      engineMode = engineMode,
      geminiVoice = geminiVoice,
      speechRate = speechRate,
      autoPlay = autoPlay
    )
  }

  fun updateVoiceSettings(
    engineMode: String,
    geminiVoice: String,
    speechRate: Float,
    autoPlay: Boolean
  ) {
    saveVoiceSettings(engineMode, geminiVoice, speechRate, autoPlay)
  }

  private fun createDefaultWelcomeMessage(targetLanguage: Language, nativeLanguage: Language): ChatMessage {
    val greeting = targetLanguage.sampleGreeting
    val app = com.example.data.BrandConfig.APP_NAME
    val tutor = com.example.data.BrandConfig.AI_TUTOR_NAME
    val intro = when (targetLanguage.id.lowercase()) {
      "es" -> "¡Hola! Soy tu tutor de español en $app. Estoy aquí para ayudarte a practicar conversación, corregir errores con paciencia y responder tus dudas. ¿De qué te gustaría hablar hoy?"
      "fr" -> "Bonjour ! Je suis votre tuteur de français $app. Pratiquons ensemble la conversation à votre rythme. De quoi souhaitez-vous parler aujourd'hui ?"
      "de" -> "Hallo! Ich bin dein Deutsch-Tutor bei $app. Lass uns zusammen Konversation üben. Worüber möchtest du heute sprechen?"
      "ja" -> "こんにちは！${app}の日本語チューターです。一緒に日本語の会話を練習しましょう！今日は何について話しますか？"
      "ko" -> "안녕하세요! ${app} 한국어 튜টার입니다. 편안하게 한국어로 대화 연습을 해봐요. 오늘 어떤 이야기를 나누고 싶으신가요?"
      "zh" -> "你好！我是你的${app}中文导师。让我们一起练习对话吧！今天你想聊些什么？"
      "ar" -> "مرحباً بك! أنا معلمك للغة العربية في $app. دعনা نتمرن على المحادثة معاً. عَمَّا تحب أن نتحدث اليوم؟"
      "hy" -> "Բարև ձեզ: Ես ձեր $app հայերենի ուսուցիչն եմ: Եկեք միասին զրուցենք: Ինչի՞ մասին կցանկանայիք խոսել այսօর:"
      "bn" -> "নমস্কার! আমি $app-এ আপনার বাংলা শিক্ষক। আসুন একসাথে বাংলা ভাষা ও সুন্দর কথোপকথন অনুশীলন করি। আজ আপনি কী নিয়ে কথা বলতে চান?"
      else -> "$greeting! I am $tutor, your ${targetLanguage.name} tutor on $app. Let's practice conversation together at your own pace! You can type anything in ${targetLanguage.name}, or ask me for explanations in ${nativeLanguage.name}."
    }

    return ChatMessage(
      id = "welcome_${targetLanguage.id}",
      sender = MessageSender.TUTOR,
      text = intro,
      targetLanguageId = targetLanguage.id
    )
  }
}
