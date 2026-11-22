package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.PresentationDeck
import com.example.data.SampleDataRepository
import com.example.data.SlideItem
import com.example.data.SlideLayoutType
import com.example.data.SlideTheme
import com.example.data.UserPreferencesState
import com.example.export.DeckExporter
import com.example.ui.components.ProUpgradeInlineCard
import com.example.ui.components.SlideCanvasCard
import com.example.ui.theme.CoralRose
import com.example.ui.theme.MintSoft
import com.example.ui.theme.SunsetPeach
import androidx.compose.material3.ButtonDefaults
import com.example.ui.viewmodel.SlideGenUiState

@Composable
fun SlideStudioScreen(
    prefs: UserPreferencesState,
    decks: List<PresentationDeck>,
    activeDeck: PresentationDeck,
    topicInput: String,
    audienceInput: String,
    slideCountInput: Int,
    selectedTheme: SlideTheme,
    genState: SlideGenUiState,
    onTopicChange: (String) -> Unit,
    onAudienceChange: (String) -> Unit,
    onSlideCountChange: (Int) -> Unit,
    onSelectDeck: (String) -> Unit,
    onSwitchTheme: (SlideTheme) -> Unit,
    onGenerateDeck: () -> Unit,
    onClearGenState: () -> Unit,
    onUpdateSlide: (String, String, String, List<String>, SlideLayoutType) -> Unit,
    onAddSlide: (SlideLayoutType) -> Unit,
    onDeleteSlide: (String) -> Unit,
    onNavigateToPlans: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPresentMode by rememberSaveable { mutableStateOf(false) }
    var showExportDialog by rememberSaveable { mutableStateOf(false) }
    var editingSlide by remember { mutableStateOf<SlideItem?>(null) }
    var showAddSlideMenu by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 760.dp

        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(0.44f)
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        SlideStudioHeader()
                    }
                    item {
                        TopicGeneratorCard(
                            topicInput = topicInput,
                            audienceInput = audienceInput,
                            slideCountInput = slideCountInput,
                            genState = genState,
                            onTopicChange = onTopicChange,
                            onAudienceChange = onAudienceChange,
                            onSlideCountChange = onSlideCountChange,
                            onGenerateDeck = onGenerateDeck,
                            onClearGenState = onClearGenState
                        )
                    }
                    item {
                        ThemeSwitcherCard(
                            selectedTheme = selectedTheme,
                            isProActive = prefs.isProActive,
                            onSwitchTheme = onSwitchTheme,
                            onNavigateToPlans = onNavigateToPlans
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(0.56f)
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        DeckControlHeader(
                            decks = decks,
                            activeDeck = activeDeck,
                            showAddMenu = showAddSlideMenu,
                            onToggleAddMenu = { showAddSlideMenu = it },
                            onSelectDeck = onSelectDeck,
                            onPresentClick = { showPresentMode = true },
                            onExportClick = { showExportDialog = true },
                            onAddSlide = { layout ->
                                showAddSlideMenu = false
                                onAddSlide(layout)
                            }
                        )
                    }
                    itemsIndexed(activeDeck.slides, key = { _, s -> s.id }) { idx, slide ->
                        SlideCardWithActions(
                            slide = slide,
                            index = idx,
                            total = activeDeck.slides.size,
                            theme = activeDeck.theme,
                            isProUser = prefs.isProActive,
                            canDelete = activeDeck.slides.size > 1,
                            onEdit = { editingSlide = slide },
                            onDelete = { onDeleteSlide(slide.id) }
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { SlideStudioHeader() }
                item {
                    TopicGeneratorCard(
                        topicInput = topicInput,
                        audienceInput = audienceInput,
                        slideCountInput = slideCountInput,
                        genState = genState,
                        onTopicChange = onTopicChange,
                        onAudienceChange = onAudienceChange,
                        onSlideCountChange = onSlideCountChange,
                        onGenerateDeck = onGenerateDeck,
                        onClearGenState = onClearGenState
                    )
                }
                item {
                    ThemeSwitcherCard(
                        selectedTheme = selectedTheme,
                        isProActive = prefs.isProActive,
                        onSwitchTheme = onSwitchTheme,
                        onNavigateToPlans = onNavigateToPlans
                    )
                }
                item {
                    DeckControlHeader(
                        decks = decks,
                        activeDeck = activeDeck,
                        showAddMenu = showAddSlideMenu,
                        onToggleAddMenu = { showAddSlideMenu = it },
                        onSelectDeck = onSelectDeck,
                        onPresentClick = { showPresentMode = true },
                        onExportClick = { showExportDialog = true },
                        onAddSlide = { layout ->
                            showAddSlideMenu = false
                            onAddSlide(layout)
                        }
                    )
                }
                itemsIndexed(activeDeck.slides, key = { _, s -> s.id }) { idx, slide ->
                    SlideCardWithActions(
                        slide = slide,
                        index = idx,
                        total = activeDeck.slides.size,
                        theme = activeDeck.theme,
                        isProUser = prefs.isProActive,
                        canDelete = activeDeck.slides.size > 1,
                        onEdit = { editingSlide = slide },
                        onDelete = { onDeleteSlide(slide.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }

    if (showPresentMode) {
        PresentationModeDialog(
            deck = activeDeck,
            isProUser = prefs.isProActive,
            onDismiss = { showPresentMode = false }
        )
    }

    if (showExportDialog) {
        ExportDeckDialog(
            deck = activeDeck,
            isProUser = prefs.isProActive,
            onCopyMarkdown = {
                DeckExporter.copyToClipboard(context, "NovaDeck Markdown", DeckExporter.toMarkdown(activeDeck))
                onShowMessage("Markdown slides copied to clipboard!")
                showExportDialog = false
            },
            onCopyHtml = {
                DeckExporter.copyToClipboard(context, "NovaDeck HTML", DeckExporter.toHtmlSlides(activeDeck))
                onShowMessage("HTML presentation copied to clipboard!")
                showExportDialog = false
            },
            onShareOutline = {
                DeckExporter.shareTextOutline(context, activeDeck)
                showExportDialog = false
            },
            onExportPdf = {
                try {
                    DeckExporter.exportAndSharePdf(context, activeDeck)
                    showExportDialog = false
                } catch (e: Exception) {
                    onShowMessage(e.localizedMessage ?: "Unable to share PDF right now.")
                }
            },
            onNavigateToPlans = {
                showExportDialog = false
                onNavigateToPlans()
            },
            onDismiss = { showExportDialog = false }
        )
    }

    editingSlide?.let { slide ->
        EditSlideDialog(
            slide = slide,
            onSave = { heading, sub, bullets, layout ->
                onUpdateSlide(slide.id, heading, sub, bullets, layout)
                editingSlide = null
            },
            onDismiss = { editingSlide = null }
        )
    }
}

@Composable
private fun SlideStudioHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "Slide Studio",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Topic-to-slides in seconds. Auto-balanced layouts — zero box dragging.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TopicGeneratorCard(
    topicInput: String,
    audienceInput: String,
    slideCountInput: Int,
    genState: SlideGenUiState,
    onTopicChange: (String) -> Unit,
    onAudienceChange: (String) -> Unit,
    onSlideCountChange: (Int) -> Unit,
    onGenerateDeck: () -> Unit,
    onClearGenState: () -> Unit
) {
    val audiences = listOf("SMP Students", "SMA Students", "Young Pro / Office")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Instant Topic-to-Slide Structuring",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = topicInput,
                onValueChange = onTopicChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("slide_topic_input"),
                label = { Text("Enter topic, subject, or rough notes") },
                placeholder = { Text("e.g., Newton's 3 Laws with daily examples") },
                minLines = 2,
                maxLines = 4,
                shape = MaterialTheme.shapes.medium
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SampleDataRepository.slideTopicChips) { chip ->
                    AssistChip(
                        onClick = { onTopicChange(chip) },
                        label = { Text(chip, maxLines = 1) },
                        modifier = Modifier.testTag("slide_topic_chip")
                    )
                }
            }

            Text(
                text = "Audience & Length",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(audiences) { aud ->
                    FilterChip(
                        selected = audienceInput == aud,
                        onClick = { onAudienceChange(aud) },
                        label = { Text(aud) }
                    )
                }
                items(listOf(5, 7)) { count ->
                    FilterChip(
                        selected = slideCountInput == count,
                        onClick = { onSlideCountChange(count) },
                        label = { Text("$count Slides") }
                    )
                }
            }

            Button(
                onClick = onGenerateDeck,
                enabled = genState != SlideGenUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generate_deck_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SunsetPeach,
                    contentColor = Color.White
                )
            ) {
                if (genState == SlideGenUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Structuring slides with AI...")
                } else {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Futuristic Deck", fontWeight = FontWeight.Bold)
                }
            }

            when (genState) {
                is SlideGenUiState.Success -> {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MintSoft.copy(alpha = 0.14f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = genState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MintSoft,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = onClearGenState, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Filled.Close, contentDescription = "Close notice", tint = MintSoft)
                            }
                        }
                    }
                }
                is SlideGenUiState.Error -> {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.14f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = genState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = onGenerateDeck) {
                                Text("Retry")
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun ThemeSwitcherCard(
    selectedTheme: SlideTheme,
    isProActive: Boolean,
    onSwitchTheme: (SlideTheme) -> Unit,
    onNavigateToPlans: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "1-Click Futuristic Themes",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(SlideTheme.entries) { theme ->
                val selected = theme == selectedTheme
                Surface(
                    onClick = { onSwitchTheme(theme) },
                    modifier = Modifier.testTag("theme_chip_${theme.name.lowercase()}"),
                    shape = MaterialTheme.shapes.medium,
                    color = theme.backgroundColor,
                    border = BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) theme.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(theme.accentColor)
                        )
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = theme.displayName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = theme.titleColor
                                )
                                if (theme.isProFeature && !isProActive) {
                                    Text(
                                        text = "PRO",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CoralRose
                                    )
                                }
                            }
                            Text(
                                text = theme.tagline,
                                style = MaterialTheme.typography.bodySmall,
                                color = theme.bodyColor
                            )
                        }
                    }
                }
            }
        }
        if (selectedTheme.isProFeature && !isProActive) {
            ProUpgradeInlineCard(
                title = "${selectedTheme.displayName} is a Pro Theme",
                description = "Previewing live right now! Unlock NovaDeck Pro to export with Pro themes & PDF.",
                onUpgradeClick = onNavigateToPlans
            )
        }
    }
}

