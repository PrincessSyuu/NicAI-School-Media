package com.example.data

import android.util.Log
import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.coroutines.CancellationException
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(
    exception: Throwable,
    operationType: OperationType,
    path: String?
): String {
    val causes = generateSequence(exception) { it.cause }
    val rootError = causes.firstOrNull { it is FirebaseFirestoreException }
        ?: causes.firstOrNull { it !is CancellationException }
        ?: exception

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", rootError.message ?: rootError.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

@Keep
@IgnoreExtraProperties
data class CloudUserProfile(
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val accountType: String = "google_personal",
    val proActive: Boolean = false,
    val completedModuleIds: List<String> = emptyList(),
    val dailyPromptsUsed: Long = 0L,
    val promptDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CloudDeckDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val audience: String = "",
    val themeName: String = SlideTheme.DARK_NEON.name,
    val aiGenerated: Boolean = false,
    val slides: List<Map<String, Any?>> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toPresentationDeck(): PresentationDeck {
        val resolvedTheme = SlideTheme.entries.firstOrNull { it.name == themeName }
            ?: SlideTheme.DARK_NEON

        val parsedSlides = slides.mapIndexed { idx, map ->
            val layoutRaw = map["layoutType"] as? String ?: "KEY_POINTS"
            val layout = SlideLayoutType.entries.firstOrNull { it.name == layoutRaw }
                ?: SlideLayoutType.KEY_POINTS

            SlideItem(
                id = (map["id"] as? String)?.takeIf { it.isNotBlank() } ?: "s_${idx + 1}",
                layoutType = layout,
                heading = map["heading"] as? String ?: "Slide ${idx + 1}",
                subheading = map["subheading"] as? String ?: "",
                bulletPoints = (map["bulletPoints"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                leftColumnTitle = map["leftColumnTitle"] as? String ?: "",
                leftColumnPoints = (map["leftColumnPoints"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                rightColumnTitle = map["rightColumnTitle"] as? String ?: "",
                rightColumnPoints = (map["rightColumnPoints"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                speakerNotes = map["speakerNotes"] as? String ?: ""
            )
        }

        return PresentationDeck(
            id = id,
            title = title.ifBlank { "Untitled Deck" },
            subtitle = subtitle,
            audience = audience.ifBlank { "Students" },
            theme = resolvedTheme,
            slides = parsedSlides.ifEmpty {
                listOf(
                    SlideItem(
                        id = "s_1",
                        layoutType = SlideLayoutType.TITLE,
                        heading = title.ifBlank { "Untitled Deck" },
                        subheading = subtitle
                    )
                )
            },
            isAiGenerated = aiGenerated
        )
    }
}

fun PresentationDeck.toSlideMaps(): List<Map<String, Any>> =
    slides.take(15).map { slide ->
        mapOf(
            "id" to slide.id,
            "layoutType" to slide.layoutType.name,
            "heading" to slide.heading.take(160),
            "subheading" to slide.subheading.take(240),
            "bulletPoints" to slide.bulletPoints.take(8).map { it.take(200) },
            "leftColumnTitle" to slide.leftColumnTitle.take(80),
            "leftColumnPoints" to slide.leftColumnPoints.take(6).map { it.take(160) },
            "rightColumnTitle" to slide.rightColumnTitle.take(80),
            "rightColumnPoints" to slide.rightColumnPoints.take(6).map { it.take(160) },
            "speakerNotes" to slide.speakerNotes.take(400)
        )
    }
