package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.SlideItem
import com.example.data.SlideLayoutType
import com.example.data.SlideTheme
import com.example.ui.theme.CoralRose
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MintSoft
import com.example.ui.theme.SunsetPeach

@Composable
fun BertDeveloperBadge(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.testTag("bert_developer_badge"),
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Code,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = "by ${stringResource(R.string.brand_creator)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun BertDeveloperFooter(
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NovaDeck Platform",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Designed & built by Bert Developer for SMP, SMA & young pros",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            TextButton(
                onClick = onAboutClick,
                modifier = Modifier.testTag("about_bert_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "About Bert Developer",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("About")
            }
        }
    }
}

@Composable
fun AboutBertDeveloperDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.about_bert_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.about_bert_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Platform Pillars",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "• Faster topic-to-slide layouts without box-dragging\n• NicAI: patient step-by-step SMP/SMA study tutor\n• Low-friction modules with attributed open resources",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_about_bert_dialog")
            ) {
                Text("Got it")
            }
        }
    )
}

@Composable
fun ProUpgradeInlineCard(
    title: String,
    description: String,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SunsetPeach.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = null,
                    tint = SunsetPeach
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedButton(
                onClick = onUpgradeClick,
                modifier = Modifier.testTag("inline_upgrade_button")
            ) {
                Text("Plans")
            }
        }
    }
}

@Composable
fun SlideCanvasCard(
    slide: SlideItem,
    slideIndex: Int,
    totalSlides: Int,
    theme: SlideTheme,
    isProUser: Boolean,
    modifier: Modifier = Modifier,
    isFullscreen: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val borderBrush = Brush.linearGradient(
        listOf(theme.accentColor.copy(alpha = 0.85f), theme.secondaryAccent.copy(alpha = 0.55f))
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .border(1.5.dp, borderBrush, MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = theme.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFullscreen) 12.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            theme.accentColor.copy(alpha = 0.14f),
                            theme.backgroundColor
                        )
                    )
                )
                .padding(if (isFullscreen) 24.dp else 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top bar: slide index & layout tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 24.dp, height = 6.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(theme.accentColor)
                    )
                    Text(
                        text = "SLIDE ${slideIndex + 1}/$totalSlides • ${slide.layoutType.label.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = theme.accentColor
                    )
                }
                if (theme.isProFeature && !isProUser) {
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = SunsetPeach.copy(alpha = 0.18f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Pro theme preview",
                                tint = SunsetPeach,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "PRO PREVIEW",
                                style = MaterialTheme.typography.labelSmall,
                                color = SunsetPeach
                            )
                        }
                    }
                }
            }

            // Inner structured card surface
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = theme.surfaceColor.copy(alpha = 0.92f)
            ) {
                Column(
                    modifier = Modifier.padding(if (isFullscreen) 20.dp else 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = slide.heading,
                        style = if (slide.layoutType == SlideLayoutType.TITLE || isFullscreen) {
                            MaterialTheme.typography.headlineMedium
                        } else {
                            MaterialTheme.typography.headlineSmall
                        },
                        color = theme.titleColor,
                        fontWeight = FontWeight.Bold
                    )

                    if (slide.subheading.isNotBlank()) {
                        Text(
                            text = slide.subheading,
                            style = MaterialTheme.typography.bodyMedium,
                            color = theme.accentColor
                        )
                    }

                    HorizontalDivider(color = theme.accentColor.copy(alpha = 0.22f))

                    if (slide.layoutType == SlideLayoutType.COMPARISON) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ComparisonColumn(
                                title = slide.leftColumnTitle.ifBlank { "Point A" },
                                points = slide.leftColumnPoints,
                                accent = theme.accentColor,
                                titleColor = theme.titleColor,
                                bodyColor = theme.bodyColor,
                                modifier = Modifier.weight(1f)
                            )
                            ComparisonColumn(
                                title = slide.rightColumnTitle.ifBlank { "Point B" },
                                points = slide.rightColumnPoints,
                                accent = theme.secondaryAccent,
                                titleColor = theme.titleColor,
                                bodyColor = theme.bodyColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            slide.bulletPoints.forEach { point ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 7.dp)
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(theme.accentColor)
                                    )
                                    Text(
                                        text = point,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = theme.bodyColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Slide footer attribution
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NovaDeck • ${theme.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = theme.bodyColor.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Bert Developer",
                    style = MaterialTheme.typography.labelSmall,
                    color = theme.accentColor.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun ComparisonColumn(
    title: String,
    points: List<String>,
    accent: Color,
    titleColor: Color,
    bodyColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = titleColor,
                fontWeight = FontWeight.Bold
            )
            points.forEach { p ->
                Text(
                    text = "• $p",
                    style = MaterialTheme.typography.bodySmall,
                    color = bodyColor
                )
            }
        }
    }
}

@Composable
fun TierStatusChip(
    isProActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.testTag("tier_status_chip"),
        shape = RoundedCornerShape(999.dp),
        color = if (isProActive) MintSoft.copy(alpha = 0.16f) else CoralRose.copy(alpha = 0.12f),
        border = BorderStroke(
            1.dp,
            if (isProActive) MintSoft else CoralRose.copy(alpha = 0.55f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = if (isProActive) MintSoft else CoralRose,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = if (isProActive) "PRO ACTIVE" else "FREE TIER",
                style = MaterialTheme.typography.labelSmall,
                color = if (isProActive) MintSoft else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
