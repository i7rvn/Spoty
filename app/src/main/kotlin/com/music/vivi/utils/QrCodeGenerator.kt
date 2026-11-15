package com.music.vivi.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * Generates a QR code bitmap for a given piece of text (typically a share link),
 * so a song, playlist, or profile can be shared by letting someone scan their
 * phone instead of sending a link.
 */
object QrCodeGenerator {

    fun generate(
        content: String,
        sizePx: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
    ): Bitmap {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 1,
        )
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        for (x in 0 until sizePx) {
            for (y in 0 until sizePx) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) foregroundColor else backgroundColor)
            }
        }
        return bitmap
    }
}
