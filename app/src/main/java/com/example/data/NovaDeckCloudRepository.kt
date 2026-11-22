package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NovaDeckCloudRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("User must sign in with Google before accessing cloud storage.")

    private fun todayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun classifyAccountType(email: String?): String {
        val normalized = email.orEmpty().trim().lowercase(Locale.US)
        return if (normalized.endsWith("@gmail.com") || normalized.endsWith("@googlemail.com") || normalized.isBlank()) {
            "google_personal"
        } else {
            "school_workspace"
        }
    }

    suspend fun ensureUserProfile(user: FirebaseUser): Result<Unit> {
        val uid = user.uid
        val docPath = "users/$uid"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid) { "Signed-in user changed; refusing cross-user write." }
            val docRef = db.collection("users").document(uid)
            val snapshot = docRef.get().await()

            val email = (user.email ?: "student@google.account").take(254)
            val displayName = (user.displayName?.takeIf { it.isNotBlank() }
                ?: email.substringBefore('@').ifBlank { "NovaDeck Learner" }).take(120)
            val accountType = classifyAccountType(email)

            if (!snapshot.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "email" to email,
                    "displayName" to displayName,
                    "accountType" to accountType,
                    "proActive" to false,
                    "completedModuleIds" to emptyList<String>(),
                    "dailyPromptsUsed" to 0L,
                    "promptDate" to todayKey(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } else {
                docRef.update(
                    mapOf(
                        "email" to email,
                        "displayName" to displayName,
                        "accountType" to accountType,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    fun observeUserProfile(userId: String): Flow<CloudUserProfile?> = flow {
        val uid = requireUserId()
        check(uid == userId) { "Refusing cross-user profile listener." }
        val path = "users/$uid"
        emitAll(
            db.collection("users").document(uid)
                .snapshots()
                .map { snapshot ->
                    if (!snapshot.exists()) null
                    else snapshot.toObject(
                        CloudUserProfile::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    fun observeUserDecks(userId: String): Flow<List<PresentationDeck>> = flow {
        val uid = requireUserId()
        check(uid == userId) { "Refusing cross-user decks listener." }
        val path = "users/$uid/decks"
        emitAll(
            db.collection("users").document(uid).collection("decks")
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CloudDeckDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).map { it.toPresentationDeck() }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun saveDeck(userId: String, deck: PresentationDeck): Result<Unit> {
        val safeDeckId = deck.id.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120).ifBlank { "deck_default" }
        val docPath = "users/$userId/decks/$safeDeckId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Refusing cross-user deck write." }

            val safeTitle = deck.title.trim().take(160).ifBlank { "Untitled Deck" }
            val safeSubtitle = deck.subtitle.trim().take(300)
            val safeAudience = deck.audience.trim().take(80).ifBlank { "Students" }
            val slideMaps = deck.toSlideMaps()
            require(slideMaps.isNotEmpty()) { "Deck must contain at least one slide." }

            val docRef = db.collection("users").document(uid)
                .collection("decks").document(safeDeckId)

            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                docRef.update(
                    mapOf(
                        "title" to safeTitle,
                        "subtitle" to safeSubtitle,
                        "audience" to safeAudience,
                        "themeName" to deck.theme.name,
                        "slides" to slideMaps,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "title" to safeTitle,
                    "subtitle" to safeSubtitle,
                    "audience" to safeAudience,
                    "themeName" to deck.theme.name,
                    "aiGenerated" to deck.isAiGenerated,
                    "slides" to slideMaps,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteDeck(userId: String, deckId: String): Result<Unit> {
        val safeDeckId = deckId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120)
        val docPath = "users/$userId/decks/$safeDeckId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Refusing cross-user deck delete." }
            require(safeDeckId.isNotBlank()) { "Deck ID cannot be blank." }
            db.collection("users").document(uid)
                .collection("decks").document(safeDeckId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateProActive(userId: String, active: Boolean): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Refusing cross-user profile update." }
            db.collection("users").document(uid).update(
                mapOf(
                    "proActive" to active,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateCompletedModules(userId: String, completedIds: Set<String>): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Refusing cross-user profile update." }
            db.collection("users").document(uid).update(
                mapOf(
                    "completedModuleIds" to completedIds.take(50).toList(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateDailyPrompts(userId: String, count: Int, dateKey: String): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Refusing cross-user profile update." }
            db.collection("users").document(uid).update(
                mapOf(
                    "dailyPromptsUsed" to count.toLong().coerceIn(0L, 10000L),
                    "promptDate" to dateKey.take(10),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }
}
