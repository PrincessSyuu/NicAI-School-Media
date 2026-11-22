package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.SampleDataRepository
import com.example.data.UserPreferencesState
import com.example.ui.components.BertDeveloperFooter
import com.example.ui.theme.AquaGlow
import com.example.ui.theme.CoralRose
import com.example.ui.theme.MintSoft
import com.example.ui.theme.SunsetPeach

@Composable
fun PlansScreen(
    prefs: UserPreferencesState,
    userEmail: String,
    isSchoolWorkspace: Boolean,
    onToggleProDemo: (Boolean) -> Unit,
    onOpenAboutBert: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Plans & Licensing",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Sustainable student-first pricing built to keep core SMP & SMA learning free.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Connected Account Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    1.dp,
                    (if (isSchoolWorkspace) AquaGlow else MintSoft).copy(alpha = 0.45f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isSchoolWorkspace) "Connected School Google Account" else "Connected Google Account",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSchoolWorkspace) AquaGlow else MintSoft
                        )
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    TextButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("plans_sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sign out",
                            tint = CoralRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sign Out", color = CoralRose)
                    }
                }
            }
        }

        // Current Active Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = if (prefs.isProActive) {
                        MintSoft.copy(alpha = 0.14f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    if (prefs.isProActive) MintSoft else MaterialTheme.colorScheme.outline
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (prefs.isProActive) "NovaDeck Pro Active (Demo)" else "Starter Free Tier",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (prefs.isProActive) MintSoft else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (prefs.isProActive) {
                                "Unlimited NicAI prompts, all futuristic themes, PDF & HTML exports unlocked."
                            } else {
                                "${prefs.promptsRemaining}/${prefs.freeDailyPromptLimit} NicAI prompts left today • Starter themes active."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    if (prefs.isProActive) {
                        OutlinedButton(
                            onClick = { onToggleProDemo(false) },
                            modifier = Modifier.testTag("switch_to_free_button")
                        ) {
                            Text("Use Free")
                        }
                    } else {
                        Button(
                            onClick = { onToggleProDemo(true) },
                            modifier = Modifier.testTag("activate_pro_demo_button")
                        ) {
                            Text("Try Pro")
                        }
                    }
                }
            }
        }

        // Tier 1: Freemium Starter
        item {
            PlanCard(
                title = "Starter (Free Forever)",
                priceLabel = "$0 / Rp 0",
                badge = "SMP & SMA ESSENTIAL",
                accent = MaterialTheme.colorScheme.primary,
                features = listOf(
                    "Full access to core SMP & SMA bite-sized study modules",
                    "8 daily NicAI step-by-step tutor prompts",
                    "Topic-to-Slide AI generator with Minimalist Tech & Clean Academic themes",
                    "Markdown & plain outline slide export"
                ),
                actionLabel = if (!prefs.isProActive) "Current Plan" else "Switch to Starter",
                isCurrent = !prefs.isProActive,
                testTag = "plan_starter_button",
                onAction = { onToggleProDemo(false) }
            )
        }

        // Tier 2: Pro / Plus Plan
        item {
            PlanCard(
                title = "NovaDeck Pro — Student & Office",
                priceLabel = "$3.99 / mo • Rp 39.000",
                badge = "MOST POPULAR",
                accent = SunsetPeach,
                features = listOf(
                    "Unlimited NicAI tutoring, quiz generation & slide coaching",
                    "Unlock Dark Neon & Executive Pitch futuristic themes",
                    "One-tap Print-Ready PDF & HTML Slide export",
                    "Unlimited saved presentation decks & study packs"
                ),
                actionLabel = if (prefs.isProActive) "Pro Preview Active" else "Activate Pro Preview",
                isCurrent = prefs.isProActive,
                testTag = "plan_pro_button",
                onAction = { onToggleProDemo(true) }
            )
        }

        // Tier 3: School Licensing & Partner Boosts
        item {
            Text(
                text = "School Licensing & Partner Boosts",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(SampleDataRepository.partnerBoosts, key = { it.id }) { boost ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (boost.badge.contains("School")) {
                                    Icons.Filled.Groups
                                } else {
                                    Icons.Filled.School
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = boost.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = boost.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Text(
                        text = boost.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Sponsor / Program: ${boost.partnerName} • Target: ${boost.targetGrade}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            Text(
                text = "Note: Interactive Pro preview is stored locally on-device and ready to wire to Play Billing or backend subscriptions.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            BertDeveloperFooter(onAboutClick = onOpenAboutBert)
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    priceLabel: String,
    badge: String,
    accent: Color,
    features: List<String>,
    actionLabel: String,
    isCurrent: Boolean,
    testTag: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, accent.copy(alpha = if (isCurrent) 0.9f else 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = accent.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Icon(
                    imageVector = if (accent == SunsetPeach) {
                        Icons.Filled.WorkspacePremium
                    } else {
                        Icons.Filled.AutoAwesome
                    },
                    contentDescription = null,
                    tint = accent
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = priceLabel,
                style = MaterialTheme.typography.headlineSmall,
                color = accent,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(16.dp)
                        )
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Button(
                onClick = onAction,
                enabled = !isCurrent,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor = Color(0xFF070B18)
                )
            ) {
                Text(actionLabel, fontWeight = FontWeight.Bold)
            }
        }
    }
}
