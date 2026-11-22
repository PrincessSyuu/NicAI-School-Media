package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.NovaDeckAiService
import com.example.data.ChatMessage
import com.example.data.CloudUserProfile
import com.example.data.GradeLevel
import com.example.data.NicAiMode
import com.example.data.NovaDeckCloudRepository
import com.example.data.PreferencesRepository
import com.example.data.PresentationDeck
import com.example.data.SampleDataRepository
import com.example.data.SlideItem
import com.example.data.SlideLayoutType
import com.example.data.SlideTheme
import com.example.data.StudyDifficulty
import com.example.data.StudyModule
import com.example.data.UserPreferencesState
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val TAG = "NovaDeckViewModel"

sealed interface SlideGenUiState {
    data object Idle : SlideGenUiState
    data object Loading : SlideGenUiState
    data class Success(val message: String) : SlideGenUiState
    data class Error(val message: String) : SlideGenUiState
}

sealed interface NicAiUiState {
    data object Idle : NicAiUiState
    data object Streaming : NicAiUiState
    data object QuotaReached : NicAiUiState
    data class Error(val message: String) : NicAiUiState
}

class NovaDeckViewModel(
    private val prefsRepo: PreferencesRepository,
    private val cloudRepo: NovaDeckCloudRepository,
    private val currentUser: FirebaseUser
) : ViewModel() {

    private val aiService = NovaDeckAiService()

    val userEmail: String = currentUser.email?.takeIf { it.isNotBlank() } ?: "Google Account"
    val isSchoolWorkspace: Boolean =
        cloudRepo.classifyAccountType(currentUser.email) == "school_workspace"
    val userDisplayName: String = currentUser.displayName?.takeIf { it.isNotBlank() }
        ?: userEmail.substringBefore('@').ifBlank { "NovaDeck Learner" }

    private val _cloudError = MutableStateFlow<String?>(null)
    val cloudError: StateFlow<String?> = _cloudError.asStateFlow()

    private val _cloudProfile = MutableStateFlow<CloudUserProfile?>(null)

    val prefsState: StateFlow<UserPreferencesState> = combine(
        prefsRepo.preferencesFlow,
        _cloudProfile
    ) { localPrefs, cloudProfile ->
        if (cloudProfile == null) {
            localPrefs
        } else {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val cloudPromptsToday = if (cloudProfile.promptDate == today) {
                cloudProfile.dailyPromptsUsed.toInt().coerceAtLeast(0)
            } else {
                0
            }
            localPrefs.copy(
                isProActive = cloudProfile.proActive,
                dailyPromptsUsed = maxOf(localPrefs.dailyPromptsUsed, cloudPromptsToday),
                completedModuleIds = cloudProfile.completedModuleIds.toSet() + localPrefs.completedModuleIds
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferencesState()
    )

    private val _decks = MutableStateFlow(SampleDataRepository.starterDecks)
    val decks: StateFlow<List<PresentationDeck>> = _decks.asStateFlow()

    private val _activeDeckId = MutableStateFlow(SampleDataRepository.starterDecks.first().id)
    val activeDeckId: StateFlow<String> = _activeDeckId.asStateFlow()

    val activeDeck: StateFlow<PresentationDeck> = combine(_decks, _activeDeckId) { list, id ->
        list.firstOrNull { it.id == id } ?: list.first()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SampleDataRepository.starterDecks.first()
    )

    private val _slideTopicInput = MutableStateFlow("")
    val slideTopicInput: StateFlow<String> = _slideTopicInput.asStateFlow()

    private val _slideAudienceInput = MutableStateFlow("SMP Students")
    val slideAudienceInput: StateFlow<String> = _slideAudienceInput.asStateFlow()

    private val _slideCountInput = MutableStateFlow(5)
    val slideCountInput: StateFlow<Int> = _slideCountInput.asStateFlow()

    private val _slideThemeInput = MutableStateFlow(SlideTheme.DARK_NEON)
    val slideThemeInput: StateFlow<SlideTheme> = _slideThemeInput.asStateFlow()

    private val _slideGenState = MutableStateFlow<SlideGenUiState>(SlideGenUiState.Idle)
    val slideGenState: StateFlow<SlideGenUiState> = _slideGenState.asStateFlow()

    private val _nicAiMode = MutableStateFlow(NicAiMode.SMP_TUTOR)
    val nicAiMode: StateFlow<NicAiMode> = _nicAiMode.asStateFlow()

    private val _nicAiUiState = MutableStateFlow<NicAiUiState>(NicAiUiState.Idle)
    val nicAiUiState: StateFlow<NicAiUiState> = _nicAiUiState.asStateFlow()

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "welcome_nicai",
                isUser = false,
                text = "Hey $userDisplayName! I'm **NicAI**, your step-by-step study buddy and slide coach here in NovaDeck.\n\nStuck on an SMP or SMA topic, or need fast slide bullet points? Tell me what you're working on and we'll break it down calmly together!"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Study Hub state
    val allModules: List<StudyModule> = SampleDataRepository.studyModules

    private val _gradeFilter = MutableStateFlow<GradeLevel?>(null)
    val gradeFilter: StateFlow<GradeLevel?> = _gradeFilter.asStateFlow()

    private val _subjectFilter = MutableStateFlow<String?>(null)
    val subjectFilter: StateFlow<String?> = _subjectFilter.asStateFlow()

    private val _difficultyFilter = MutableStateFlow<StudyDifficulty?>(null)
    val difficultyFilter: StateFlow<StudyDifficulty?> = _difficultyFilter.asStateFlow()

    private val _selectedModuleId = MutableStateFlow<String?>(null)
    val selectedModuleId: StateFlow<String?> = _selectedModuleId.asStateFlow()

    private val _walkthroughChecked = MutableStateFlow<Map<String, Set<Int>>>(emptyMap())
    val walkthroughChecked: StateFlow<Map<String, Set<Int>>> = _walkthroughChecked.asStateFlow()

    private val _miniCheckAnswers = MutableStateFlow<Map<String, Int>>(emptyMap())
    val miniCheckAnswers: StateFlow<Map<String, Int>> = _miniCheckAnswers.asStateFlow()

    init {
        ensureUserProfileInCloud()
        observeCloudUserProfile()
        observeCloudDecks()
    }

    fun dismissCloudError() {
        _cloudError.value = null
    }

    fun retryCloudSync() {
        _cloudError.value = null
        ensureUserProfileInCloud()
    }

    private fun ensureUserProfileInCloud() {
        viewModelScope.launch {
            val result = cloudRepo.ensureUserProfile(currentUser)
            if (result.isFailure) {
                val err = result.exceptionOrNull()
                _cloudError.value = err?.localizedMessage
                    ?: "Could not sync your Google profile to cloud storage."
            }
        }
    }

    private fun observeCloudUserProfile() {
        viewModelScope.launch {
            cloudRepo.observeUserProfile(currentUser.uid)
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    _cloudError.value = firestoreError?.localizedMessage
                        ?: error.localizedMessage
                        ?: "Could not sync cloud profile."
                    emit(null)
                }
                .collect { profile ->
                    _cloudProfile.value = profile
                }
        }
    }

    private fun observeCloudDecks() {
        viewModelScope.launch {
            cloudRepo.observeUserDecks(currentUser.uid)
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    _cloudError.value = firestoreError?.localizedMessage
                        ?: error.localizedMessage
                        ?: "Could not load your cloud decks."
                    emit(emptyList())
                }
                .collect { remoteDecks ->
                    val starterTemplates = SampleDataRepository.starterDecks.filter { starter ->
                        remoteDecks.none { it.id == starter.id }
                    }
                    val merged = remoteDecks + starterTemplates
                    _decks.value = merged
                    if (merged.none { it.id == _activeDeckId.value } && merged.isNotEmpty()) {
                        _activeDeckId.value = merged.first().id
                    }
                }
        }
    }

    private fun persistDeckToCloud(deck: PresentationDeck) {
        viewModelScope.launch {
            val result = cloudRepo.saveDeck(currentUser.uid, deck)
            if (result.isFailure) {
                _cloudError.value = result.exceptionOrNull()?.localizedMessage
                    ?: "Deck saved locally, but cloud sync failed."
            }
        }
    }

    fun updateSlideTopicInput(value: String) {
        _slideTopicInput.value = value
    }

    fun updateSlideAudienceInput(value: String) {
        _slideAudienceInput.value = value
    }

    fun updateSlideCountInput(count: Int) {
        _slideCountInput.value = count
    }

    fun selectDeck(deckId: String) {
        _activeDeckId.value = deckId
        val selected = _decks.value.firstOrNull { it.id == deckId }
        if (selected != null) {
            _slideThemeInput.value = selected.theme
        }
    }

    fun switchDeckTheme(theme: SlideTheme) {
        _slideThemeInput.value = theme
        val currentId = _activeDeckId.value
        var updatedDeck: PresentationDeck? = null
        _decks.update { list ->
            list.map { deck ->
                if (deck.id == currentId) {
                    deck.copy(theme = theme).also { updatedDeck = it }
                } else {
                    deck
                }
            }
        }
        updatedDeck?.let(::persistDeckToCloud)
    }

    fun clearSlideGenState() {
        _slideGenState.value = SlideGenUiState.Idle
    }

    fun generateSlideDeck() {
        val topic = _slideTopicInput.value.trim()
        if (topic.isBlank()) {
            _slideGenState.value = SlideGenUiState.Error("Enter a topic, school subject, or rough notes first.")
            return
        }

        val audience = _slideAudienceInput.value
        val count = _slideCountInput.value
        val theme = _slideThemeInput.value

        viewModelScope.launch {
            _slideGenState.value = SlideGenUiState.Loading
            try {
                val deck = aiService.generateStructuredDeck(
                    topicOrNotes = topic,
                    audience = audience,
                    slideCount = count,
                    theme = theme
                )
                _decks.update { listOf(deck) + it }
                _activeDeckId.value = deck.id
                _slideGenState.value = SlideGenUiState.Success("Generated ${deck.slides.size} slides for \"${deck.title}\"")
                persistDeckToCloud(deck)
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Slide generation timed out", e)
                _slideGenState.value = SlideGenUiState.Error(
                    "Slide generation timed out after 60 seconds. Please try a shorter topic or retry."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Slide generation failed", e)
                _slideGenState.value = SlideGenUiState.Error(
                    e.localizedMessage ?: "Could not generate slides. Check connection and retry."
                )
            }
        }
    }

    fun updateSlide(
        slideId: String,
        heading: String,
        subheading: String,
        bullets: List<String>,
        layoutType: SlideLayoutType
    ) {
        val currentId = _activeDeckId.value
        var updatedDeck: PresentationDeck? = null
        _decks.update { list ->
            list.map { deck ->
                if (deck.id != currentId) deck
                else deck.copy(
                    slides = deck.slides.map { slide ->
                        if (slide.id != slideId) slide
                        else slide.copy(
                            heading = heading.ifBlank { slide.heading },
                            subheading = subheading,
                            bulletPoints = bullets.filter { it.isNotBlank() },
                            layoutType = layoutType
                        )
                    }
                ).also { updatedDeck = it }
            }
        }
        updatedDeck?.let(::persistDeckToCloud)
    }

    fun addQuickSlide(layoutType: SlideLayoutType) {
        val currentId = _activeDeckId.value
        val newSlide = when (layoutType) {
            SlideLayoutType.COMPARISON -> SlideItem(
                id = "s_${UUID.randomUUID().toString().take(6)}",
                layoutType = SlideLayoutType.COMPARISON,
                heading = "Side-by-Side Comparison",
                subheading = "Auto-balanced two-column breakdown",
                leftColumnTitle = "Concept A",
                leftColumnPoints = listOf("Core strength or trait", "Best used when..."),
                rightColumnTitle = "Concept B",
                rightColumnPoints = listOf("Key contrast point", "Real-world example")
            )
            SlideLayoutType.QA -> SlideItem(
                id = "s_${UUID.randomUUID().toString().take(6)}",
                layoutType = SlideLayoutType.QA,
                heading = "Discussion & Q&A",
                subheading = "Quick check with your class or team",
                bulletPoints = listOf("What surprised you most here?", "Where can we apply this next?")
            )
            else -> SlideItem(
                id = "s_${UUID.randomUUID().toString().take(6)}",
                layoutType = layoutType,
                heading = "New ${layoutType.label}",
                subheading = "Tap edit to customize points",
                bulletPoints = listOf("First crisp takeaway", "Second supporting fact", "Third actionable note")
            )
        }
        var updatedDeck: PresentationDeck? = null
        _decks.update { list ->
            list.map { deck ->
                if (deck.id == currentId && deck.slides.size < 15) {
                    deck.copy(slides = deck.slides + newSlide).also { updatedDeck = it }
                } else {
                    deck
                }
            }
        }
        updatedDeck?.let(::persistDeckToCloud)
    }

    fun deleteSlide(slideId: String) {
        val currentId = _activeDeckId.value
        var updatedDeck: PresentationDeck? = null
        _decks.update { list ->
            list.map { deck ->
                if (deck.id == currentId && deck.slides.size > 1) {
                    deck.copy(slides = deck.slides.filterNot { it.id == slideId }).also { updatedDeck = it }
                } else {
                    deck
                }
            }
        }
        updatedDeck?.let(::persistDeckToCloud)
    }

    fun setNicAiMode(mode: NicAiMode) {
        _nicAiMode.value = mode
    }

    fun clearNicAiError() {
        if (_nicAiUiState.value is NicAiUiState.Error) {
            _nicAiUiState.value = NicAiUiState.Idle
        }
    }

    fun sendNicAiMessage(rawPrompt: String) {
        val prompt = rawPrompt.trim()
        if (prompt.isBlank()) return
        if (_nicAiUiState.value == NicAiUiState.Streaming) return

        val prefs = prefsState.value
        if (!prefs.isProActive && prefs.promptsRemaining <= 0) {
            _nicAiUiState.value = NicAiUiState.QuotaReached
            return
        }

        val mode = _nicAiMode.value
        val userMsg = ChatMessage(
            id = "u_${UUID.randomUUID().toString().take(8)}",
            isUser = true,
            text = prompt,
            mode = mode
        )
        val assistantMsgId = "a_${UUID.randomUUID().toString().take(8)}"
        val placeholderAiMsg = ChatMessage(
            id = assistantMsgId,
            isUser = false,
            text = "",
            isStreaming = true,
            mode = mode,
            suggestedDeckTopic = prompt.take(80)
        )

        val historySnapshot = _chatMessages.value
        _chatMessages.update { it + userMsg + placeholderAiMsg }
        _nicAiUiState.value = NicAiUiState.Streaming

        viewModelScope.launch {
            try {
                aiService.streamNicAiResponse(
                    userPrompt = prompt,
                    mode = mode,
                    recentHistory = historySnapshot
                ).collect { partialText ->
                    _chatMessages.update { msgs ->
                        msgs.map { m ->
                            if (m.id == assistantMsgId) m.copy(text = partialText, isStreaming = true) else m
                        }
                    }
                }

                _chatMessages.update { msgs ->
                    msgs.map { m ->
                        if (m.id == assistantMsgId) m.copy(isStreaming = false) else m
                    }
                }
                if (!prefs.isProActive) {
                    prefsRepo.incrementDailyPromptUsed()
                    val nextUsed = prefs.dailyPromptsUsed + 1
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                    val cloudRes = cloudRepo.updateDailyPrompts(currentUser.uid, nextUsed, today)
                    if (cloudRes.isFailure) {
                        Log.w(TAG, "Failed to sync daily prompt counter", cloudRes.exceptionOrNull())
                    }
                }
                _nicAiUiState.value = NicAiUiState.Idle
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "NicAI streaming timed out", e)
                removePendingMessage(assistantMsgId)
                _nicAiUiState.value = NicAiUiState.Error(
                    "NicAI timed out after 60 seconds. Please retry your question."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "NicAI streaming failed", e)
                removePendingMessage(assistantMsgId)
                _nicAiUiState.value = NicAiUiState.Error(
                    e.localizedMessage ?: "NicAI couldn't connect right now. Please try again."
                )
            }
        }
    }

    private fun removePendingMessage(messageId: String) {
        _chatMessages.update { msgs ->
            msgs.filterNot { it.id == messageId && it.text.isBlank() }
                .map { if (it.id == messageId) it.copy(isStreaming = false) else it }
        }
    }

    // Study Hub Actions
    fun setGradeFilter(grade: GradeLevel?) {
        _gradeFilter.value = grade
    }

    fun setSubjectFilter(subject: String?) {
        _subjectFilter.value = subject
    }

    fun setDifficultyFilter(difficulty: StudyDifficulty?) {
        _difficultyFilter.value = difficulty
    }

    fun openModule(moduleId: String) {
        _selectedModuleId.value = moduleId
    }

    fun closeModule() {
        _selectedModuleId.value = null
    }

    fun toggleWalkthroughStep(moduleId: String, stepIndex: Int) {
        _walkthroughChecked.update { map ->
            val current = map[moduleId] ?: emptySet()
            val updated = if (stepIndex in current) current - stepIndex else current + stepIndex
            map + (moduleId to updated)
        }
    }

    fun answerMiniCheck(module: StudyModule, questionId: String, selectedOption: Int) {
        _miniCheckAnswers.update { it + (questionId to selectedOption) }
        val updatedAnswers = _miniCheckAnswers.value
        val allCorrectOrAttempted = module.miniChecks.all { q ->
            updatedAnswers[q.id] == q.correctIndex
        }
        if (allCorrectOrAttempted) {
            markModuleDone(module.id)
        }
    }

    fun markModuleDone(moduleId: String) {
        viewModelScope.launch {
            prefsRepo.markModuleCompleted(moduleId)
            val updatedSet = prefsState.value.completedModuleIds + moduleId
            val res = cloudRepo.updateCompletedModules(currentUser.uid, updatedSet)
            if (res.isFailure) {
                _cloudError.value = res.exceptionOrNull()?.localizedMessage
                    ?: "Completed locally, but cloud progress sync failed."
            }
        }
    }

    fun toggleProDemo(active: Boolean) {
        viewModelScope.launch {
            prefsRepo.setProActive(active)
            val res = cloudRepo.updateProActive(currentUser.uid, active)
            if (res.isFailure) {
                _cloudError.value = res.exceptionOrNull()?.localizedMessage
                    ?: "Updated locally, but cloud plan sync failed."
            }
            if (active && _nicAiUiState.value == NicAiUiState.QuotaReached) {
                _nicAiUiState.value = NicAiUiState.Idle
            }
        }
    }

    fun toggleDarkTheme(dark: Boolean) {
        viewModelScope.launch {
            prefsRepo.setDarkTheme(dark)
        }
    }
}
