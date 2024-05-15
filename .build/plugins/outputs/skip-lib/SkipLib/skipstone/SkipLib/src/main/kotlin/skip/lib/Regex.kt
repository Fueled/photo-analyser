// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.lib

import skip.lib.Array

interface RegexComponent {
    //associatedtype RegexOutput // in Swift but not Kotlin

    /// The regular expression represented by this component.
    val regex: Regex
}

/// Kotlin representation of `Swift.Regex`.
class Regex: RegexComponent {
    private val _regex: kotlin.text.Regex

    constructor(string: String) {
        _regex = kotlin.text.Regex(string, RegexOption.MULTILINE)
    }

    override val regex: Regex
        get() = this

    /// The result of matching a regular expression against a string.
    class Match {
        /// The range of the overall match.
        // public let range: Range<String.Index>

        internal val match: kotlin.text.MatchResult

        val count: Int
            get() = match.groups.size

        operator fun get(index: Int): Regex.Match.MatchGroup = MatchGroup(group = match.groups.get(index))

        class MatchGroup {
            internal val group: kotlin.text.MatchGroup?

            // val range: IntRange

            val substring: Substring?
                get() {
                    val matchtarget_0 = group
                    if (matchtarget_0 != null) {
                        val group = matchtarget_0
                        return Substring(group.value, 0)
                    } else {
                        return null
                    }
                }

            constructor(group: kotlin.text.MatchGroup? = null) {
                this.group = group.sref()
            }

            companion object {
            }
        }

        constructor(match: kotlin.text.MatchResult) {
            this.match = match.sref()
        }

        companion object {
        }
    }

    fun matches(string: String): Array<Regex.Match> {
        var matches: Array<Regex.Match> = arrayOf()
        for (match in _regex.findAll(string)) {
            matches.append(Match(match = match))
        }
        return matches.sref()
    }

    fun replace(string: String, with: String): String {
        val replacement = with
        return _regex.replace(string, replacement)
    }

    companion object {
    }
}

