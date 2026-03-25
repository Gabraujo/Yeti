package com.example.yetimobile.pdf

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.provider.MediaStore
import com.example.yetimobile.data.RecordItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {
    private const val PAGE_WIDTH = 595 // A4 72dpi width
    private const val PAGE_HEIGHT = 842

    fun export(
        context: Context,
        employee: String,
        date: String,
        time: String,
        items: List<RecordItem>
    ): Boolean {
        val doc = PdfDocument()
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        var pageNumber = 1
        var y = 60

        fun newPage(): PdfDocument.Page {
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber++).create()
            y = 60
            return doc.startPage(pageInfo)
        }

        var page = newPage()
        val canvas = page.canvas

        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("Requisicao - Yeti", 40f, 40f, paint)
        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("Funcionario: $employee", 40f, 60f, paint)
        canvas.drawText("Data/Hora: $date $time", 40f, 76f, paint)
        y = 100

        fun header() {
            paint.isFakeBoldText = true
            canvas.drawText("Setor", 40f, y.toFloat(), paint)
            canvas.drawText("Codigo", 140f, y.toFloat(), paint)
            canvas.drawText("Descricao", 240f, y.toFloat(), paint)
            canvas.drawText("Qtd", 440f, y.toFloat(), paint)
            canvas.drawText("Lotes", 500f, y.toFloat(), paint)
            paint.isFakeBoldText = false
            y += 16
        }

        header()

        items.forEach { item ->
            if (y > PAGE_HEIGHT - 60) {
                doc.finishPage(page)
                page = newPage()
                header()
            }
            canvas.drawText(item.sector, 40f, y.toFloat(), paint)
            canvas.drawText(item.code, 140f, y.toFloat(), paint)
            canvas.drawText(item.description.take(40), 240f, y.toFloat(), paint)
            canvas.drawText(item.quantity.toString(), 440f, y.toFloat(), paint)
            canvas.drawText(item.batches.toString(), 500f, y.toFloat(), paint)
            y += 16
        }

        doc.finishPage(page)

        val fileName = "requisicao-${sanitize(date)}.pdf"
        val ok = writeFile(context, doc, fileName)
        doc.close()
        return ok
    }

    private fun sanitize(str: String) = str.replace("/", "-").replace(" ", "_")

    private fun writeFile(context: Context, doc: PdfDocument, name: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, name)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, "Download/")
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: return false
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    doc.writeTo(os)
                }
            } else {
                val downloads = context.getExternalFilesDir(null) ?: context.filesDir
                val outFile = File(downloads, name)
                FileOutputStream(outFile).use { os ->
                    doc.writeTo(os)
                }
            }
            true
        } catch (t: Throwable) {
            false
        }
    }
}
