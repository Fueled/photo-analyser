// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription

class AccessibilityActionKind: Sendable {

    constructor(named: Text) {
    }

    private constructor() {
    }

    override fun equals(other: Any?): Boolean = other is AccessibilityActionKind

    companion object {
        val default = AccessibilityActionKind()
        val escape = AccessibilityActionKind()
        val magicTap = AccessibilityActionKind()
    }
}

enum class AccessibilityAdjustmentDirection: Sendable {
    increment,
    decrement;

    companion object {
    }
}

enum class AccessibilityChildBehavior {
    ignore,
    contain,
    combine;

    companion object {
    }
}

class AccessibilityCustomContentKey {
    constructor(label: Text, id: String) {
    }

    constructor(labelKey: LocalizedStringKey, id: String) {
    }

    constructor(labelKey: LocalizedStringKey) {
    }

    override fun equals(other: Any?): Boolean = other is AccessibilityCustomContentKey

    companion object {
    }
}

class AccessibilityDirectTouchOptions: OptionSet<AccessibilityDirectTouchOptions, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): AccessibilityDirectTouchOptions = AccessibilityDirectTouchOptions(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: AccessibilityDirectTouchOptions) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as AccessibilityDirectTouchOptions
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = AccessibilityDirectTouchOptions(this as MutableStruct)

    private fun assignfrom(target: AccessibilityDirectTouchOptions) {
        this.rawValue = target.rawValue
    }

    companion object {

        val silentOnTouch = AccessibilityDirectTouchOptions(rawValue = 1)
        val requiresActivation = AccessibilityDirectTouchOptions(rawValue = 2)

        fun of(vararg options: AccessibilityDirectTouchOptions): AccessibilityDirectTouchOptions {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return AccessibilityDirectTouchOptions(rawValue = value)
        }
    }
}

enum class AccessibilityHeadingLevel(override val rawValue: UInt, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<UInt> {
    unspecified(UInt(0)),
    h1(UInt(1)),
    h2(UInt(2)),
    h3(UInt(3)),
    h4(UInt(4)),
    h5(UInt(5)),
    h6(UInt(6));

    companion object {
    }
}

fun AccessibilityHeadingLevel(rawValue: UInt): AccessibilityHeadingLevel? {
    return when (rawValue) {
        UInt(0) -> AccessibilityHeadingLevel.unspecified
        UInt(1) -> AccessibilityHeadingLevel.h1
        UInt(2) -> AccessibilityHeadingLevel.h2
        UInt(3) -> AccessibilityHeadingLevel.h3
        UInt(4) -> AccessibilityHeadingLevel.h4
        UInt(5) -> AccessibilityHeadingLevel.h5
        UInt(6) -> AccessibilityHeadingLevel.h6
        else -> null
    }
}

enum class AccessibilityLabeledPairRole {
    label,
    content;

    companion object {
    }
}

interface AccessibilityRotorContent {
}

class AccessibilityRotorEntry<ID>: AccessibilityRotorContent {
    constructor(label: Text, id: ID, textRange: IntRange? = null, prepare: () -> Unit = { ->  }) {
    }

    constructor(label: Text, id: ID, in_: Namespace.ID, textRange: IntRange? = null, prepare: () -> Unit = { ->  }) {
    }

    constructor(label: Text? = null, textRange: IntRange, prepare: () -> Unit = { ->  }) {
    }

    constructor(labelKey: LocalizedStringKey, id: ID, textRange: IntRange? = null, prepare: () -> Unit = { ->  }) {
    }

    constructor(label: String, id: ID, textRange: IntRange? = null, prepare: () -> Unit = { ->  }) {
    }

    constructor(labelKey: LocalizedStringKey, id: ID, in_: Namespace.ID, textRange: IntRange? = null, prepare: () -> Unit = { ->  }) {
    }

    constructor(labelKey: LocalizedStringKey, textRange: IntRange, prepare: () -> Unit = { ->  }) {
    }

    constructor(label: String, textRange: IntRange, prepare: () -> Unit = { ->  }) {
    }

    companion object {
    }
}

class AccessibilitySystemRotor: Sendable {

    companion object {
        fun links(visited: Boolean): AccessibilitySystemRotor = AccessibilitySystemRotor()

        val links = AccessibilitySystemRotor()

        fun headings(level: AccessibilityHeadingLevel): AccessibilitySystemRotor = AccessibilitySystemRotor()

        val headings = AccessibilitySystemRotor()
        val boldText = AccessibilitySystemRotor()
        val italicText = AccessibilitySystemRotor()
        val underlineText = AccessibilitySystemRotor()
        val misspelledWords = AccessibilitySystemRotor()
        val images = AccessibilitySystemRotor()
        val textFields = AccessibilitySystemRotor()
        val tables = AccessibilitySystemRotor()
        val lists = AccessibilitySystemRotor()
        val landmarks = AccessibilitySystemRotor()
    }
}

class AccessibilityTechnologies: OptionSet<AccessibilityTechnologies, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): AccessibilityTechnologies = AccessibilityTechnologies(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: AccessibilityTechnologies) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as AccessibilityTechnologies
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = AccessibilityTechnologies(this as MutableStruct)

    private fun assignfrom(target: AccessibilityTechnologies) {
        this.rawValue = target.rawValue
    }

    companion object {

        val voiceOver = AccessibilityTechnologies(rawValue = 1)
        val switchControl = AccessibilityTechnologies(rawValue = 2)

        fun of(vararg options: AccessibilityTechnologies): AccessibilityTechnologies {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return AccessibilityTechnologies(rawValue = value)
        }
    }
}

