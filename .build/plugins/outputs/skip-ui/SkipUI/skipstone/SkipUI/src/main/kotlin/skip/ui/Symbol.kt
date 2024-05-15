package skip.ui

import skip.lib.*


enum class SymbolRenderingMode {
    monochrome,
    multicolor,
    hierarchical,
    palette;

    companion object {
    }
}

class SymbolVariants: Sendable {

    val circle: SymbolVariants
        get() = this
    val square: SymbolVariants
        get() = this
    val rectangle: SymbolVariants
        get() = this

    val fill: SymbolVariants
        get() = this

    val slash: SymbolVariants
        get() = this

    fun contains(other: SymbolVariants): Boolean {
        fatalError()
    }

    override fun equals(other: Any?): Boolean = other is SymbolVariants

    override fun hashCode(): Int = "SymbolVariants".hashCode()

    companion object {
        val none = SymbolVariants()
        val circle = SymbolVariants()
        val square = SymbolVariants()
        val rectangle = SymbolVariants()

        val fill = SymbolVariants()

        val slash = SymbolVariants()
    }
}


