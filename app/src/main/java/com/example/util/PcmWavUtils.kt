package com.example.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object PcmWavUtils {

  /**
   * Checks whether the input byte array already starts with a RIFF/WAVE header.
   * If it does, returns it directly. If not (meaning it is raw linear PCM audio),
   * prepends a standard 44-byte RIFF/WAVE PCM header.
   */
  fun ensureWavBytes(
    rawBytes: ByteArray,
    sampleRate: Int = 24000,
    channels: Short = 1,
    bitsPerSample: Short = 16
  ): ByteArray {
    if (rawBytes.size >= 4 &&
      rawBytes[0] == 'R'.code.toByte() &&
      rawBytes[1] == 'I'.code.toByte() &&
      rawBytes[2] == 'F'.code.toByte() &&
      rawBytes[3] == 'F'.code.toByte()
    ) {
      return rawBytes
    }

    val totalAudioLen = rawBytes.size
    val totalDataLen = totalAudioLen + 36
    val byteRate = sampleRate * channels * bitsPerSample / 8
    val blockAlign = (channels * bitsPerSample / 8).toShort()

    val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
    header.put("RIFF".toByteArray(Charsets.US_ASCII))
    header.putInt(totalDataLen)
    header.put("WAVE".toByteArray(Charsets.US_ASCII))
    header.put("fmt ".toByteArray(Charsets.US_ASCII))
    header.putInt(16) // Subchunk1Size for PCM
    header.putShort(1) // AudioFormat 1 = PCM
    header.putShort(channels)
    header.putInt(sampleRate)
    header.putInt(byteRate)
    header.putShort(blockAlign)
    header.putShort(bitsPerSample)
    header.put("data".toByteArray(Charsets.US_ASCII))
    header.putInt(totalAudioLen)

    val out = ByteArray(44 + rawBytes.size)
    System.arraycopy(header.array(), 0, out, 0, 44)
    System.arraycopy(rawBytes, 0, out, 44, rawBytes.size)
    return out
  }

  /**
   * Writes the given audio bytes to a temporary WAV cache file for playback by MediaPlayer.
   */
  fun writeWavToCache(context: Context, audioBytes: ByteArray, filenamePrefix: String = "tutor_speech"): File {
    val cacheDir = context.cacheDir
    val tempFile = File(cacheDir, "${filenamePrefix}_${System.currentTimeMillis()}.wav")
    FileOutputStream(tempFile).use { fos ->
      fos.write(audioBytes)
      fos.flush()
    }
    return tempFile
  }
}