enum class AccessibilityTextContentType: Sendable {
    plain,
    console,
    fileSystem,
    messaging,
    narrative,
    sourceCode,
    spreadsheet,
    wordProcessing;

    companion object {
    }
}

class AccessibilityTraits: OptionSet<AccessibilityTraits, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): AccessibilityTraits = AccessibilityTraits(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: AccessibilityTraits) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as AccessibilityTraits
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = AccessibilityTraits(this as MutableStruct)

    private fun assignfrom(target: AccessibilityTraits) {
        this.rawValue = target.rawValue
    }

    companion object {

        val isButton = AccessibilityTraits(rawValue = 1 shl 0)
        val isHeader = AccessibilityTraits(rawValue = 1 shl 1)
        val isSelected = AccessibilityTraits(rawValue = 1 shl 2)
        val isLink = AccessibilityTraits(rawValue = 1 shl 3)
        val isSearchField = AccessibilityTraits(rawValue = 1 shl 4)
        val isImage = AccessibilityTraits(rawValue = 1 shl 5)
        val playsSound = AccessibilityTraits(rawValue = 1 shl 6)
        val isKeyboardKey = AccessibilityTraits(rawValue = 1 shl 7)
        val isStaticText = AccessibilityTraits(rawValue = 1 shl 8)
        val isSummaryElement = AccessibilityTraits(rawValue = 1 shl 9)
        val updatesFrequently = AccessibilityTraits(rawValue = 1 shl 10)
        val startsMediaSession = AccessibilityTraits(rawValue = 1 shl 11)
        val allowsDirectInteraction = AccessibilityTraits(rawValue = 1 shl 12)
        val causesPageTurn = AccessibilityTraits(rawValue = 1 shl 13)
        val isModal = AccessibilityTraits(rawValue = 1 shl 14)
        val isToggle = AccessibilityTraits(rawValue = 1 shl 15)

        fun of(vararg options: AccessibilityTraits): AccessibilityTraits {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return AccessibilityTraits(rawValue = value)
        }
    }
}

class AccessibilityZoomGestureAction {
    enum class Direction {
        zoomIn,
        zoomOut;

        companion object {
        }
    }

    val direction: AccessibilityZoomGestureAction.Direction
    val location: UnitPoint
    val point: CGPoint

    constructor(direction: AccessibilityZoomGestureAction.Direction, location: UnitPoint, point: CGPoint) {
        this.direction = direction
        this.location = location.sref()
        this.point = point.sref()
    }

    companion object {
    }
}

