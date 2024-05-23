package com.example.accessibilityplayground.util

import androidx.compose.foundation.focusable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex

/**
 * An object can be provided to any number of composables
 */
data class TraversalOrder(val number: Float) {
    constructor(number: Int) : this(number.toFloat())
}

object AccessibilityUtils {

    /**
     * isTraversalGroup is to also request talback focus on parent composables like Box, Column, Row
     */
    fun Modifier.traversalOrder(traversalOrder: TraversalOrder) = this.then(
        Modifier
            .semantics {
                isTraversalGroup = true
                traversalIndex = traversalOrder.number
            }
            .focusable()
    )

    /**
     * You can trigger talkback focus by setting semantics { focused = true } for one frame
     * semantics(mergeDescendants = true) is necessary to work with parent composables like Box, Column, Row
     */
    fun Modifier.traversalFocusRequest(traversalRequestFocus: TraversalFocusRequest, mergeDescendants: Boolean = true) =
        then(Modifier.semantics(mergeDescendants = mergeDescendants) {
            traversalRequestFocus.let {
                focused = it.isFocused
                if (it.isFocused) {
                    it.onFocusStateChanged()
                }
            }
        })

    /**
     * Example:
     * val (firstFocus, secondFocus, thirdFocus) = AccessibilityUtils.createRefs()
     */
    fun createRefs() = TraversalFactory

    @Suppress("MagicNumber")
    object TraversalFactory {
        operator fun component1() = TraversalOrder(1)
        operator fun component2() = TraversalOrder(2)
        operator fun component3() = TraversalOrder(3)
        operator fun component4() = TraversalOrder(4)
        operator fun component5() = TraversalOrder(5)
        operator fun component6() = TraversalOrder(6)
        operator fun component7() = TraversalOrder(7)
        operator fun component8() = TraversalOrder(8)
        operator fun component9() = TraversalOrder(9)
        operator fun component10() = TraversalOrder(10)
        operator fun component11() = TraversalOrder(11)
        operator fun component12() = TraversalOrder(12)
        operator fun component13() = TraversalOrder(13)
        operator fun component14() = TraversalOrder(14)
        operator fun component15() = TraversalOrder(15)
        operator fun component16() = TraversalOrder(16)
    }
}

/**
 * An object can be provided to only one composable
 */
class TraversalFocusRequest {
    private val traversalFocusRequestState = TraversalFocusRequestState()
    internal val isFocused
        get() = traversalFocusRequestState.value

    internal fun onFocusStateChanged() {
        if (isFocused) {
            traversalFocusRequestState.onStateChanged(isFocused = false)
        }
    }

    fun requestTraversalFocus() {
        traversalFocusRequestState.onStateChanged(isFocused = true)
    }
}

internal class TraversalFocusRequestState : State<Boolean> {
    override var value: Boolean by mutableStateOf(false)

    fun onStateChanged(isFocused: Boolean) {
        value = isFocused
    }
}
