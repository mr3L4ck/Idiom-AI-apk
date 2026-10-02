package com.example.data

data class SpeakingPhrase(
  val id: String,
  val original: String,
  val pronunciation: String,
  val translation: String,
  val contextTip: String,
  val level: String = "Beginner"
)

data class WordComparison(
  val word: String,
  val matched: Boolean
)

data class PronunciationEvaluation(
  val expectedText: String,
  val recognizedText: String,
  val isExactMatch: Boolean,
  val isApproximateMatch: Boolean,
  val matchedWordCount: Int,
  val totalWordCount: Int,
  val matchPercentage: Int,
  val wordComparisons: List<WordComparison>,
  val feedbackSummary: String,
  val disclaimerText: String = "Word match comparison is based on automated speech recognition transcript matching, not acoustic phonetic analysis."
)

data class ListeningExercise(
  val id: String,
  val spokenPhrase: String,
  val promptQuestion: String,
  val options: List<String>,
  val correctOptionIndex: Int,
  val translation: String,
  val pronunciation: String,
  val contextExplanation: String
)

object PronunciationEvaluator {

  private val punctuationRegex = Regex("[.,!?;:\"'()¡¿—\\[\\]{}]")

  fun cleanWord(text: String): String {
    return text.replace(punctuationRegex, "").trim().lowercase()
  }

  fun evaluate(expected: String, recognized: String): PronunciationEvaluation {
    val cleanExpected = cleanWord(expected)
    val cleanRecognized = cleanWord(recognized)

    val expectedWords = expected
      .split("\\s+".toRegex())
      .filter { it.isNotBlank() }

    val recognizedWordSet = recognized
      .split("\\s+".toRegex())
      .map { cleanWord(it) }
      .filter { it.isNotBlank() }
      .toSet()

    var matchedCount = 0
    val comparisons = expectedWords.map { originalToken ->
      val cleaned = cleanWord(originalToken)
      val isMatch = recognizedWordSet.contains(cleaned)
      if (isMatch) matchedCount++
      WordComparison(word = originalToken, matched = isMatch)
    }

    val totalWords = expectedWords.size.coerceAtLeast(1)
    val percentage = ((matchedCount.toFloat() / totalWords) * 100).toInt().coerceIn(0, 100)

    val isExact = cleanExpected == cleanRecognized || (percentage == 100 && recognized.isNotBlank())
    val isApproximate = !isExact && percentage >= 60

    val feedback = when {
      isExact -> "Excellent! Perfect transcript match with the target phrase."
      isApproximate -> "Good effort! Most words recognized correctly ($matchedCount of $totalWords words)."
      matchedCount > 0 -> "Some words recognized ($matchedCount of $totalWords). Try listening once more and speaking steadily."
      else -> "No matching words detected. Listen to the expected pronunciation and give it another try!"
    }

    return PronunciationEvaluation(
      expectedText = expected,
      recognizedText = recognized,
      isExactMatch = isExact,
      isApproximateMatch = isApproximate,
      matchedWordCount = matchedCount,
      totalWordCount = totalWords,
      matchPercentage = percentage,
      wordComparisons = comparisons,
      feedbackSummary = feedback
    )
  }
}

object VoiceLearningCatalog {

