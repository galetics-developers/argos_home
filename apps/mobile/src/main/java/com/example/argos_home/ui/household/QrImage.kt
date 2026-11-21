package com.example.argos_home.ui.household

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.ui.theme.Primary700
import com.example.argos_home.ui.theme.White50
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/** Renders [content] as a real, scannable QR via ZXing; recomputed only when the content changes. */
@Composable
fun QrImage(
    content: String,
    modifier: Modifier = Modifier,
    sizeDp: Int = 180,
    foreground: Color = Primary700,
    background: Color = White50
) {
    val bitmap = remember(content, foreground, background) {
        generateQrBitmap(content, 512, foreground.toArgb(), background.toArgb())
    }
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = stringResource(R.string.qr_code_description),
        modifier = modifier.size(sizeDp.dp)
    )
}

// Encodes to a BitMatrix (margin + medium error correction), then paints each module into an ARGB bitmap.
private fun generateQrBitmap(content: String, size: Int, foreground: Int, background: Int): Bitmap {
    val hints = hashMapOf<EncodeHintType, Any>(
        EncodeHintType.MARGIN to 1,
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M
    )
    val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val width = matrix.width
    val height = matrix.height
    val pixels = IntArray(width * height) { index ->
        if (matrix.get(index % width, index / width)) foreground else background
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}
