package com.example.baglamaanalyzer.dsp

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.audio.TeeAudioProcessor
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

/**
 * Intercepts PCM audio data from ExoPlayer and performs pitch detection.
 */
@OptIn(UnstableApi::class)
class BaglamaPitchAnalyzer(
    private val onResult: (PitchResult) -> Unit
) : TeeAudioProcessor.AudioBufferSink {

    private var sampleRate = 48000
    
    // RMS based attack detection with Gate
    private var lastRms = 0f
    private val alpha = 0.4f // faster smoothing for responsiveness
    private val energyGate = 0.012f // tuned based on logs to ignore vocal artifacts

    // Using the custom profile for constraints (80Hz - 1200Hz)
    private val instrumentProfile = DivanBaglamaProfile.profile

    // YIN algorithm parameters - sensitivity tuning
    private val yinThreshold = 0.20f // more permissive to lock onto baglama harmonic content

    // --- Temporal Smoothing Buffer ---
    private val smoothingWindowSize = 5 // smaller window for faster response
    private val freqBuffer = mutableListOf<Float>()
    private var stableFrequency = 0f

    override fun flush(sampleRateHz: Int, channelCount: Int, encoding: Int) {
        this.sampleRate = sampleRateHz
    }

    override fun handleBuffer(buffer: ByteBuffer) {
        val remaining = buffer.remaining()
        if (remaining == 0) return

        val pcmData = ByteArray(remaining)
        buffer.duplicate().get(pcmData)
        
        val shortBuffer = ByteBuffer.wrap(pcmData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        val floats = FloatArray(shortBuffer.limit())
        var sumSquared = 0f
        
        var zeroCrossings = 0
        for (i in 0 until shortBuffer.limit()) {
            val sample = shortBuffer.get(i).toInt().toFloat() / 32768f
            floats[i] = sample
            sumSquared += sample * sample
            if (i > 0 && ((floats[i-1] >= 0 && floats[i] < 0) || (floats[i-1] < 0 && floats[i] >= 0))) {
                zeroCrossings++
            }
        }

        val rms = sqrt(sumSquared / floats.size.toFloat())
        val gatedRms = if (rms > energyGate) rms else 0f
        val smoothedRms = alpha * gatedRms + (1 - alpha) * lastRms
        lastRms = smoothedRms

        val zcr = zeroCrossings.toFloat() / floats.size.toFloat()
        
        // Attempt pitch detection if above energy threshold
        // ZCR filter relaxed slightly as baglama also produces crossings
        if (smoothedRms > energyGate && zcr < 0.45f) {
            val frequency = estimateFrequency(floats, sampleRate).toFloat()
            
            if (frequency > 0 && frequency >= instrumentProfile.minFreq && frequency <= instrumentProfile.maxFreq) {
                updateStableFrequency(frequency)
                
                if (stableFrequency > 0) {
                    onResult(
                        PitchResult(
                            frequency = stableFrequency,
                            rms = smoothedRms,
                            noteName = DivanBaglamaProfile.getTurkishNoteName(stableFrequency.toDouble()),
                            makam = DivanBaglamaProfile.estimateMakam(stableFrequency.toDouble())
                        )
                    )
                    return
                }
            }
        } else if (rms < energyGate * 0.3f) {
            // Only clear the UI "lock" if the sound truly stops
            freqBuffer.clear()
            stableFrequency = 0f
        }

        // Default state if no valid pitch found
        if (stableFrequency > 0) {
            onResult(
                PitchResult(
                    frequency = stableFrequency,
                    rms = smoothedRms,
                    noteName = DivanBaglamaProfile.getTurkishNoteName(stableFrequency.toDouble()),
                    makam = DivanBaglamaProfile.estimateMakam(stableFrequency.toDouble())
                )
            )
        } else {
            onResult(PitchResult(0f, smoothedRms, "...", "Scanning..."))
        }
    }

    private fun updateStableFrequency(newFreq: Float) {
        freqBuffer.add(newFreq)
        if (freqBuffer.size > smoothingWindowSize) {
            freqBuffer.removeAt(0)
        }

        if (freqBuffer.size >= 3) {
            val sorted = freqBuffer.sorted()
            val median = sorted[sorted.size / 2]
            
            // Relaxed variance to allow for vibrato/natural pitch changes
            val variance = sorted.last() - sorted.first()
            if (variance < 35f) {
                stableFrequency = median
            }
        } else {
            stableFrequency = newFreq
        }
    }

    private fun estimateFrequency(samples: FloatArray, sampleRate: Int): Double {
        val bufferSize = samples.size
        val minTau = (sampleRate / instrumentProfile.maxFreq).toInt().coerceAtLeast(4)
        val maxTau = (sampleRate / instrumentProfile.minFreq).toInt().coerceAtMost(bufferSize / 2)
        
        if (maxTau <= minTau) return 0.0
        
        val yinBuffer = FloatArray(maxTau)

        // Step 1: Difference Function
        for (tau in 0 until maxTau) {
            var diff = 0f
            for (i in 0 until maxTau) {
                val delta = samples[i] - samples[i + tau]
                diff += delta * delta
            }
            yinBuffer[tau] = diff
        }

        // Step 2: Cumulative Mean Normalized Difference Function
        yinBuffer[0] = 1f
        var runningSum = 0f
        for (tau in 1 until maxTau) {
            runningSum += yinBuffer[tau]
            if (runningSum != 0f) {
                yinBuffer[tau] *= tau / runningSum
            } else {
                yinBuffer[tau] = 1f
            }
        }

        // Step 3: Absolute Thresholding
        var tauEstimate = -1
        for (tau in minTau until maxTau) {
            if (yinBuffer[tau] < yinThreshold) {
                var t = tau
                while (t + 1 < maxTau && yinBuffer[t + 1] < yinBuffer[t]) {
                    t++
                }
                tauEstimate = t
                break
            }
        }

        if (tauEstimate == -1) return 0.0

        // Step 4: Parabolic Interpolation
        val betterTau: Float
        val x0 = if (tauEstimate < 1) 0 else tauEstimate - 1
        val x2 = if (tauEstimate + 1 < maxTau) tauEstimate + 1 else tauEstimate

        if (x0 == tauEstimate) {
            betterTau = if (yinBuffer[tauEstimate] <= yinBuffer[x2]) tauEstimate.toFloat() else x2.toFloat()
        } else if (x2 == tauEstimate) {
            betterTau = if (yinBuffer[tauEstimate] <= yinBuffer[x0]) tauEstimate.toFloat() else x0.toFloat()
        } else {
            val s0 = yinBuffer[x0]
            val s1 = yinBuffer[tauEstimate]
            val s2 = yinBuffer[x2]
            val denom = 2 * s1 - s2 - s0
            betterTau = if (denom != 0f) {
                tauEstimate + (s2 - s0) / (2 * denom)
            } else {
                tauEstimate.toFloat()
            }
        }

        return if (betterTau > 0) sampleRate.toDouble() / betterTau else 0.0
    }
}

data class PitchResult(
    val frequency: Float,
    val rms: Float,
    val noteName: String,
    val makam: String
)
