package com.example.accessibilityplayground.util

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.example.accessibilityplayground.util.AccessibilityUtils.liveRegionRequest

/**
 * An object can be provided to any number of composables
 */
data class TraversalOrder(val number: Float) {
    constructor(number: Int) : this(number.toFloat())

    companion object {
        val DEFAULT = TraversalOrder(0)
    }
}

/**
 * Custom announcement that can be requested anytime
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Announcement(liveRegionRequest: LiveRegionRequest, contentDescription: String, role: Role? = null) {
    Box(
        modifier = Modifier
            .size(1.dp)
            .liveRegionRequest(liveRegionRequest)
            .semantics {
                invisibleToUser()
                this.contentDescription = contentDescription
                if (role != null) {
                    this.role = role
                }
            }
    )
}

object AccessibilityUtils {

    /**
     * isTraversalGroup is to also request talback focus on parent composables like Box, Column, Row
     */
    fun Modifier.traversalOrder(traversalOrder: TraversalOrder) = then(
        Modifier
            .semantics {
                isTraversalGroup = true
                traversalIndex = traversalOrder.number
            }
            .focusable()
    )

    /**
     * In case there is normal order between sibling composable, but custom order between children ones
     */
    fun Modifier.traversalGroup() = then(
        Modifier.traversalOrder(TraversalOrder.DEFAULT)
    )

    /**
     * You can trigger talkback focus by setting semantics { focused = true } for one frame
     * semantics(mergeDescendants = true) is necessary to work with parent composables like Box, Column, Row
     */
    fun Modifier.traversalFocusRequest(
        traversalFocusRequest: TraversalFocusRequest,
        mergeDescendants: Boolean = true,
        onFocusChanged: () -> Unit = {}
    ) = then(
        Modifier.semantics(mergeDescendants = mergeDescendants) {
            traversalFocusRequest.let {
                focused = it.isFocused
                if (it.isFocused) {
                    onFocusChanged()
                    it.onFocusStateChanged()
                }
            }
        }
    )

    /**
     * Triggering the accessibility reader programmatically
     */
    fun Modifier.liveRegionRequest(liveRegionRequest: LiveRegionRequest) = then(
        Modifier.semantics {
            liveRegionRequest.state?.let {
                if (it.isApplied) {
                    liveRegion = it.liveRegionMode
                } else {
                    liveRegionRequest.onStateApplied()
                }
            }
        }
    )

    /**
     * Example:
     * val (firstFocus, secondFocus, thirdFocus) = AccessibilityUtils.createTraversalRefs()
     */
    fun createTraversalRefs() = TraversalFactory

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

class LiveRegionRequest {
    private val liveRegionRequestState = LiveRegionRequestState()

    internal val state
        get() = liveRegionRequestState.value

    internal fun onStateApplied() {
        val appliedState = state?.copy(isApplied = true) ?: return
        liveRegionRequestState.onStateChanged(appliedState)
    }

    fun requestLiveRegionMode(liveRegionMode: LiveRegionMode) {
        liveRegionRequestState.onStateChanged(
            LiveRegionRequestState.LiveRegionState(
                liveRegionMode = liveRegionMode,
                isApplied = false
            )
        )
    }
}

internal class TraversalFocusRequestState : State<Boolean> {
    override var value: Boolean by mutableStateOf(false)

    fun onStateChanged(isFocused: Boolean) {
        value = isFocused
    }
}

internal class LiveRegionRequestState : State<LiveRegionRequestState.LiveRegionState?> {
    override var value: LiveRegionState? by mutableStateOf(null)

    data class LiveRegionState(
        val liveRegionMode: LiveRegionMode,
        val isApplied: Boolean
    )

    fun onStateChanged(liveRegionState: LiveRegionState) {
        value = liveRegionState
    }
}
