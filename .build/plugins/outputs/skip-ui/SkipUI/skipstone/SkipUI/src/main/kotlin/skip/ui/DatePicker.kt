// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Set

import skip.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

class DatePicker: View {

    internal val selection: Binding<Date>
    internal val label: ComposeBuilder
    internal val dateFormatter: DateFormatter?
    internal val timeFormatter: DateFormatter?

    constructor(selection: Binding<Date>, displayedComponents: DatePickerComponents = DatePickerComponents.of(DatePickerComponents.hourAndMinute, DatePickerComponents.date), label: () -> View) {
        this.selection = selection.sref()
        this.label = ComposeBuilder.from(label)
        if (displayedComponents.contains(DatePickerComponents.date)) {
            dateFormatter = DateFormatter()
            dateFormatter?.dateStyle = DateFormatter.Style.medium
            dateFormatter?.timeStyle = DateFormatter.Style.none
        } else {
            dateFormatter = null
        }
        if (displayedComponents.contains(DatePickerComponents.hourAndMinute)) {
            timeFormatter = DateFormatter()
            timeFormatter?.dateStyle = DateFormatter.Style.none
            timeFormatter?.timeStyle = DateFormatter.Style.short
        } else {
            timeFormatter = null
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(selection: Binding<Date>, in_: IntRange, displayedComponents: DatePickerComponents = DatePickerComponents.of(DatePickerComponents.hourAndMinute, DatePickerComponents.date), label: () -> View) {
        val range = in_
        this.selection = selection.sref()
        this.dateFormatter = null
        this.timeFormatter = null
        this.label = ComposeBuilder.from(label)
    }

    constructor(titleKey: LocalizedStringKey, selection: Binding<Date>, displayedComponents: DatePickerComponents = DatePickerComponents.of(DatePickerComponents.hourAndMinute, DatePickerComponents.date)): this(selection = selection, displayedComponents = displayedComponents, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(titleKey: LocalizedStringKey, selection: Binding<Date>, in_: IntRange, displayedComponents: DatePickerComponents = DatePickerComponents.of(DatePickerComponents.hourAndMinute, DatePickerComponents.date)): this(selection = selection, displayedComponents = displayedComponents, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(title: String, selection: Binding<Date>, displayedComponents: DatePickerComponents = DatePickerComponents.of(DatePickerComponents.hourAndMinute, DatePickerComponents.date)): this(selection = selection, displayedComponents = displayedComponents, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(title: String, selection: Binding<Date>, in_: IntRange, displayedComponents: DatePickerComponents = DatePickerComponents.of(DatePickerComponents.hourAndMinute, DatePickerComponents.date)): this(selection = selection, displayedComponents = displayedComponents, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val contentContext = context.content()
        val horizontalArrangement = Arrangement.spacedBy(8.dp)
        if (EnvironmentValues.shared._labelsHidden) {
            Row(modifier = context.modifier, horizontalArrangement = horizontalArrangement, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { -> ComposePickerContent(context = contentContext) }
        } else {
            ComposeContainer(modifier = context.modifier, fillWidth = true) { modifier ->
                Row(modifier = modifier, horizontalArrangement = horizontalArrangement, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { ->
                    label.Compose(context = contentContext)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1.0f))
                    ComposePickerContent(context = contentContext)
                }
            }
        }
    }

    @Composable
    private fun ComposePickerContent(context: ComposeContext) {
        val isDatePickerPresented = remember { -> mutableStateOf(false) }
        val isTimePickerPresented = remember { -> mutableStateOf(false) }
        val isEnabled = EnvironmentValues.shared.isEnabled
        val date = selection.wrappedValue.sref()
        val (hour, minute) = hourAndMinute(from = date)
        val currentLocale = Locale(androidx.compose.ui.platform.LocalConfiguration.current.locales[0])

        dateFormatter?.locale = currentLocale
        dateFormatter?.string(from = date)?.let { dateString ->
            val text = Text(verbatim = dateString)
            if (isEnabled) {
                ComposeTextButton(label = text, context = context) { -> isDatePickerPresented.value = true }
            } else {
                text.Compose(context = context)
            }
        }
        timeFormatter?.locale = currentLocale
        timeFormatter?.string(from = date)?.let { timeString ->
            val text = Text(verbatim = timeString)
            if (isEnabled) {
                ComposeTextButton(label = text, context = context) { -> isTimePickerPresented.value = true }
            } else {
                text.Compose(context = context)
            }
        }

        val tintColor = (EnvironmentValues.shared._tint ?: Color.accentColor).colorImpl()
        ComposeDatePicker(context = context, isPresented = isDatePickerPresented, tintColor = tintColor) { it -> didSelect(date = it, hour = hour, minute = minute) }
        ComposeTimePicker(context = context, isPresented = isTimePickerPresented, tintColor = tintColor, hour = hour, minute = minute) { it, it_1 -> didSelect(date = date, hour = it, minute = it_1) }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ComposeDatePicker(context: ComposeContext, isPresented: MutableState<Boolean>, tintColor: androidx.compose.ui.graphics.Color, dateSelected: (Date) -> Unit) {
        if (!isPresented.value) {
            return
        }
        val timeZoneOffset = Double(TimeZone.current.secondsFromGMT())
        val initialSeconds = selection.wrappedValue.timeIntervalSince1970 + timeZoneOffset
        val state = rememberDatePickerState(initialSelectedDateMillis = Long(initialSeconds * 1000.0))
        val colors = DatePickerDefaults.colors(selectedDayContainerColor = tintColor, selectedYearContainerColor = tintColor, todayDateBorderColor = tintColor, currentYearContentColor = tintColor)
        DatePickerDialog(onDismissRequest = { -> isPresented.value = false }, confirmButton = { ->
            Button(stringResource(android.R.string.ok), action = { -> isPresented.value = false }).padding().Compose(context = context)
        }, content = { -> DatePicker(state = state, modifier = context.modifier, colors = colors) })
        state.selectedDateMillis.sref()?.let { millis ->
            dateSelected(Date(timeIntervalSince1970 = Double(millis / 1000.0) - timeZoneOffset))
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ComposeTimePicker(context: ComposeContext, isPresented: MutableState<Boolean>, tintColor: androidx.compose.ui.graphics.Color, hour: Int, minute: Int, timeSelected: (Int, Int) -> Unit) {
        if (!isPresented.value) {
            return
        }
        val state = rememberTimePickerState(initialHour = hour, initialMinute = minute)
        val containerColor = tintColor.copy(alpha = 0.25f)
        val colors = TimePickerDefaults.colors(selectorColor = tintColor, periodSelectorSelectedContainerColor = containerColor, timeSelectorSelectedContainerColor = containerColor)
        DatePickerDialog(onDismissRequest = { -> isPresented.value = false }, confirmButton = { ->
            Button(stringResource(android.R.string.ok), action = { -> isPresented.value = false }).padding().Compose(context = context)
        }, content = { ->
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.TopCenter) { -> TimePicker(modifier = Modifier.padding(16.dp), state = state, colors = colors) }
        })
        timeSelected(state.hour, state.minute)
    }

    private fun didSelect(date: Date, hour: Int, minute: Int) {
        // Subtract out any existing hour and minute from the given date, then add the selected values
        val (baseHour, baseMinute) = hourAndMinute(from = date)
        val baseSeconds = date.timeIntervalSince1970 - Double(baseHour * 60 * 60) - Double(baseMinute * 60)
        val selectedSeconds = baseSeconds + Double(hour * 60 * 60) + Double(minute * 60)
        if (selectedSeconds != selection.wrappedValue.timeIntervalSince1970) {
            // selection is a 'let' constant so Swift would not allow us to assign to it
            selection.wrappedValue = Date(timeIntervalSince1970 = selectedSeconds)
        }
    }

    private fun hourAndMinute(from: Date): Tuple2<Int, Int> {
        val date = from
        val calendar = Calendar.current.sref()
        val timeComponents = calendar.dateComponents(setOf(Calendar.Component.hour, Calendar.Component.minute), from = date)
        return Tuple2(timeComponents.hour!!, timeComponents.minute!!)
    }

    companion object {
    }
}

class DatePickerComponents: OptionSet<DatePickerComponents, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): DatePickerComponents = DatePickerComponents(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: DatePickerComponents) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as DatePickerComponents
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = DatePickerComponents(this as MutableStruct)

    private fun assignfrom(target: DatePickerComponents) {
        this.rawValue = target.rawValue
    }

    companion object {

        val hourAndMinute = DatePickerComponents(rawValue = 1 shl 0)
        val date = DatePickerComponents(rawValue = 1 shl 1)

        fun of(vararg options: DatePickerComponents): DatePickerComponents {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return DatePickerComponents(rawValue = value)
        }
    }
}

class DatePickerStyle: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DatePickerStyle) return false
        return rawValue == other.rawValue
    }

    companion object {

        val automatic = DatePickerStyle(rawValue = 0)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val graphical = DatePickerStyle(rawValue = 1)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val wheel = DatePickerStyle(rawValue = 2)

        val compact = DatePickerStyle(rawValue = 3)
    }
}

