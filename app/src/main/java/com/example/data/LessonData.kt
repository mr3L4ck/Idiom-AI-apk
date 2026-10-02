package com.example.data

data class PhraseCard(
  val original: String,
  val pronunciation: String,
  val translation: String,
  val contextNote: String
)

data class SampleLesson(
  val id: String,
  val title: String,
  val category: String,
  val durationMinutes: Int,
  val xpReward: Int,
  val phrases: List<PhraseCard>
)

object LessonCatalog {

  fun getSampleLessonForLanguage(languageId: String): SampleLesson {
    return when (languageId.lowercase()) {
      "es" -> SampleLesson(
        id = "es_lesson_1",
        title = "Essential Greetings & First Words",
        category = "Conversation Basics",
        durationMinutes = 5,
        xpReward = 20,
        phrases = listOf(
          PhraseCard("¡Hola! ¿Cómo estás?", "OH-lah, KOH-moh ess-TAHS?", "Hello! How are you?", "Casual greeting used at any time of day"),
          PhraseCard("Mucho gusto en conocerte", "MOO-choh GOOS-toh en koh-noh-SEHR-teh", "Nice to meet you", "Common when introduced to someone new"),
          PhraseCard("Por favor y gracias", "por fah-VOR ee GRAH-syahs", "Please and thank you", "Polite courtesy expressions"),
          PhraseCard("¿Hablas inglés?", "AH-blahs een-GLEHS?", "Do you speak English?", "Helpful when traveling in Spanish-speaking areas")
        )
      )
      "fr" -> SampleLesson(
        id = "fr_lesson_1",
        title = "Café Culture & Friendly Greetings",
        category = "Daily Conversations",
        durationMinutes = 5,
        xpReward = 20,
        phrases = listOf(
          PhraseCard("Bonjour, comment allez-vous?", "bon-ZHOOR, koh-mahn tah-lay VOO?", "Good day, how are you?", "Respectful universal French greeting"),
          PhraseCard("Un café, s'il vous plaît", "uhn kah-FAY, seel voo pleh", "A coffee, please", "Everyday phrase for cafés and bistros"),
          PhraseCard("Merci beaucoup", "mair-SEE boh-KOO", "Thank you very much", "Warm way to express gratitude"),
          PhraseCard("Bonne journée!", "bun zhoor-NAY!", "Have a wonderful day!", "Polite parting phrase")
        )
      )
      "de" -> SampleLesson(
        id = "de_lesson_1",
        title = "Greetings & Courtesies in German",
        category = "Basics",
        durationMinutes = 5,
        xpReward = 20,
        phrases = listOf(
          PhraseCard("Guten Tag, wie geht es Ihnen?", "GOO-ten tahk, vee gayt es EE-nen?", "Good day, how are you?", "Polite formal greeting"),
          PhraseCard("Freut mich, Sie kennenzulernen", "FROYT mikh, zee KEN-en-tsoo-lehr-nen", "Pleased to meet you", "Used upon introduction"),
          PhraseCard("Bitte und danke schön", "BIT-teh oont DAHN-keh shurn", "Please and thank you very much", "Essential daily manners"),
          PhraseCard("Auf Wiedersehen!", "owf VEE-der-zayn!", "Goodbye! (See you again)", "Standard polite goodbye")
        )
      )
      "ja" -> SampleLesson(
        id = "ja_lesson_1",
        title = "First Steps: Japanese Politeness",
        category = "Social Etiquette",
        durationMinutes = 5,
        xpReward = 25,
        phrases = listOf(
          PhraseCard("こんにちは (Konnichiwa)", "kohn-nee-chee-wah", "Hello / Good afternoon", "Standard greeting used during daylight hours"),
          PhraseCard("はじめまして (Hajimemashite)", "hah-jee-meh-mahsh-teh", "Nice to meet you (first time)", "Said when meeting someone for the first time"),
          PhraseCard("ありがとうございます (Arigatou gozaimasu)", "ah-ree-gah-toh goh-zeye-mahs", "Thank you very much", "Polite and respectful gratitude"),
          PhraseCard("すみません (Sumimasen)", "soo-mee-mah-sen", "Excuse me / I'm sorry", "Used to get attention or apologize")
        )
      )
      "ko" -> SampleLesson(
        id = "ko_lesson_1",
        title = "Everyday Hangul Greetings",
        category = "Beginner Korean",
        durationMinutes = 5,
        xpReward = 20,
        phrases = listOf(
          PhraseCard("안녕하세요 (Annyeonghaseyo)", "ahn-nyong-hah-seh-yoh", "Hello / Peace be with you", "Most common polite greeting"),
          PhraseCard("반갑습니다 (Bangapseumnida)", "bahn-gahp-seum-nee-dah", "Nice to meet you", "Respectful introductory phrase"),
          PhraseCard("감사합니다 (Gamsahamnida)", "gahm-sah-hahm-nee-dah", "Thank you", "Essential polite phrase"),
          PhraseCard("또 만나요! (Tto mannayo!)", "ttoh mahn-nah-yoh", "See you again!", "Friendly farewell")
        )
      )
      "ar" -> SampleLesson(
        id = "ar_lesson_1",
        title = "Warm Arabic Welcomes",
        category = "Cultural Basics",
        durationMinutes = 5,
        xpReward = 25,
        phrases = listOf(
          PhraseCard("مرحباً بك (Marhaban bik)", "MAR-hah-bahn beek", "Welcome / Hello", "Friendly greeting to welcome someone"),
          PhraseCard("السلام عليكم (As-salamu alaykum)", "as-sah-LAH-moo ah-LAY-koom", "Peace be upon you", "Traditional Arabic greeting"),
          PhraseCard("شكراً جزيلاً (Shukran jazilan)", "SHOOK-rahn jah-ZEE-lahn", "Thank you very much", "Expressing heartfelt thanks"),
          PhraseCard("مع السلامة (Ma'a as-salama)", "mah-ah as-sah-LAH-mah", "Goodbye (Go with peace)", "Polite departure phrase")
        )
      )
      "hi" -> SampleLesson(
        id = "hi_lesson_1",
        title = "Namaste: Welcoming Greetings",
        category = "Spoken Hindi",
        durationMinutes = 5,
        xpReward = 20,
        phrases = listOf(
          PhraseCard("नमस्ते, आप कैसे हैं? (Namaste, aap kaise hain?)", "nuh-muh-STAY, aap KAY-say hain?", "Hello, how are you?", "Respectful universal Hindi greeting"),
          PhraseCard("आपसे मिलकर खुशी हुई (Aapse milkar khushi hui)", "aap-say mil-kar KHOO-shee hoo-ee", "Pleased to meet you", "Courteous introduction"),
          PhraseCard("धन्यवाद (Dhanyavaad)", "dhun-yuh-VAAD", "Thank you", "Expressing gratitude"),
          PhraseCard("फिर मिलेंगे! (Phir milenge!)", "feer mil-ENG-gay", "See you again!", "Warm farewell")
        )
      )
      "zh" -> SampleLesson(
        id = "zh_lesson_1",
        title = "Mandarin Tones & Basic Greetings",
        category = "Foundations",
        durationMinutes = 5,
        xpReward = 25,
        phrases = listOf(
          PhraseCard("你好！很高兴认识你 (Nǐ hǎo! Hěn gāoxìng rènshí nǐ)", "nee how! hen gaow-shing ren-shr nee", "Hello! Very happy to meet you", "Friendly first introduction"),
          PhraseCard("谢谢您 (Xièxie nín)", "sheh-sheh neen", "Thank you (polite/respectful)", "Respectful gratitude"),
          PhraseCard("没问题 (Méi wèntí)", "may wen-tee", "No problem / You're welcome", "Common conversational reply"),
          PhraseCard("再见！ (Zàijiàn!)", "zeye-jyen", "Goodbye! (See you again)", "Standard farewell")
        )
      )
      "hy" -> SampleLesson(
        id = "hy_lesson_1",
        title = "Discovering Armenian: First Words",
        category = "Heritage & Culture",
        durationMinutes = 5,
        xpReward = 20,
        phrases = listOf(
          PhraseCard("Բարև ձեզ, ինչպե՞ս եք (Barev dzez, inchpes ek?)", "bah-REV dzez, eench-PES ek?", "Hello, how are you?", "Respectful Armenian greeting"),
          PhraseCard("Շատ ուրախ եմ (Shat urakh em)", "shaht oo-RAKH em", "Very pleased (to meet you)", "Warm introductory response"),
          PhraseCard("Շնորհակալություն (Shnorhakalutyun)", "shnor-hah-kah-loot-YOON", "Thank you", "Expressing sincere thanks"),
          PhraseCard("Հաջողություն (Hajoghutyun)", "hah-joh-ghoot-YOON", "Success / Goodbye", "Wishing good fortune upon parting")
        )
      )
      "pt" -> SampleLesson(
        id = "pt_lesson_1",
        title = "Warm Greetings in Portuguese",
        category = "Conversation Starters",
        durationMinutes = 5,
        xpReward = 20,
        phrases = listOf(
          PhraseCard("Olá, tudo bem com você?", "oh-LAH, TOO-doo beng kom voh-SAY?", "Hello, is everything good with you?", "Most common Brazilian & Portuguese greeting"),
          PhraseCard("Prazer em conhecer você", "prah-ZAIR eng koh-nyeh-SEHR voh-SAY", "Pleasure to meet you", "Friendly introduction"),
          PhraseCard("Muito obrigado / obrigada", "MOO-eet-oh oh-bree-GAH-doo", "Thank you very much", "Standard expression of gratitude"),
          PhraseCard("Tenha um ótimo dia!", "TEN-yah oom OH-tee-moo JEE-ah", "Have a great day!", "Friendly parting")
        )
      )
      "en" -> SampleLesson(
        id = "en_lesson_1",
        title = "Everyday English Conversations",
        category = "Global English",
        durationMinutes = 5,
        xpReward = 20,
        phrases = listOf(
          PhraseCard("Hi there, how is your day going?", "hye thair, how iz yor day goh-ing?", "Friendly daily greeting", "Casual greeting between peers"),
          PhraseCard("It's great to connect with you", "its grayt too kuh-nekt with yoo", "Pleased to meet you", "Warm professional and personal introduction"),
          PhraseCard("Thank you for your help", "thangk yoo for yor help", "Polite expression of thanks", "Essential polite gratitude"),
          PhraseCard("Catch you later!", "kach yoo lay-ter", "See you later!", "Casual friendly goodbye")
        )
      )
      else -> SampleLesson(
        id = "general_lesson_1",
        title = "Essential Phrases & Greetings",
        category = "Beginner Foundations",
        durationMinutes = 5,
        xpReward = 20,
        phrases = listOf(
          PhraseCard("Welcome to ${BrandConfig.APP_NAME}!", "wel-kum", "Warm greetings", "Starting your language journey"),
          PhraseCard("Please and thank you", "pleez and thangk yoo", "Everyday courtesy", "Core manners in any culture"),
          PhraseCard("How are you today?", "how ahr yoo too-day", "Conversational opener", "Great way to connect with speakers")
        )
      )
    }
  }
}
