package com.example.data

enum class QuestionType {
  MULTIPLE_CHOICE,
  FILL_IN_THE_BLANK
}

data class LessonVocabulary(
  val id: String,
  val term: String,
  val phonetic: String,
  val translation: String,
  val exampleSentence: String,
  val exampleTranslation: String,
  val audioText: String,
  val culturalNote: String
)

data class LessonQuestion(
  val id: String,
  val type: QuestionType,
  val prompt: String,
  val contextSentence: String? = null,
  val options: List<String>,
  val correctAnswer: String,
  val explanation: String
)

data class CourseLesson(
  val id: String,
  val title: String,
  val subtitle: String,
  val description: String,
  val durationMinutes: Int,
  val xpReward: Int,
  val vocabulary: List<LessonVocabulary>,
  val questions: List<LessonQuestion>
)

data class CourseUnit(
  val id: String,
  val unitNumber: Int,
  val title: String,
  val description: String,
  val isUnlocked: Boolean,
  val lessons: List<CourseLesson>
)

data class Course(
  val languageId: String,
  val title: String,
  val subtitle: String,
  val level: String,
  val units: List<CourseUnit>
)

object CourseCatalog {

  /**
   * Verified Spanish Beginner Course.
   * Spanish is the first verified course implementation.
   * Other languages return null to show a transparent coming-soon course message.
   */
  private val spanishCourse: Course = Course(
    languageId = "es",
    title = "Spanish Foundations (A1)",
    subtitle = "Conversational Fluency for Beginners",
    level = "Beginner • CEFR A1",
    units = listOf(
      CourseUnit(
        id = "es_unit_1",
        unitNumber = 1,
        title = "Everyday Greetings",
        description = "Master essential greetings, polite courtesies, and conversational warmups in Spanish.",
        isUnlocked = true,
        lessons = listOf(
          CourseLesson(
            id = "es_u1_l1",
            title = "Everyday Greetings",
            subtitle = "5 Core Greetings & Practice",
            description = "Learn how to greet people naturally at any time of day, express gratitude, and introduce yourself.",
            durationMinutes = 5,
            xpReward = 30,
            vocabulary = listOf(
              LessonVocabulary(
                id = "es_v1",
                term = "¡Hola!",
                phonetic = "OH-lah",
                translation = "Hello / Hi",
                exampleSentence = "¡Hola! ¿Cómo estás hoy?",
                exampleTranslation = "Hello! How are you today?",
                audioText = "¡Hola!",
                culturalNote = "The most universal, friendly greeting across the entire Spanish-speaking world."
              ),
              LessonVocabulary(
                id = "es_v2",
                term = "Buenos días",
                phonetic = "BWEH-nos DEE-ahs",
                translation = "Good morning",
                exampleSentence = "Buenos días, un café por favor.",
                exampleTranslation = "Good morning, a coffee please.",
                audioText = "Buenos días",
                culturalNote = "Used politely from early morning until the afternoon meal around noon or 1 PM."
              ),
              LessonVocabulary(
                id = "es_v3",
                term = "Buenas tardes",
                phonetic = "BWEH-nas TAR-des",
                translation = "Good afternoon",
                exampleSentence = "Buenas tardes, ¿cómo le va?",
                exampleTranslation = "Good afternoon, how is it going?",
                audioText = "Buenas tardes",
                culturalNote = "Used from the early afternoon until sunset or dinner time."
              ),
              LessonVocabulary(
                id = "es_v4",
                term = "Muchas gracias",
                phonetic = "MOO-chas GRAH-syas",
                translation = "Thank you very much",
                exampleSentence = "Muchas gracias por tu ayuda.",
                exampleTranslation = "Thank you very much for your help.",
                audioText = "Muchas gracias",
                culturalNote = "Essential courtesy expression showing warmth and appreciation."
              ),
              LessonVocabulary(
                id = "es_v5",
                term = "Mucho gusto",
                phonetic = "MOO-choh GOOS-toh",
                translation = "Nice to meet you",
                exampleSentence = "Mucho gusto en conocerte.",
                exampleTranslation = "It is a pleasure to meet you.",
                audioText = "Mucho gusto",
                culturalNote = "Literally means 'much pleasure', customary when introduced to someone new."
              )
            ),
            questions = listOf(
              LessonQuestion(
                id = "q1",
                type = QuestionType.MULTIPLE_CHOICE,
                prompt = "Which greeting means 'Good morning' in Spanish?",
                options = listOf("Buenos días", "Buenas tardes", "Buenas noches", "Mucho gusto"),
                correctAnswer = "Buenos días",
                explanation = "'Buenos días' is used in the morning until midday. Notice that 'días' is masculine plural, so it uses 'buenos'."
              ),
              LessonQuestion(
                id = "q2",
                type = QuestionType.FILL_IN_THE_BLANK,
                prompt = "Complete the courtesy phrase: 'Muchas ______' (Thank you very much)",
                contextSentence = "Muchas ______",
                options = listOf("gracias", "tardes", "días", "hola"),
                correctAnswer = "gracias",
                explanation = "'Muchas gracias' is the standard Spanish phrase for 'thank you very much' (literally 'many thanks')."
              ),
              LessonQuestion(
                id = "q3",
                type = QuestionType.MULTIPLE_CHOICE,
                prompt = "When meeting someone for the first time, what polite phrase means 'Nice to meet you'?",
                options = listOf("Mucho gusto", "De nada", "Por favor", "Hasta luego"),
                correctAnswer = "Mucho gusto",
                explanation = "'Mucho gusto' literally means 'much pleasure' and is the most common way to say 'pleased to meet you'."
              ),
              LessonQuestion(
                id = "q4",
                type = QuestionType.FILL_IN_THE_BLANK,
                prompt = "Fill in the missing greeting: '¡______! ¿Cómo estás?' (Hello! How are you?)",
                contextSentence = "¡______! ¿Cómo estás?",
                options = listOf("Hola", "Adiós", "Gracias", "Noches"),
                correctAnswer = "Hola",
                explanation = "'¡Hola!' is the universal Spanish greeting for 'Hello' or 'Hi', suitable in almost any situation."
              ),
              LessonQuestion(
                id = "q5",
                type = QuestionType.MULTIPLE_CHOICE,
                prompt = "It is 3:30 PM. What is the most natural greeting to use?",
                options = listOf("Buenas tardes", "Buenos días", "Buenas noches", "Hasta mañana"),
                correctAnswer = "Buenas tardes",
                explanation = "'Buenas tardes' is used from early afternoon until the evening sunset."
              ),
              LessonQuestion(
                id = "q6",
                type = QuestionType.FILL_IN_THE_BLANK,
                prompt = "Complete the greeting for afternoon: 'Buenas ______'",
                contextSentence = "Buenas ______",
                options = listOf("tardes", "días", "gusto", "gracias"),
                correctAnswer = "tardes",
                explanation = "'Tardes' is feminine plural, so it pairs with 'buenas' to make 'Buenas tardes' (Good afternoon)."
              )
            )
          )
        )
      ),
      CourseUnit(
        id = "es_unit_2",
        unitNumber = 2,
        title = "At the Café & Dining",
        description = "Order food and drinks, ask for the bill, and express culinary preferences.",
        isUnlocked = false,
        lessons = emptyList()
      ),
      CourseUnit(
        id = "es_unit_3",
        unitNumber = 3,
        title = "Getting Around & Directions",
        description = "Navigate transit, ask where places are located, and check into accommodations.",
        isUnlocked = false,
        lessons = emptyList()
      )
    )
  )

  /**
   * Retrieves course content for the target language.
   * Returns verified Spanish course for "es".
   * Returns null for languages whose courses are currently in development,
   * guaranteeing that no incorrect mock translations or fake lessons are presented.
   */
  fun getCourseForLanguage(languageId: String): Course? {
    return if (languageId.equals("es", ignoreCase = true)) {
      spanishCourse
    } else {
      null
    }
  }
}
