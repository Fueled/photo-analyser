// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import skip.foundation.*

class EditActions: OptionSet<EditActions, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): EditActions = EditActions(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: EditActions) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as EditActions
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = EditActions(this as MutableStruct)

    private fun assignfrom(target: EditActions) {
        this.rawValue = target.rawValue
    }

    companion object {

        val move = EditActions(rawValue = 1)
        val delete = EditActions(rawValue = 2)
        val all = EditActions(rawValue = 3)

        fun of(vararg options: EditActions): EditActions {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return EditActions(rawValue = value)
        }
    }
}

internal class EditActionsModifierView: ComposeModifierView {
    internal var isDeleteDisabled: Boolean? = null
    internal var isMoveDisabled: Boolean? = null

    internal constructor(view: View, isDeleteDisabled: Boolean? = null, isMoveDisabled: Boolean? = null): super(view = view, role = ComposeModifierRole.editActions) {
        val wrappedEditActionsView = Companion.unwrap(view = view)
        this.isDeleteDisabled = isDeleteDisabled ?: wrappedEditActionsView?.isDeleteDisabled
        this.isMoveDisabled = isMoveDisabled ?: wrappedEditActionsView?.isMoveDisabled
    }

    companion object {

        /// Return the edit actions modifier information for the given view.
        internal fun unwrap(view: View): EditActionsModifierView? {
            return view.strippingModifiers(until = { it -> it == ComposeModifierRole.editActions }, perform = { it -> it as? EditActionsModifierView })
        }
    }
}

fun <Element> Array<Element>.remove(atOffsets: IntSet) {
    val offsets = atOffsets
    for (offset in offsets.reversed()) {
        remove(at = offset)
    }
}

fun <Element> Array<Element>.move(fromOffsets: IntSet, toOffset: Int) {
    val source = fromOffsets
    val destination = toOffset
    if (source.count <= 1 && (destination == source[0] || destination == source[0] + 1)) {
        return
    }

    var moved: Array<Element> = arrayOf()
    var belowDestinationCount = 0
    for (offset in source.reversed()) {
        moved.append(remove(at = offset))
        if (offset < destination) {
            belowDestinationCount += 1
        }
    }
    for (m in moved.sref()) {
        insert(m, at = destination - belowDestinationCount)
    }
}