  fun getSpeakingPhrases(languageId: String): List<SpeakingPhrase> {
    return when (languageId.lowercase()) {
      "es" -> listOf(
        SpeakingPhrase(
          id = "es_spk_1",
          original = "¡Hola! ¿Cómo estás?",
          pronunciation = "OH-lah, KOH-moh ess-TAHS?",
          translation = "Hello! How are you?",
          contextTip = "The inverted marks '¡¿' indicate questioning and exclamation tone in Spanish."
        ),
        SpeakingPhrase(
          id = "es_spk_2",
          original = "Mucho gusto en conocerte",
          pronunciation = "MOO-choh GOOS-toh en koh-noh-SEHR-teh",
          translation = "Nice to meet you",
          contextTip = "Pronounce 'g' smoothly before vowels in 'gusto'."
        ),
        SpeakingPhrase(
          id = "es_spk_3",
          original = "Por favor y gracias",
          pronunciation = "por fah-VOR ee GRAH-syahs",
          translation = "Please and thank you",
          contextTip = "Essential politeness phrases used daily across the Spanish-speaking world."
        ),
        SpeakingPhrase(
          id = "es_spk_4",
          original = "Buenos días, que tengas un buen día",
          pronunciation = "BWAY-nohs DEE-ahs, kay TEN-gahs oon bwen DEE-ah",
          translation = "Good morning, have a good day",
          contextTip = "'Buenos días' is standard until the afternoon."
        ),
        SpeakingPhrase(
          id = "es_spk_5",
          original = "¿Dónde está la estación de tren?",
          pronunciation = "DOHN-day ess-TAH lah ess-tah-SYOHN day tren?",
          translation = "Where is the train station?",
          contextTip = "Stress the final syllable on 'está' and 'estación'."
        )
      )

      "fr" -> listOf(
        SpeakingPhrase(
          id = "fr_spk_1",
          original = "Bonjour, comment allez-vous?",
          pronunciation = "bon-ZHOOR, koh-mahn tah-lay VOO?",
          translation = "Hello, how are you?",
          contextTip = "Notice the liaison between 'comment' and 'allez'."
        ),
        SpeakingPhrase(
          id = "fr_spk_2",
          original = "Un café, s'il vous plaît",
          pronunciation = "uhn kah-FAY, seel voo pleh",
          translation = "A coffee, please",
          contextTip = "The staple polite phrase for ordering in France."
        ),
        SpeakingPhrase(
          id = "fr_spk_3",
          original = "Merci beaucoup pour votre aide",
          pronunciation = "mair-SEE boh-KOO poor VO-truh ed",
          translation = "Thank you very much for your help",
          contextTip = "'Beaucoup' ends in a round 'oo' sound, silent 'p'."
        ),
        SpeakingPhrase(
          id = "fr_spk_4",
          original = "Bonne journée et à bientôt!",
          pronunciation = "bun zhoor-NAY ay ah byan-TOH!",
          translation = "Have a great day and see you soon!",
          contextTip = "A warm, customary parting phrase."
        )
      )

      "de" -> listOf(
        SpeakingPhrase(
          id = "de_spk_1",
          original = "Guten Tag, wie geht es Ihnen?",
          pronunciation = "GOO-ten tahk, vee gayt es EE-nen?",
          translation = "Good day, how are you?",
          contextTip = "'W' is pronounced like English 'V'."
        ),
        SpeakingPhrase(
          id = "de_spk_2",
          original = "Freut mich, Sie kennenzulernen",
          pronunciation = "FROYT mikh, zee KEN-en-tsoo-lehr-nen",
          translation = "Pleased to meet you",
          contextTip = "'eu' sounds like English 'oy' in 'Freut'."
        ),
        SpeakingPhrase(
          id = "de_spk_3",
          original = "Bitte und danke schön",
          pronunciation = "BIT-teh oont DAHN-keh shurn",
          translation = "Please and thank you very much",
          contextTip = "Essential daily courtesy words."
        ),
        SpeakingPhrase(
          id = "de_spk_4",
          original = "Auf Wiedersehen, bis bald!",
          pronunciation = "owf VEE-der-zayn, biss bahlt!",
          translation = "Goodbye, see you soon!",
          contextTip = "Standard formal and semi-formal departure."
        )
      )

      "ja" -> listOf(
        SpeakingPhrase(
          id = "ja_spk_1",
          original = "こんにちは",
          pronunciation = "Konnichiwa (kohn-nee-chee-wah)",
          translation = "Hello / Good afternoon",
          contextTip = "The final 'ha' character is pronounced 'wa' as a topic particle."
        ),
        SpeakingPhrase(
          id = "ja_spk_2",
          original = "はじめまして",
          pronunciation = "Hajimemashite (hah-jee-meh-mahsh-teh)",
          translation = "Nice to meet you",
          contextTip = "Traditional first-time introduction phrase."
        ),
        SpeakingPhrase(
          id = "ja_spk_3",
          original = "ありがとうございます",
          pronunciation = "Arigatou gozaimasu (ah-ree-gah-toh goh-zeye-mahs)",
          translation = "Thank you very much",
          contextTip = "The final 'u' in 'masu' is whispered or silent."
        ),
        SpeakingPhrase(
          id = "ja_spk_4",
          original = "すみません",
          pronunciation = "Sumimasen (soo-mee-mah-sen)",
          translation = "Excuse me / I'm sorry",
          contextTip = "Versatile word to call a waiter or politely apologize."
        )
      )

      "ko" -> listOf(
        SpeakingPhrase(
          id = "ko_spk_1",
          original = "안녕하세요",
          pronunciation = "Annyeonghaseyo (ahn-nyong-hah-seh-yoh)",
          translation = "Hello / Peace be with you",
          contextTip = "Universal polite greeting suitable in any everyday situation."
        ),
        SpeakingPhrase(
          id = "ko_spk_2",
          original = "감사합니다",
          pronunciation = "Gamsahamnida (gahm-sah-hahm-nee-dah)",
          translation = "Thank you",
          contextTip = "The standard polite thank you expression."
        ),
        SpeakingPhrase(
          id = "ko_spk_3",
          original = "반갑습니다",
          pronunciation = "Bangapseumnida (bahn-gahp-seum-nee-dah)",
          translation = "Nice to meet you",
          contextTip = "Clear pronunciation of the 'm' and 'n' consonants."
        )
      )

      "pt" -> listOf(
        SpeakingPhrase(
          id = "pt_spk_1",
          original = "Olá, como você está?",
          pronunciation = "oh-LAH, KOH-moo voh-SAY ess-TAH?",
          translation = "Hello, how are you?",
          contextTip = "Accented 'á' indicates strong open vowel stress."
        ),
        SpeakingPhrase(
          id = "pt_spk_2",
          original = "Muito prazer em conhecê-lo",
          pronunciation = "MOO-ee-too prah-ZEHR aym kohn-yay-SAY-loo",
          translation = "Pleasure to meet you",
          contextTip = "Nasal 'em' sounds similar to English 'aim' without ending hard."
        ),
        SpeakingPhrase(
          id = "pt_spk_3",
          original = "Muito obrigado",
          pronunciation = "MOO-ee-too oh-bree-GAH-doo",
          translation = "Thank you very much",
          contextTip = "Men say 'obrigado', women say 'obrigada'."
        )
      )

      "it" -> listOf(
        SpeakingPhrase(
          id = "it_spk_1",
          original = "Ciao, come stai?",
          pronunciation = "CHOW, KOH-may STY?",
          translation = "Hi, how are you?",
          contextTip = "Informal friendly greeting for friends and peers."
        ),
        SpeakingPhrase(
          id = "it_spk_2",
          original = "Piacere di conoscerti",
          pronunciation = "pyah-CHEH-ray dee koh-NOH-shehr-tee",
          translation = "Pleasure to meet you",
          contextTip = "Smooth Italian rhythm with rolling 'r'."
        ),
        SpeakingPhrase(
          id = "it_spk_3",
          original = "Grazie mille",
          pronunciation = "GRAHT-tsyeh MEEL-lay",
          translation = "A thousand thanks / Thank you so much",
          contextTip = "Double 'll' is pronounced with gentle prolongation."
        )
      )

      "ar" -> listOf(
        SpeakingPhrase(
          id = "ar_spk_1",
          original = "مرحباً بك",
          pronunciation = "Marhaban bik (MAR-hah-bahn beek)",
          translation = "Welcome / Hello",
          contextTip = "Open 'h' sound from the throat."
        ),
        SpeakingPhrase(
          id = "ar_spk_2",
          original = "السلام عليكم",
          pronunciation = "As-salamu alaykum",
          translation = "Peace be upon you",
          contextTip = "Traditional Arabic greeting known worldwide."
        ),
        SpeakingPhrase(
          id = "ar_spk_3",
          original = "شكراً جزيلاً",
          pronunciation = "Shukran jazilan",
          translation = "Thank you very much",
          contextTip = "Warm appreciation phrase."
        )
      )

      else -> {
        // Data-driven fallback using phrases from LessonCatalog
        val sampleLesson = LessonCatalog.getSampleLessonForLanguage(languageId)
        sampleLesson.phrases.mapIndexed { idx, p ->
          SpeakingPhrase(
            id = "${languageId}_fallback_$idx",
            original = p.original,
            pronunciation = p.pronunciation,
            translation = p.translation,
            contextTip = p.contextNote
          )
        }
      }
    }
  }

