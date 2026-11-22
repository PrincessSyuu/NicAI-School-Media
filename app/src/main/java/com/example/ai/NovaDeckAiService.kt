package com.example.ai

import android.util.Log
import com.example.data.ChatMessage
import com.example.data.NicAiMode
import com.example.data.PresentationDeck
import com.example.data.SlideItem
import com.example.data.SlideLayoutType
import com.example.data.SlideTheme
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.UUID

private const val TAG = "NovaDeckAiService"
private const val DEFAULT_MODEL = "gemini-3.8-flash"

class NovaDeckAiService {

    private val nicAiSystemInstruction = content {
        text(
            """
            You are NicAI, the custom AI study tutor and presentation coach inside NovaDeck (created by Bert Developer).
            Your users are primarily Junior High (SMP, ages 12-15) and High School (SMA, ages 15-18) students who may feel overwhelmed by textbooks, plus young professionals / office users building quick decks.
            Rules:
            - Be warm, patient, encouraging, and crystal clear — never condescending and never dump giant walls of text.
            - Break explanations into short numbered steps, bullet points, or everyday analogies.
            - When asked for study help, end with one quick friendly check-in question or study tip.
            - When asked for slide content or office productivity help, produce crisp, scannable slide bullets (max 10-12 words per bullet).
            - Match the user's language (English or Indonesian) naturally.
            """.trimIndent()
        )
    }

    private val nicAiModel by lazy {
        Firebase.ai.generativeModel(
            modelName = DEFAULT_MODEL,
            systemInstruction = nicAiSystemInstruction
        )
    }

    private val slideSchema = Schema.obj(
        properties = mapOf(
            "deckTitle" to Schema.string(description = "Short, punchy title of the presentation deck"),
            "subtitle" to Schema.string(description = "One-line engaging subtitle for the deck"),
            "slides" to Schema.array(
                description = "Ordered list of slides in the deck",
                items = Schema.obj(
                    properties = mapOf(
                        "layoutType" to Schema.enumeration(
                            values = listOf("TITLE", "KEY_POINTS", "COMPARISON", "SUMMARY", "QA"),
                            description = "Layout type for this slide"
                        ),
                        "heading" to Schema.string(description = "Concise slide headline (max 8 words)"),
                        "subheading" to Schema.string(description = "Short supporting lead-in line"),
                        "bulletPoints" to Schema.array(
                            items = Schema.string(),
                            description = "3 to 4 short, clear bullet points (used for TITLE, KEY_POINTS, SUMMARY, QA)"
                        ),
                        "leftColumnTitle" to Schema.string(description = "Left column header for COMPARISON layout"),
                        "leftColumnPoints" to Schema.array(
                            items = Schema.string(),
                            description = "2 to 3 concise points for left column in COMPARISON layout"
                        ),
                        "rightColumnTitle" to Schema.string(description = "Right column header for COMPARISON layout"),
                        "rightColumnPoints" to Schema.array(
                            items = Schema.string(),
                            description = "2 to 3 concise points for right column in COMPARISON layout"
                        ),
                        "speakerNotes" to Schema.string(description = "1-2 friendly presenter cue sentences")
                    ),
                    optionalProperties = listOf(
                        "subheading",
                        "bulletPoints",
                        "leftColumnTitle",
                        "leftColumnPoints",
                        "rightColumnTitle",
                        "rightColumnPoints",
                        "speakerNotes"
                    )
                )
            )
        )
    )