@Composable
private fun DeckControlHeader(
    decks: List<PresentationDeck>,
    activeDeck: PresentationDeck,
    showAddMenu: Boolean,
    onToggleAddMenu: (Boolean) -> Unit,
    onSelectDeck: (String) -> Unit,
    onPresentClick: () -> Unit,
    onExportClick: () -> Unit,
    onAddSlide: (SlideLayoutType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(decks, key = { it.id }) { deck ->
                FilterChip(
                    selected = deck.id == activeDeck.id,
                    onClick = { onSelectDeck(deck.id) },
                    label = {
                        Text(
                            text = deck.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPresentClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("present_deck_button")
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Present")
            }

            OutlinedButton(
                onClick = onExportClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("export_deck_button")
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export")
            }

            Box {
                OutlinedButton(
                    onClick = { onToggleAddMenu(true) },
                    modifier = Modifier.testTag("add_slide_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add slide", modifier = Modifier.size(18.dp))
                }
                DropdownMenu(
                    expanded = showAddMenu,
                    onDismissRequest = { onToggleAddMenu(false) }
                ) {
                    SlideLayoutType.entries.forEach { layout ->
                        DropdownMenuItem(
                            text = { Text("Add ${layout.label}") },
                            onClick = { onAddSlide(layout) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SlideCardWithActions(
    slide: SlideItem,
    index: Int,
    total: Int,
    theme: SlideTheme,
    isProUser: Boolean,
    canDelete: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SlideCanvasCard(
            slide = slide,
            slideIndex = index,
            totalSlides = total,
            theme = theme,
            isProUser = isProUser,
            onClick = onEdit
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (slide.speakerNotes.isNotBlank()) {
                Text(
                    text = "Cue: ${slide.speakerNotes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            Row {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.testTag("edit_slide_${slide.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit slide ${index + 1}",
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (canDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_slide_${slide.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "Delete slide ${index + 1}",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresentationModeDialog(
    deck: PresentationDeck,
    isProUser: Boolean,
    onDismiss: () -> Unit
) {
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var showNotes by rememberSaveable { mutableStateOf(false) }
    val safeIndex = currentIndex.coerceIn(0, deck.slides.lastIndex)
    val slide = deck.slides[safeIndex]

    BackHandler(onBack = onDismiss)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF050812)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = deck.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Presentation Mode • ${safeIndex + 1} of ${deck.slides.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = deck.theme.accentColor
                        )
                    }
                    Row {
                        TextButton(onClick = { showNotes = !showNotes }) {
                            Text(if (showNotes) "Hide Notes" else "Notes")
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("exit_presentation_button")
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Exit presentation", tint = Color.White)
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = { (safeIndex + 1).toFloat() / deck.slides.size.coerceAtLeast(1) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(99.dp)),
                    color = deck.theme.accentColor
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SlideCanvasCard(
                        slide = slide,
                        slideIndex = safeIndex,
                        totalSlides = deck.slides.size,
                        theme = deck.theme,
                        isProUser = isProUser,
                        isFullscreen = true
                    )
                }

                if (showNotes && slide.speakerNotes.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = Color(0xFF131B33)
                    ) {
                        Text(
                            text = "Speaker Note: ${slide.speakerNotes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD5E1F5),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { if (safeIndex > 0) currentIndex = safeIndex - 1 },
                        enabled = safeIndex > 0,
                        modifier = Modifier.testTag("present_prev_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Previous")
                    }
                    Button(
                        onClick = {
                            if (safeIndex < deck.slides.lastIndex) {
                                currentIndex = safeIndex + 1
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.testTag("present_next_button")
                    ) {
                        Text(if (safeIndex < deck.slides.lastIndex) "Next Slide" else "Finish")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportDeckDialog(
    deck: PresentationDeck,
    isProUser: Boolean,
    onCopyMarkdown: () -> Unit,
    onCopyHtml: () -> Unit,
    onShareOutline: () -> Unit,
    onExportPdf: () -> Unit,
    onNavigateToPlans: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export \"${deck.title}\"") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Export clean, structured slides instantly—ready for class, office reviews, or sharing.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ExportOptionRow(
                    title = "Copy Markdown Slides",
                    subtitle = "Free • Ready-to-paste outline & notes",
                    icon = Icons.Filled.ContentCopy,
                    locked = false,
                    onClick = onCopyMarkdown
                )

                ExportOptionRow(
                    title = "Share Text Outline",
                    subtitle = "Free • Send to WhatsApp, Docs, or Classroom",
                    icon = Icons.Filled.Description,
                    locked = false,
                    onClick = onShareOutline
                )

                ExportOptionRow(
                    title = "Export & Share Print-Ready PDF",
                    subtitle = if (isProUser) "Pro Unlocked • Styled 16:9 PDF deck" else "Pro Feature • Upgrade to unlock PDF",
                    icon = Icons.Filled.PictureAsPdf,
                    locked = !isProUser,
                    onClick = if (isProUser) onExportPdf else onNavigateToPlans
                )

                ExportOptionRow(
                    title = "Copy Standalone HTML Slides",
                    subtitle = if (isProUser) "Pro Unlocked • Responsive web slide deck" else "Pro Feature • Upgrade to unlock HTML",
                    icon = Icons.Filled.Code,
                    locked = !isProUser,
                    onClick = if (isProUser) onCopyHtml else onNavigateToPlans
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun ExportOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    locked: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(
            1.dp,
            if (locked) SunsetPeach.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (locked) Icons.Filled.Lock else icon,
                contentDescription = null,
                tint = if (locked) SunsetPeach else MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EditSlideDialog(
    slide: SlideItem,
    onSave: (String, String, List<String>, SlideLayoutType) -> Unit,
    onDismiss: () -> Unit
) {
    var heading by remember { mutableStateOf(slide.heading) }
    var subheading by remember { mutableStateOf(slide.subheading) }
    var bulletsText by remember { mutableStateOf(slide.bulletPoints.joinToString("\n")) }
    var selectedLayout by remember { mutableStateOf(slide.layoutType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quick-Edit Slide") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(SlideLayoutType.entries) { layout ->
                        FilterChip(
                            selected = selectedLayout == layout,
                            onClick = { selectedLayout = layout },
                            label = { Text(layout.label) }
                        )
                    }
                }
                OutlinedTextField(
                    value = heading,
                    onValueChange = { heading = it },
                    label = { Text("Heading") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subheading,
                    onValueChange = { subheading = it },
                    label = { Text("Subheading") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (selectedLayout != SlideLayoutType.COMPARISON) {
                    OutlinedTextField(
                        value = bulletsText,
                        onValueChange = { bulletsText = it },
                        label = { Text("Bullet Points (one per line)") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bullets = bulletsText.lines().map { it.trim() }.filter { it.isNotBlank() }
                    onSave(heading, subheading, bullets, selectedLayout)
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
