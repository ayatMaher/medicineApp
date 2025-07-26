package com.example.medicineapplication.validator

class DrugNameExtractor {
    fun extract(text: String): List<String> {
        val drugNameMap = mapOf(
            "panadol" to "بنادول", "brufen" to "بروفين", "augmentin" to "أوجمنتين",
            "amoxicillin" to "أموكسيسيلين", "aspirin" to "أسبرين", "voltaren" to "فولتارين",
            "paracetamol" to "باراسيتامول", "ibuprofen" to "إيبوبروفين", "cataflam" to "كاتافلام", "flagyl" to "فلاجيل",
            "zithromax" to "زيثروماكس", "clavulin" to "كلافولين", "omeprazole" to "أوميبرازول",
            "nexium" to "نيكسيم", "ciprofloxacin" to "سيبروفلوكساسين", "amoxil" to "أموكسيل", "cetirizine" to "سيتريزين"
        )

        val cleanedText = text.replace(Regex("[^A-Za-z0-9\\s]"), " ").lowercase()

        return drugNameMap.keys.filter { cleanedText.contains(it) }
            .mapNotNull { drugNameMap[it] }
            .distinct()
    }
}