    private val slideDeckModel by lazy {
        Firebase.ai.generativeModel(
            modelName = DEFAULT_MODEL,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = slideSchema
            }
        )
    }

    fun streamNicAiResponse(
        userPrompt: String,
        mode: NicAiMode,
        recentHistory: List<ChatMessage>
    ): Flow<String> = flow {
        val modeContext = when (mode) {
            NicAiMode.SMP_TUTOR -> "[Active Mode: SMP Tutor — Grades 7-9, extra gentle analogies, simple math/science steps]"
            NicAiMode.SMA_TUTOR -> "[Active Mode: SMA Tutor — Grades 10-12, structured formulas, exam-ready reasoning]"
            NicAiMode.SLIDE_COACH -> "[Active Mode: Slide & Office Coach — sharp presentation structure, crisp bullets, executive clarity]"
        }

        val chatHistory = recentHistory
            .filter { !it.isStreaming && it.text.isNotBlank() }
            .takeLast(8)
            .map { msg ->
                content(if (msg.isUser) "user" else "model") {
                    text(msg.text)
                }
            }

        val chat = nicAiModel.startChat(history = chatHistory)
        val fullPrompt = "$modeContext\n\nUser request: $userPrompt"

        val accumulated = StringBuilder()
        withTimeout(60_000L) {
            chat.sendMessageStream(fullPrompt).collect { chunk ->
                val chunkText = chunk.text.orEmpty()
                if (chunkText.isNotEmpty()) {
                    accumulated.append(chunkText)
                    emit(accumulated.toString())
                }
            }
        }
        if (accumulated.isBlank()) {
            throw IllegalStateException("NicAI returned an empty response. Please try again.")
        }
    }

    suspend fun generateStructuredDeck(
        topicOrNotes: String,
        audience: String,
        slideCount: Int,
        theme: SlideTheme
    ): PresentationDeck {
        val prompt = """
            Create a clear, engaging $slideCount-slide presentation for $audience based on this topic or rough notes:
            "$topicOrNotes"

            Structure requirements:
            - Slide 1 must use layoutType "TITLE".
            - Include a mix of "KEY_POINTS" and at least one "COMPARISON" slide (with populated leftColumnTitle, leftColumnPoints, rightColumnTitle, rightColumnPoints) where helpful.
            - Include a "SUMMARY" slide near the end, and a "QA" slide as the final slide if slideCount >= 5.
            - Keep bullet points crisp, teen-friendly, and scannable (under 14 words each, 3-4 bullets per slide).
            - Match the user's language (English or Indonesian).
        """.trimIndent()

        val response = withTimeout(60_000L) {
            slideDeckModel.generateContent(prompt)
        }

        val jsonText = response.text
        if (jsonText.isNullOrBlank()) {
            Log.e(TAG, "Structured slide generation returned empty text.")
            throw IllegalStateException("AI returned an empty slide deck. Please try again.")
        }

        val json = JSONObject(jsonText)
        val deckTitle = json.optString("deckTitle", topicOrNotes.take(40)).ifBlank { "Untitled Deck" }
        val subtitle = json.optString("subtitle", "Generated with NovaDeck Slide Studio")
        val slidesJson = json.getJSONArray("slides")

        val slides = (0 until slidesJson.length()).map { idx ->
            val obj = slidesJson.getJSONObject(idx)
            val layoutStr = obj.optString("layoutType", "KEY_POINTS")
            val layout = SlideLayoutType.entries.firstOrNull { it.name == layoutStr }
                ?: SlideLayoutType.KEY_POINTS

            val bullets = obj.optJSONArray("bulletPoints")?.let { arr ->
                (0 until arr.length()).mapNotNull { i -> arr.optString(i).takeIf { it.isNotBlank() } }
            } ?: emptyList()

            val leftPoints = obj.optJSONArray("leftColumnPoints")?.let { arr ->
                (0 until arr.length()).mapNotNull { i -> arr.optString(i).takeIf { it.isNotBlank() } }
            } ?: emptyList()

            val rightPoints = obj.optJSONArray("rightColumnPoints")?.let { arr ->
                (0 until arr.length()).mapNotNull { i -> arr.optString(i).takeIf { it.isNotBlank() } }
            } ?: emptyList()

            SlideItem(
                id = "slide_${UUID.randomUUID().toString().take(8)}",
                layoutType = layout,
                heading = obj.optString("heading", "Slide ${idx + 1}"),
                subheading = obj.optString("subheading", ""),
                bulletPoints = bullets,
                leftColumnTitle = obj.optString("leftColumnTitle", "Option A"),
                leftColumnPoints = leftPoints,
                rightColumnTitle = obj.optString("rightColumnTitle", "Option B"),
                rightColumnPoints = rightPoints,
                speakerNotes = obj.optString("speakerNotes", "")
            )
        }

        if (slides.isEmpty()) {
            throw IllegalStateException("No slides were generated. Try adding a little more detail.")
        }

        return PresentationDeck(
            id = "deck_${UUID.randomUUID().toString().take(8)}",
            title = deckTitle,
            subtitle = subtitle,
            audience = audience,
            theme = theme,
            slides = slides,
            isAiGenerated = true
        )
    }
}
