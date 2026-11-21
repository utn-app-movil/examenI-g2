package cr.ac.utn.examenI.domain.model

enum class mark_Channel(val displayName: String) {
    DIGITAL("Digital"),
    TV("TV"),
    RADIO("Radio");

    override fun toString(): String = displayName
}