  fun getListeningExercises(languageId: String): List<ListeningExercise> {
    return when (languageId.lowercase()) {
      "es" -> listOf(
        ListeningExercise(
          id = "es_lis_1",
          spokenPhrase = "Mucho gusto en conocerte",
          promptQuestion = "Listen carefully to the audio. What is the meaning in English?",
          options = listOf(
            "Nice to meet you",
            "Where is the restaurant?",
            "Good evening, see you tomorrow",
            "I would like a coffee, please"
          ),
          correctOptionIndex = 0,
          translation = "Nice to meet you",
          pronunciation = "MOO-choh GOOS-toh en koh-noh-SEHR-teh",
          contextExplanation = "'Mucho gusto' literally means 'much pleasure', used upon introduction."
        ),
        ListeningExercise(
          id = "es_lis_2",
          spokenPhrase = "Por favor y gracias",
          promptQuestion = "What courteous expression was spoken?",
          options = listOf(
            "Excuse me and goodbye",
            "Please and thank you",
            "Yes and no",
            "Good day and good night"
          ),
          correctOptionIndex = 1,
          translation = "Please and thank you",
          pronunciation = "por fah-VOR ee GRAH-syahs",
          contextExplanation = "'Por favor' = please, 'gracias' = thank you."
        ),
        ListeningExercise(
          id = "es_lis_3",
          spokenPhrase = "¿Cómo te llamas?",
          promptQuestion = "What question is being asked in Spanish?",
          options = listOf(
            "How old are you?",
            "Where are you from?",
            "What is your name?",
            "Do you speak English?"
          ),
          correctOptionIndex = 2,
          translation = "What is your name?",
          pronunciation = "KOH-moh tay YAH-mahs?",
          contextExplanation = "Literally 'How do you call yourself?' - standard conversational introduction."
        ),
        ListeningExercise(
          id = "es_lis_4",
          spokenPhrase = "Buenos días",
          promptQuestion = "Which daily greeting was spoken?",
          options = listOf(
            "Good morning",
            "Good afternoon",
            "Good evening",
            "Goodbye"
          ),
          correctOptionIndex = 0,
          translation = "Good morning",
          pronunciation = "BWAY-nohs DEE-ahs",
          contextExplanation = "'Buenos días' is greeting during morning hours."
        )
      )

      "fr" -> listOf(
        ListeningExercise(
          id = "fr_lis_1",
          spokenPhrase = "Merci beaucoup",
          promptQuestion = "What does the French phrase mean?",
          options = listOf(
            "You are welcome",
            "Thank you very much",
            "Excuse me please",
            "Have a great day"
          ),
          correctOptionIndex = 1,
          translation = "Thank you very much",
          pronunciation = "mair-SEE boh-KOO",
          contextExplanation = "'Merci' = thank you, 'beaucoup' = much/a lot."
        ),
        ListeningExercise(
          id = "fr_lis_2",
          spokenPhrase = "Un café, s'il vous plaît",
          promptQuestion = "What item is the speaker requesting?",
          options = listOf(
            "A glass of water",
            "A train ticket",
            "A coffee, please",
            "The restaurant menu"
          ),
          correctOptionIndex = 2,
          translation = "A coffee, please",
          pronunciation = "uhn kah-FAY, seel voo pleh",
          contextExplanation = "Classic French order phrase at cafés."
        )
      )

      "de" -> listOf(
        ListeningExercise(
          id = "de_lis_1",
          spokenPhrase = "Guten Tag",
          promptQuestion = "What German greeting did you hear?",
          options = listOf(
            "Good day / Hello",
            "Good night",
            "Goodbye",
            "See you later"
          ),
          correctOptionIndex = 0,
          translation = "Good day / Hello",
          pronunciation = "GOO-ten tahk",
          contextExplanation = "Standard daytime greeting throughout Germany, Austria, and Switzerland."
        ),
        ListeningExercise(
          id = "de_lis_2",
          spokenPhrase = "Danke schön",
          promptQuestion = "What is the polite phrase meaning?",
          options = listOf(
            "You're welcome",
            "Thank you very much",
            "I'm sorry",
            "Please repeat"
          ),
          correctOptionIndex = 1,
          translation = "Thank you very much",
          pronunciation = "DAHN-keh shurn",
          contextExplanation = "'Danke' = thanks, 'schön' = nicely/beautifully, together 'thank you very much'."
        )
      )

      else -> listOf(
        ListeningExercise(
          id = "${languageId}_lis_1",
          spokenPhrase = LessonCatalog.getSampleLessonForLanguage(languageId).phrases.firstOrNull()?.original ?: "Hello",
          promptQuestion = "Listen to the audio. What did the speaker say?",
          options = listOf(
            LessonCatalog.getSampleLessonForLanguage(languageId).phrases.firstOrNull()?.translation ?: "Hello",
            "Goodbye, have a good day",
            "Where is the hotel?",
            "Can you help me please?"
          ),
          correctOptionIndex = 0,
          translation = LessonCatalog.getSampleLessonForLanguage(languageId).phrases.firstOrNull()?.translation ?: "Hello",
          pronunciation = LessonCatalog.getSampleLessonForLanguage(languageId).phrases.firstOrNull()?.pronunciation ?: "",
          contextExplanation = LessonCatalog.getSampleLessonForLanguage(languageId).phrases.firstOrNull()?.contextNote ?: ""
        )
      )
    }
  }
}
