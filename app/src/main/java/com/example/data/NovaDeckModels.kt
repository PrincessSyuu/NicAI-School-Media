package com.example.data

import androidx.compose.ui.graphics.Color

enum class SlideTheme(
    val displayName: String,
    val tagline: String,
    val isProFeature: Boolean,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val accentColor: Color,
    val secondaryAccent: Color,
    val titleColor: Color,
    val bodyColor: Color
) {
    MINIMALIST_TECH(
        displayName = "Minimalist Tech",
        tagline = "Warm alabaster & electric indigo",
        isProFeature = false,
        backgroundColor = Color(0xFFF6F8FC),
        surfaceColor = Color(0xFFFFFFFF),
        accentColor = Color(0xFF6366F1),
        secondaryAccent = Color(0xFF06B6D4),
        titleColor = Color(0xFF0F172A),
        bodyColor = Color(0xFF334155)
    ),
    CLEAN_ACADEMIC(
        displayName = "Clean Academic",
        tagline = "Calm paper & mint-emerald clarity",
        isProFeature = false,
        backgroundColor = Color(0xFFFAF8F2),
        surfaceColor = Color(0xFFFFFFFF),
        accentColor = Color(0xFF059669),
        secondaryAccent = Color(0xFF06B6D4),
        titleColor = Color(0xFF14281D),
        bodyColor = Color(0xFF374151)
    ),
    DARK_NEON(
        displayName = "Dark Neon",
        tagline = "Obsidian slate & aqua-violet glow",
        isProFeature = true,
        backgroundColor = Color(0xFF0B0F19),
        surfaceColor = Color(0xFF121826),
        accentColor = Color(0xFF22D3EE),
        secondaryAccent = Color(0xFF8B5CF6),
        titleColor = Color(0xFFF3F6FC),
        bodyColor = Color(0xFFC2CEE6)
    ),
    EXECUTIVE_PITCH(
        displayName = "Executive Pitch",
        tagline = "Obsidian charcoal & champagne gold",
        isProFeature = true,
        backgroundColor = Color(0xFF121318),
        surfaceColor = Color(0xFF1C1E26),
        accentColor = Color(0xFFFFB84D),
        secondaryAccent = Color(0xFF38BDF8),
        titleColor = Color(0xFFFAFAFC),
        bodyColor = Color(0xFFD1D5DB)
    )
}

enum class SlideLayoutType(val label: String) {
    TITLE("Title Slide"),
    KEY_POINTS("Key Points"),
    COMPARISON("Comparison"),
    SUMMARY("Summary"),
    QA("Q & A")
}

data class SlideItem(
    val id: String,
    val layoutType: SlideLayoutType,
    val heading: String,
    val subheading: String = "",
    val bulletPoints: List<String> = emptyList(),
    val leftColumnTitle: String = "",
    val leftColumnPoints: List<String> = emptyList(),
    val rightColumnTitle: String = "",
    val rightColumnPoints: List<String> = emptyList(),
    val speakerNotes: String = ""
)

data class PresentationDeck(
    val id: String,
    val title: String,
    val subtitle: String,
    val audience: String,
    val theme: SlideTheme,
    val slides: List<SlideItem>,
    val isAiGenerated: Boolean = false
)

enum class GradeLevel(val label: String, val badge: String) {
    SMP("SMP (Grades 7–9)", "SMP"),
    SMA("SMA (Grades 10–12)", "SMA"),
    PRO_LIFE("Young Pro & Study Skills", "PRO/SKILL")
}

enum class StudyDifficulty(val label: String) {
    GENTLE_START("Gentle Start"),
    CORE("Core"),
    CHALLENGE("Challenge")
}

enum class ResourceSourceType(val label: String) {
    OFFICIAL_OPEN_EDU("Official Open Edu"),
    YOUTUBE_EDU("YouTube Edu Channel")
}

data class CuratedResource(
    val title: String,
    val sourceName: String,
    val sourceType: ResourceSourceType,
    val attributionNote: String,
    val url: String,
    val durationLabel: String
)

data class MiniCheckQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val encouragingFeedback: String
)

data class StudyModule(
    val id: String,
    val title: String,
    val subject: String,
    val gradeLevel: GradeLevel,
    val difficulty: StudyDifficulty,
    val estimatedMinutes: Int,
    val goal: String,
    val tldrConcept: String,
    val walkthroughSteps: List<String>,
    val miniChecks: List<MiniCheckQuestion>,
    val curatedResources: List<CuratedResource>
)

enum class NicAiMode(
    val label: String,
    val shortHint: String
) {
    SMP_TUTOR("SMP Tutor", "Calm, simple steps for Grades 7–9"),
    SMA_TUTOR("SMA Tutor", "Clear formulas & exam prep for Grades 10–12"),
    SLIDE_COACH("Slide & Office Coach", "Crisp outlines, pitches & summaries")
}

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val isStreaming: Boolean = false,
    val mode: NicAiMode = NicAiMode.SMP_TUTOR,
    val suggestedDeckTopic: String? = null
)

data class PartnerBoost(
    val id: String,
    val title: String,
    val partnerName: String,
    val badge: String,
    val description: String,
    val targetGrade: String
)
