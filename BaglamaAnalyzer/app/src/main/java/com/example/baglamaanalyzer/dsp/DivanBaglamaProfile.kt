package com.example.baglamaanalyzer.dsp

import com.nicos.pitchkit.tuner.models.InstrumentProfile

/**
 * Custom Instrument Profile for 7-String Divan Bağlama.
 * User's Specific Setup:
 * Bottom: 3x Steel -> 123.5 Hz (Si / B2)
 * Middle: 1x Thin Steel -> 164 Hz (Mi / E3), 1x Bass -> 82 Hz (Mi / E2)
 * Top: 1x Steel -> 110 Hz (La / A2), 1x Thick Bass -> 55 Hz (La / A1)
 */
object DivanBaglamaProfile {

    val profile = InstrumentProfile(
        name = "7-String Divan",
        minFreq = 50.0,  // Lowered for La (A1) @ 55Hz
        maxFreq = 1200.0,
        bassCeiling = 300.0,
        harmonicPivot = 150.0,
        openStrings = listOf(
            InstrumentProfile.OpenString("Si (B2)", 123.5),
            InstrumentProfile.OpenString("Mi (E3)", 164.8),
            InstrumentProfile.OpenString("Mi (E2)", 82.4),
            InstrumentProfile.OpenString("La (A2)", 110.0),
            InstrumentProfile.OpenString("La (A1)", 55.0)
        )
    )

    /**
     * Maps a frequency to a Turkish Microtonal (Koma) note name.
     * Calibrated for the user's B2 (Si) base tuning.
     */
    fun getTurkishNoteName(frequency: Double): String {
        if (frequency <= 0) return "..."
        return when (frequency) {
            // Low Register (Top Strings)
            in 53.0..57.0 -> "La (A1) - Bass"
            in 108.0..112.0 -> "La (A2)"
            in 80.0..84.0 -> "Mi (E2) - Bass"
            
            // Si (B) Base Register (Bottom Strings)
            in 121.5..125.5 -> "Si (B2)"
            in 136.0..140.0 -> "Do (C3)"
            in 144.0..148.0 -> "Do 4 Koma (Hicaz)"
            in 154.0..158.0 -> "Re (D3)"
            
            // Mi (E) Register (Middle Strings)
            in 162.0..166.0 -> "Mi (E3)"
            in 176.0..182.0 -> "Fa (F3)"
            in 188.0..194.0 -> "Fa 2 Koma (Eviç)"
            in 214.0..222.0 -> "Sol (G3)"
            in 240.0..250.0 -> "La (A3)"
            
            else -> "%.1f Hz".format(frequency)
        }
    }

    private var lastMakam = "Scanning..."

    /**
     * Estimates Makam based on detected frequency range.
     */
    fun estimateMakam(frequency: Double): String {
        val newMakam = when {
            // Si (B2) base tuning often places Karar on Si or Re
            frequency in 120.0..130.0 -> "Uşşak (Karar on Si)"
            frequency in 150.0..165.0 -> "Hüseyni / Uşşak"
            frequency > 240.0 -> "High Register"
            frequency > 50.0 -> "Central Anatolian"
            else -> "Scanning..."
        }

        if (newMakam != "Scanning..." || lastMakam == "Scanning...") {
            lastMakam = newMakam
        }
        
        return lastMakam
    }
}
