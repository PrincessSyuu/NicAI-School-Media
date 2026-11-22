package com.example.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.CuratedResource
import com.example.data.GradeLevel
import com.example.data.ResourceSourceType
import com.example.data.StudyDifficulty
import com.example.data.StudyModule
import com.example.data.UserPreferencesState
import com.example.ui.theme.CoralRose
import com.example.ui.theme.LimeSoft
import com.example.ui.theme.MintSoft
import com.example.ui.theme.SunsetPeach

@Composable
fun StudyHubScreen(
    prefs: UserPreferencesState,
    modules: List<StudyModule>,
    gradeFilter: GradeLevel?,
    subjectFilter: String?,
    difficultyFilter: StudyDifficulty?,
    selectedModuleId: String?,
    walkthroughChecked: Map<String, Set<Int>>,
    miniCheckAnswers: Map<String, Int>,
    onGradeFilterChange: (GradeLevel?) -> Unit,
    onSubjectFilterChange: (String?) -> Unit,
    onDifficultyFilterChange: (StudyDifficulty?) -> Unit,
    onOpenModule: (String) -> Unit,
    onCloseModule: () -> Unit,
    onToggleWalkthroughStep: (String, Int) -> Unit,
    onAnswerMiniCheck: (StudyModule, String, Int) -> Unit,
    onMarkModuleDone: (String) -> Unit,
    onAskNicAiAboutTopic: (String) -> Unit,
    onTurnModuleIntoSlides: (String) -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects = modules.map { it.subject }.distinct()
    val filteredModules = modules.filter { mod ->
        (gradeFilter == null || mod.gradeLevel == gradeFilter) &&
            (subjectFilter == null || mod.subject == subjectFilter) &&
            (difficultyFilter == null || mod.difficulty == difficultyFilter)
    }
    val selectedModule = modules.firstOrNull { it.id == selectedModuleId }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 760.dp

        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                ModuleListPane(
                    prefs = prefs,
                    modules = filteredModules,
                    subjects = subjects,
                    gradeFilter = gradeFilter,
                    subjectFilter = subjectFilter,
                    difficultyFilter = difficultyFilter,
                    selectedModuleId = selectedModule?.id,
                    onGradeFilterChange = onGradeFilterChange,
                    onSubjectFilterChange = onSubjectFilterChange,
                    onDifficultyFilterChange = onDifficultyFilterChange,
                    onOpenModule = onOpenModule,
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxHeight()
                )
                val detailModule = selectedModule ?: filteredModules.firstOrNull()
                if (detailModule != null) {
                    ModuleReaderPane(
                        module = detailModule,
                        isCompleted = detailModule.id in prefs.completedModuleIds,
                        checkedSteps = walkthroughChecked[detailModule.id] ?: emptySet(),
                        miniCheckAnswers = miniCheckAnswers,
                        showBackButton = false,
                        onBack = onCloseModule,
                        onToggleStep = { idx -> onToggleWalkthroughStep(detailModule.id, idx) },
                        onAnswerMiniCheck = { qId, opt -> onAnswerMiniCheck(detailModule, qId, opt) },
                        onMarkDone = { onMarkModuleDone(detailModule.id) },
                        onAskNicAi = { onAskNicAiAboutTopic("Help me understand ${detailModule.title} (${detailModule.subject}) step-by-step") },
                        onTurnIntoSlides = { onTurnModuleIntoSlides("${detailModule.title}: ${detailModule.goal}") },
                        onShowMessage = onShowMessage,
                        modifier = Modifier
                            .weight(0.55f)
                            .fillMaxHeight()
                    )
                }
            }
        } else {
            if (selectedModule != null) {
                BackHandler(onBack = onCloseModule)
                ModuleReaderPane(
                    module = selectedModule,
                    isCompleted = selectedModule.id in prefs.completedModuleIds,
                    checkedSteps = walkthroughChecked[selectedModule.id] ?: emptySet(),
                    miniCheckAnswers = miniCheckAnswers,
                    showBackButton = true,
                    onBack = onCloseModule,
                    onToggleStep = { idx -> onToggleWalkthroughStep(selectedModule.id, idx) },
                    onAnswerMiniCheck = { qId, opt -> onAnswerMiniCheck(selectedModule, qId, opt) },
                    onMarkDone = { onMarkModuleDone(selectedModule.id) },
                    onAskNicAi = { onAskNicAiAboutTopic("Help me understand ${selectedModule.title} (${selectedModule.subject}) step-by-step") },
                    onTurnIntoSlides = { onTurnModuleIntoSlides("${selectedModule.title}: ${selectedModule.goal}") },
                    onShowMessage = onShowMessage,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                ModuleListPane(
                    prefs = prefs,
                    modules = filteredModules,
                    subjects = subjects,
                    gradeFilter = gradeFilter,
                    subjectFilter = subjectFilter,
                    difficultyFilter = difficultyFilter,
                    selectedModuleId = null,
                    onGradeFilterChange = onGradeFilterChange,
                    onSubjectFilterChange = onSubjectFilterChange,
                    onDifficultyFilterChange = onDifficultyFilterChange,
                    onOpenModule = onOpenModule,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ModuleListPane(
    prefs: UserPreferencesState,
    modules: List<StudyModule>,
    subjects: List<String>,
    gradeFilter: GradeLevel?,
    subjectFilter: String?,
    difficultyFilter: StudyDifficulty?,
    selectedModuleId: String?,
    onGradeFilterChange: (GradeLevel?) -> Unit,
    onSubjectFilterChange: (String?) -> Unit,
    onDifficultyFilterChange: (StudyDifficulty?) -> Unit,
    onOpenModule: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Study Support Hub",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Bite-sized calm modules for SMP & SMA — zero textbook overwhelm.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Grade Filter
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = gradeFilter == null,
                        onClick = { onGradeFilterChange(null) },
                        label = { Text("All Grades") }
                    )
                }
                items(GradeLevel.entries) { grade ->
                    FilterChip(
                        selected = gradeFilter == grade,
                        onClick = { onGradeFilterChange(if (gradeFilter == grade) null else grade) },
                        label = { Text(grade.badge) },
                        modifier = Modifier.testTag("filter_grade_${grade.name.lowercase()}")
                    )
                }
            }
        }

        // Subject & Difficulty Filter
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = subjectFilter == null,
                        onClick = { onSubjectFilterChange(null) },
                        label = { Text("All Subjects") }
                    )
                }
                items(subjects) { subj ->
                    FilterChip(
                        selected = subjectFilter == subj,
                        onClick = { onSubjectFilterChange(if (subjectFilter == subj) null else subj) },
                        label = { Text(subj) }
                    )
                }
                items(StudyDifficulty.entries) { diff ->
                    FilterChip(
                        selected = difficultyFilter == diff,
                        onClick = { onDifficultyFilterChange(if (difficultyFilter == diff) null else diff) },
                        label = { Text(diff.label) }
                    )
                }
            }
        }

        if (modules.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("No modules match those filters yet.", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = {
                            onGradeFilterChange(null)
                            onSubjectFilterChange(null)
                            onDifficultyFilterChange(null)
                        }) {
                            Text("Reset Filters")
                        }
                    }
                }
            }
        } else {
            items(modules, key = { it.id }) { mod ->
                val done = mod.id in prefs.completedModuleIds
                val isSelected = mod.id == selectedModuleId
                Card(
                    onClick = { onOpenModule(mod.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("study_module_card_${mod.id}"),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(
                        1.dp,
                        when {
                            done -> MintSoft.copy(alpha = 0.65f)
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                BadgePill(mod.gradeLevel.badge, MaterialTheme.colorScheme.primary)
                                BadgePill(mod.subject, MaterialTheme.colorScheme.secondary)
                                BadgePill("${mod.estimatedMinutes}m", SunsetPeach)
                            }
                            if (done) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = MintSoft,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = mod.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = mod.goal,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${mod.difficulty.label} • ${mod.walkthroughSteps.size} calm steps • ${mod.curatedResources.size} attributed resources",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
private fun ModuleReaderPane(
    module: StudyModule,
    isCompleted: Boolean,
    checkedSteps: Set<Int>,
    miniCheckAnswers: Map<String, Int>,
    showBackButton: Boolean,
    onBack: () -> Unit,
    onToggleStep: (Int) -> Unit,
    onAnswerMiniCheck: (String, Int) -> Unit,
    onMarkDone: () -> Unit,
    onAskNicAi: () -> Unit,
    onTurnIntoSlides: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showBackButton) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("module_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to modules"
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${module.gradeLevel.label} • ${module.subject}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = module.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (isCompleted) {
                    BadgePill("DONE", MintSoft)
                }
            }
        }

        // Quick Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAskNicAi,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("module_ask_nicai_button")
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ask NicAI")
                }
                OutlinedButton(
                    onClick = onTurnIntoSlides,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("module_to_slides_button")
                ) {
                    Icon(Icons.Filled.Slideshow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Make Slides")
                }
            }
        }

        // 1. TL;DR / Quick Concept
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "1. TL;DR / QUICK CONCEPT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = module.tldrConcept,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 2. Step-by-Step Walkthrough
        item {
            val progress = if (module.walkthroughSteps.isEmpty()) 0f
            else checkedSteps.size.toFloat() / module.walkthroughSteps.size

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. STEP-BY-STEP WALKTHROUGH",
                            style = MaterialTheme.typography.labelSmall,
                            color = MintSoft
                        )
                        Text(
                            text = "${checkedSteps.size}/${module.walkthroughSteps.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MintSoft
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(99.dp)),
                        color = MintSoft
                    )

                    module.walkthroughSteps.forEachIndexed { index, stepText ->
                        val checked = index in checkedSteps
                        Surface(
                            onClick = { onToggleStep(index) },
                            shape = MaterialTheme.shapes.small,
                            color = if (checked) {
                                MintSoft.copy(alpha = 0.14f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { onToggleStep(index) }
                                )
                                Text(
                                    text = stepText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Mini Check
        item {
            Text(
                text = "3. MINI CHECK (ZERO-PRESSURE)",
                style = MaterialTheme.typography.labelSmall,
                color = SunsetPeach
            )
        }

        itemsIndexed(module.miniChecks, key = { _, q -> q.id }) { qIdx, question ->
            val selectedOpt = miniCheckAnswers[question.id]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SunsetPeach.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Q${qIdx + 1}: ${question.question}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    question.options.forEachIndexed { optIdx, optText ->
                        val isChosen = selectedOpt == optIdx
                        val isCorrect = optIdx == question.correctIndex
                        Surface(
                            onClick = { onAnswerMiniCheck(question.id, optIdx) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mini_check_${question.id}_$optIdx"),
                            shape = MaterialTheme.shapes.small,
                            color = when {
                                isChosen && isCorrect -> MintSoft.copy(alpha = 0.18f)
                                isChosen && !isCorrect -> CoralRose.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    isChosen && isCorrect -> MintSoft
                                    isChosen && !isCorrect -> CoralRose
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (isChosen) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = when {
                                        isChosen && isCorrect -> MintSoft
                                        isChosen && !isCorrect -> CoralRose
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(text = optText, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    if (selectedOpt != null) {
                        val right = selectedOpt == question.correctIndex
                        Text(
                            text = if (right) {
                                question.encouragingFeedback
                            } else {
                                "Close! Try another option or tap 'Ask NicAI' above for a gentle hint."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (right) MintSoft else SunsetPeach
                        )
                    }
                }
            }
        }

        // 4. Curated Official & YouTube Resources
        item {
            Text(
                text = "4. CURATED OFFICIAL & YOUTUBE RESOURCES",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "External links open in your browser/YouTube with full source & creator attribution.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(module.curatedResources, key = { it.url }) { resource ->
            CuratedResourceCard(
                resource = resource,
                onOpen = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resource.url))
                        context.startActivity(intent)
                    } catch (_: ActivityNotFoundException) {
                        onShowMessage("No browser or YouTube app available to open ${resource.sourceName}.")
                    }
                }
            )
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Button(
                onClick = onMarkDone,
                enabled = !isCompleted,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mark_module_complete_button")
            ) {
                Text(if (isCompleted) "Module Completed" else "Mark Module Completed")
            }
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun CuratedResourceCard(
    resource: CuratedResource,
    onOpen: () -> Unit
) {
    val isYouTube = resource.sourceType == ResourceSourceType.YOUTUBE_EDU
    Card(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isYouTube) Icons.Filled.PlayCircleOutline else Icons.Filled.Public,
                contentDescription = null,
                tint = if (isYouTube) CoralRose else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                BadgePill(
                    text = "${resource.sourceType.label} • ${resource.durationLabel}",
                    color = if (isYouTube) CoralRose else MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = resource.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Source: ${resource.sourceName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = resource.attributionNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Open external resource",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun BadgePill(
    text: String,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = RoundedCornerShape(99.dp),
        color = color.copy(alpha = 0.16f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
