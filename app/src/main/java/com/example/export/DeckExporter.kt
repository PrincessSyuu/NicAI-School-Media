package com.example.export

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import com.example.data.PresentationDeck
import com.example.data.SlideLayoutType
import java.io.File
import java.io.FileOutputStream

object DeckExporter {

    fun toMarkdown(deck: PresentationDeck): String = buildString {
        appendLine("# ${deck.title}")
        appendLine("*${deck.subtitle}* — Audience: ${deck.audience} | Theme: ${deck.theme.displayName}")
        appendLine("Created with NovaDeck by Bert Developer")
        appendLine()
        deck.slides.forEachIndexed { index, slide ->
            appendLine("---")
            appendLine("## Slide ${index + 1}: ${slide.heading}")
            if (slide.subheading.isNotBlank()) {
                appendLine("**${slide.subheading}**")
            }
            appendLine()
            if (slide.layoutType == SlideLayoutType.COMPARISON) {
                appendLine("### ${slide.leftColumnTitle}")
                slide.leftColumnPoints.forEach { appendLine("- $it") }
                appendLine()
                appendLine("### ${slide.rightColumnTitle}")
                slide.rightColumnPoints.forEach { appendLine("- $it") }
            } else {
                slide.bulletPoints.forEach { appendLine("- $it") }
            }
            if (slide.speakerNotes.isNotBlank()) {
                appendLine()
                appendLine("> Speaker Notes: ${slide.speakerNotes}")
            }
            appendLine()
        }
    }

    fun toHtmlSlides(deck: PresentationDeck): String {
        val bgHex = String.format("#%06X", 0xFFFFFF and deck.theme.backgroundColor.toArgb())
        val surfaceHex = String.format("#%06X", 0xFFFFFF and deck.theme.surfaceColor.toArgb())
        val accentHex = String.format("#%06X", 0xFFFFFF and deck.theme.accentColor.toArgb())
        val titleHex = String.format("#%06X", 0xFFFFFF and deck.theme.titleColor.toArgb())
        val bodyHex = String.format("#%06X", 0xFFFFFF and deck.theme.bodyColor.toArgb())

        return buildString {
            appendLine("<!DOCTYPE html>")
            appendLine("<html lang=\"en\"><head><meta charset=\"UTF-8\">")
            appendLine("<title>${escapeHtml(deck.title)} — NovaDeck</title>")
            appendLine("<style>")
            appendLine("body{margin:0;font-family:system-ui,sans-serif;background:$bgHex;color:$bodyHex;padding:24px;}")
            appendLine(".slide{background:$surfaceHex;border-left:6px solid $accentHex;border-radius:16px;padding:32px;margin:24px auto;max-width:860px;box-shadow:0 12px 30px rgba(0,0,0,0.2);}")
            appendLine("h1,h2{color:$titleHex;margin-top:0;} .tag{color:$accentHex;font-size:12px;letter-spacing:1px;text-transform:uppercase;}")
            appendLine(".cols{display:grid;grid-template-columns:1fr 1fr;gap:24px;} footer{text-align:center;opacity:0.7;margin-top:32px;font-size:13px;}")
            appendLine("</style></head><body>")
            deck.slides.forEachIndexed { index, slide ->
                appendLine("<section class=\"slide\">")
                appendLine("<div class=\"tag\">Slide ${index + 1} • ${slide.layoutType.label}</div>")
                appendLine("<h2>${escapeHtml(slide.heading)}</h2>")
                if (slide.subheading.isNotBlank()) {
                    appendLine("<p><strong>${escapeHtml(slide.subheading)}</strong></p>")
                }
                if (slide.layoutType == SlideLayoutType.COMPARISON) {
                    appendLine("<div class=\"cols\"><div><h3>${escapeHtml(slide.leftColumnTitle)}</h3><ul>")
                    slide.leftColumnPoints.forEach { appendLine("<li>${escapeHtml(it)}</li>") }
                    appendLine("</ul></div><div><h3>${escapeHtml(slide.rightColumnTitle)}</h3><ul>")
                    slide.rightColumnPoints.forEach { appendLine("<li>${escapeHtml(it)}</li>") }
                    appendLine("</ul></div></div>")
                } else {
                    appendLine("<ul>")
                    slide.bulletPoints.forEach { appendLine("<li>${escapeHtml(it)}</li>") }
                    appendLine("</ul>")
                }
                appendLine("</section>")
            }
            appendLine("<footer>Generated with NovaDeck Slide Studio • Created by Bert Developer</footer>")
            appendLine("</body></html>")
        }
    }

