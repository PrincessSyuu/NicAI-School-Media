package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.PresentationDeck
import com.example.data.SampleDataRepository
import com.example.data.StudyModule
import com.example.data.UserPreferencesState
import com.example.ui.components.BertDeveloperBadge
import com.example.ui.components.BertDeveloperFooter
import com.example.ui.components.TierStatusChip
import com.example.ui.theme.AquaGlow
import com.example.ui.theme.CoralRose
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MintSoft
import com.example.ui.theme.SunsetPeach
import androidx.compose.material3.ButtonDefaults

@Composable
fun HomeScreen(
    prefs: UserPreferencesState,
    decks: List<PresentationDeck>,
    modules: List<StudyModule>,
    userEmail: String,
    userDisplayName: String,
    isSchoolWorkspace: Boolean,
    cloudError: String?,
    onDismissCloudError: () -> Unit,
    onRetryCloudSync: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateToSlides: () -> Unit,
    onOpenDeck: (String) -> Unit,
    onNavigateToNicAi: () -> Unit,
    onNavigateToStudyHub: () -> Unit,
    onOpenModule: (String) -> Unit,
    onNavigateToPlans: () -> Unit,
    onToggleTheme: (Boolean) -> Unit,
    onOpenAboutBert: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NovaDeck",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    BertDeveloperBadge(onClick = onOpenAboutBert)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { onToggleTheme(!prefs.isDarkTheme) },
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (prefs.isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Toggle light or dark theme"
                        )
                    }
                    TierStatusChip(
                        isProActive = prefs.isProActive,
                        onClick = onNavigateToPlans
                    )
                }
            }
        }

        // Connected Google / School Account Card
        item {
            val badgeAccent = if (isSchoolWorkspace) AquaGlow else MintSoft
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, badgeAccent.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(badgeAccent.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSchoolWorkspace) Icons.Filled.School else Icons.Filled.AccountCircle,
                                contentDescription = null,
                                tint = badgeAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = userDisplayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Surface(
                                    shape = RoundedCornerShape(99.dp),
                                    color = badgeAccent.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = if (isSchoolWorkspace) "SCHOOL EMAIL" else "GOOGLE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = badgeAccent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CloudDone,
                                    contentDescription = null,
                                    tint = MintSoft,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = userEmail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sign out",
                            modifier = Modifier.size(16.dp),
                            tint = CoralRose
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sign Out", color = CoralRose)
                    }
                }
            }
        }

        if (cloudError != null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = CoralRose.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, CoralRose.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ErrorOutline,
                                contentDescription = null,
                                tint = CoralRose
                            )
                            Text(
                                text = cloudError,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onRetryCloudSync,
                                modifier = Modifier.testTag("retry_cloud_sync_button")
                            ) {
                                Text("Retry Sync")
                            }
                            TextButton(onClick = onDismissCloudError) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }
        }

        // Futuristic Hero Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_novadeck_1795362189471),
                        contentDescription = "NovaDeck futuristic workspace illustration",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF070B18).copy(alpha = 0.45f),
                                        Color(0xFF070B18).copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .align(Alignment.BottomStart),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                        ) {
                            Text(
                                text = "SMP • SMA • YOUNG PRO",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "Study Calmer. Present Faster.",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Turn rough notes into balanced slides in seconds, or learn tough topics step-by-step with NicAI.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD5E1F5)
                        )
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onNavigateToSlides,
                                modifier = Modifier.testTag("hero_slide_studio_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SunsetPeach,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Slideshow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Slide Studio")
                            }
                            OutlinedButton(
                                onClick = onNavigateToNicAi,
                                modifier = Modifier.testTag("hero_ask_nicai_button"),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f))
                            ) {
                                Text("Ask NicAI", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Momentum Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MomentumMetricCard(
                    value = "${decks.size}",
                    label = "Decks Ready",
                    accent = AquaGlow,
                    modifier = Modifier.weight(1f)
                )
                MomentumMetricCard(
                    value = "${prefs.completedModuleIds.size}/${modules.size}",
                    label = "Modules Done",
                    accent = MintSoft,
                    modifier = Modifier.weight(1f)
                )
                MomentumMetricCard(
                    value = if (prefs.isProActive) "∞" else "${prefs.promptsRemaining}",
                    label = if (prefs.isProActive) "Pro NicAI" else "Free Prompts",
                    accent = CoralRose,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Launch Shortcuts
        item {
            Text(
                text = "Quick Launch",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth >= 600.dp
                if (isWide) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionCard(
                            title = "Slide Studio",
                            subtitle = "Topic to deck fast",
                            icon = Icons.Filled.Slideshow,
                            accent = MaterialTheme.colorScheme.primary,
                            testTag = "quick_slides_card",
                            onClick = onNavigateToSlides,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            title = "NicAI Tutor",
                            subtitle = "Calm step-by-step",
                            icon = Icons.Filled.AutoAwesome,
                            accent = MaterialTheme.colorScheme.secondary,
                            testTag = "quick_nicai_card",
                            onClick = onNavigateToNicAi,
                            modifier = Modifier.weight(1f),
                            useNicAiAvatar = true
                        )
                        QuickActionCard(
                            title = "Study Hub",
                            subtitle = "Bite-sized modules",
                            icon = Icons.Filled.MenuBook,
                            accent = MintSoft,
                            testTag = "quick_study_card",
                            onClick = onNavigateToStudyHub,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            title = "Plans & Pro",
                            subtitle = "Student & Office",
                            icon = Icons.Filled.WorkspacePremium,
                            accent = SunsetPeach,
                            testTag = "quick_plans_card",
                            onClick = onNavigateToPlans,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionCard(
                                title = "Slide Studio",
                                subtitle = "Topic to deck fast",
                                icon = Icons.Filled.Slideshow,
                                accent = MaterialTheme.colorScheme.primary,
                                testTag = "quick_slides_card",
                                onClick = onNavigateToSlides,
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionCard(
                                title = "NicAI Tutor",
                                subtitle = "Calm step-by-step",
                                icon = Icons.Filled.AutoAwesome,
                                accent = MaterialTheme.colorScheme.secondary,
                                testTag = "quick_nicai_card",
                                onClick = onNavigateToNicAi,
                                modifier = Modifier.weight(1f),
                                useNicAiAvatar = true
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionCard(
                                title = "Study Hub",
                                subtitle = "Bite-sized modules",
                                icon = Icons.Filled.MenuBook,
                                accent = MintSoft,
                                testTag = "quick_study_card",
                                onClick = onNavigateToStudyHub,
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionCard(
                                title = "Plans & Pro",
                                subtitle = "Student & Office",
                                icon = Icons.Filled.WorkspacePremium,
                                accent = SunsetPeach,
                                testTag = "quick_plans_card",
                                onClick = onNavigateToPlans,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Recent Decks Carousel
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Slide Decks",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Open Studio",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onNavigateToSlides)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(decks, key = { it.id }) { deck ->
                    Card(
                        onClick = { onOpenDeck(deck.id) },
                        modifier = Modifier
                            .width(245.dp)
                            .testTag("home_deck_card_${deck.id}"),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = deck.theme.backgroundColor),
                        border = BorderStroke(1.dp, deck.theme.accentColor.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${deck.slides.size} SLIDES • ${deck.theme.displayName.uppercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = deck.theme.accentColor
                            )
                            Text(
                                text = deck.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = deck.theme.titleColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = deck.audience,
                                style = MaterialTheme.typography.bodySmall,
                                color = deck.theme.bodyColor
                            )
                        }
                    }
                }
            }
        }

        // Low-Friction Study Pick
        item {
            val recommended = modules.firstOrNull { it.id !in prefs.completedModuleIds } ?: modules.first()
            Text(
                text = "Low-Friction Study Pick",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                onClick = { onOpenModule(recommended.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_recommended_module"),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ),
                border = BorderStroke(1.dp, MintSoft.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${recommended.gradeLevel.badge} • ${recommended.subject.uppercase()} • ${recommended.estimatedMinutes} MIN",
                            style = MaterialTheme.typography.labelSmall,
                            color = MintSoft
                        )
                        Text(
                            text = recommended.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = recommended.goal,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open study module",
                        tint = MintSoft
                    )
                }
            }
        }

        // Partner Educational Spotlight (Monetization touchpoint)
        item {
            val partner = SampleDataRepository.partnerBoosts.first()
            Card(
                onClick = onNavigateToPlans,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = partner.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = partner.partnerName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = partner.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = partner.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Bert Developer Footer
        item {
            BertDeveloperFooter(onAboutClick = onOpenAboutBert)
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun MomentumMetricCard(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily
                ),
                color = accent,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    useNicAiAvatar: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = modifier.testTag(testTag),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (useNicAiAvatar) {
                Image(
                    painter = painterResource(id = R.drawable.img_nicai_avatar_1795362189371),
                    contentDescription = "NicAI avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