    fun copyToClipboard(context: Context, label: String, content: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, content))
    }

    fun shareTextOutline(context: Context, deck: PresentationDeck) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, deck.title)
            putExtra(Intent.EXTRA_TEXT, toMarkdown(deck))
        }
        context.startActivity(Intent.createChooser(intent, "Share Slide Outline"))
    }

    fun exportAndSharePdf(context: Context, deck: PresentationDeck) {
        val pdfDocument = PdfDocument()
        val pageWidth = 960
        val pageHeight = 540

        val bgPaint = Paint().apply { color = deck.theme.backgroundColor.toArgb() }
        val cardPaint = Paint().apply { color = deck.theme.surfaceColor.toArgb() }
        val accentPaint = Paint().apply { color = deck.theme.accentColor.toArgb() }

        val tagPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = deck.theme.accentColor.toArgb()
            textSize = 16f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = deck.theme.titleColor.toArgb()
            textSize = 34f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val subPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = deck.theme.bodyColor.toArgb()
            textSize = 20f
        }
        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = deck.theme.bodyColor.toArgb()
            textSize = 20f
        }
        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = deck.theme.bodyColor.toArgb()
            alpha = 160
            textSize = 14f
        }

        deck.slides.forEachIndexed { index, slide ->
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)
            canvas.drawRoundRect(36f, 36f, pageWidth - 36f, pageHeight - 36f, 20f, 20f, cardPaint)
            canvas.drawRect(36f, 36f, 48f, pageHeight - 36f, accentPaint)

            canvas.drawText(
                "SLIDE ${index + 1} / ${deck.slides.size} • ${slide.layoutType.label.uppercase()}",
                76f,
                84f,
                tagPaint
            )

            val titleLayout = StaticLayout.Builder.obtain(
                slide.heading, 0, slide.heading.length, titlePaint, pageWidth - 150
            ).build()
            canvas.save()
            canvas.translate(76f, 104f)
            titleLayout.draw(canvas)
            canvas.restore()

            var yCursor = 104f + titleLayout.height + 16f
            if (slide.subheading.isNotBlank()) {
                val subLayout = StaticLayout.Builder.obtain(
                    slide.subheading, 0, slide.subheading.length, subPaint, pageWidth - 150
                ).build()
                canvas.save()
                canvas.translate(76f, yCursor)
                subLayout.draw(canvas)
                canvas.restore()
                yCursor += subLayout.height + 24f
            }

            if (slide.layoutType == SlideLayoutType.COMPARISON) {
                val colWidth = (pageWidth - 190) / 2
                val leftText = buildString {
                    appendLine(slide.leftColumnTitle)
                    slide.leftColumnPoints.forEach { appendLine("• $it") }
                }
                val rightText = buildString {
                    appendLine(slide.rightColumnTitle)
                    slide.rightColumnPoints.forEach { appendLine("• $it") }
                }
                drawWrappedBlock(canvas, leftText, bodyPaint, 76f, yCursor, colWidth)
                drawWrappedBlock(canvas, rightText, bodyPaint, 76f + colWidth + 32f, yCursor, colWidth)
            } else {
                val bulletsText = slide.bulletPoints.joinToString("\n\n") { "• $it" }
                drawWrappedBlock(canvas, bulletsText, bodyPaint, 76f, yCursor, pageWidth - 150)
            }

            canvas.drawText(
                "${deck.title} • NovaDeck by Bert Developer",
                76f,
                pageHeight - 54f,
                footerPaint
            )
            pdfDocument.finishPage(page)
        }

        val exportDir = File(context.cacheDir, "decks").apply { mkdirs() }
        val safeName = deck.title.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_').ifBlank { "novadeck" }
        val pdfFile = File(exportDir, "${safeName}.pdf")
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "${deck.title} (NovaDeck PDF)")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Export Presentation PDF"))
    }

    private fun drawWrappedBlock(
        canvas: Canvas,
        text: String,
        paint: TextPaint,
        x: Float,
        y: Float,
        width: Int
    ) {
        if (text.isBlank()) return
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .build()
        canvas.save()
        canvas.translate(x, y)
        layout.draw(canvas)
        canvas.restore()
    }

    private fun escapeHtml(text: String): String =
        text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
}
