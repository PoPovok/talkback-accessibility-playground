package com.example.accessibilityplayground.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.tooling.preview.Preview
import com.example.accessibilityplayground.ui.theme.AccessibilityPlaygroundTheme
import com.example.accessibilityplayground.util.AccessibilityUtils
import com.example.accessibilityplayground.util.AccessibilityUtils.liveRegionRequest
import com.example.accessibilityplayground.util.AccessibilityUtils.traversalFocusRequest
import com.example.accessibilityplayground.util.AccessibilityUtils.traversalOrder
import com.example.accessibilityplayground.util.Announcement
import com.example.accessibilityplayground.util.LiveRegionRequest
import com.example.accessibilityplayground.util.TraversalFocusRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ColumnScope.CustomBox(color: Color, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .background(color)
            .fillMaxWidth()
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun NormalTraversalOrder() {
    Column(modifier = Modifier.fillMaxSize()) {
        CustomBox(color = Color.White) { Text(text = "First") }
        CustomBox(color = Color.Yellow) { Text(text = "Second") }
        CustomBox(color = Color.Green) { Text(text = "Third") }
    }
}

@Composable
fun ModifiedTraversalOrder1() {
    Column(modifier = Modifier.fillMaxSize()) {
        val (
            white,
            yellow,
            green
        ) = AccessibilityUtils.createTraversalRefs()

        CustomBox(modifier = Modifier.traversalOrder(yellow), color = Color.Yellow) {
            Text(text = "Second")
        }
        CustomBox(modifier = Modifier.traversalOrder(white), color = Color.White) {
            Text(text = "First")
        }
        CustomBox(modifier = Modifier.traversalOrder(green), color = Color.Green) {
            Text(text = "Third")
        }
    }
}

@Composable
fun ModifiedTraversalOrder2() {
    Column(modifier = Modifier.fillMaxSize()) {
        val (
            white,
            yellow,
            green,
            blue
        ) = AccessibilityUtils.createTraversalRefs()

        CustomBox(modifier = Modifier.traversalOrder(yellow), color = Color.Yellow) {
            Text(text = "Second")
        }
        CustomBox(modifier = Modifier.traversalOrder(blue), color = Color.Magenta) {
            Text(text = "Fourth")
        }
        CustomBox(modifier = Modifier.traversalOrder(white), color = Color.White) {
            Text(text = "First")
        }
        CustomBox(modifier = Modifier.traversalOrder(green), color = Color.Green) {
            Text(text = "Third")
        }
    }
}

@Composable
fun DeepTraversalOrder1() {
    Column(modifier = Modifier.fillMaxSize()) {
        val (
            first,
            second,
            third
        ) = AccessibilityUtils.createTraversalRefs()

        Row(
            modifier = Modifier
                .weight(1f)
                .traversalOrder(second)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                CustomBox(color = Color.Yellow) { Text(text = "Second") }
                CustomBox(color = Color.Yellow) { Text(text = "Fourth") }
            }
            Column(modifier = Modifier.weight(1f)) {
                CustomBox(color = Color.Yellow) { Text(text = "Third") }
                CustomBox(color = Color.Yellow) { Text(text = "Fifth") }
            }
        }
        CustomBox(modifier = Modifier.traversalOrder(first), color = Color.White) {
            Text(text = "First")
        }
        CustomBox(modifier = Modifier.traversalOrder(third), color = Color.Green) {
            Text(text = "Sixth")
        }
    }
}

@Composable
fun DeepTraversalOrder2() {
    Column(modifier = Modifier.fillMaxSize()) {
        val (
            first,
            second,
            third,
            fourth,
            fifth,
            sixth
        ) = AccessibilityUtils.createTraversalRefs()

        Row(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.weight(1f)) {
                CustomBox(modifier = Modifier.traversalOrder(fourth), color = Color.Yellow) {
                    Text(text = "Fourth")
                }
                CustomBox(modifier = Modifier.traversalOrder(third), color = Color.Yellow) {
                    Text(text = "Third")
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                CustomBox(modifier = Modifier.traversalOrder(second), color = Color.Yellow) {
                    Text(text = "Second")
                }
                CustomBox(modifier = Modifier.traversalOrder(fifth), color = Color.Yellow) {
                    Text(text = "Fifth")
                }
            }
        }
        CustomBox(modifier = Modifier.traversalOrder(first), color = Color.White) {
            Text(text = "First")
        }
        CustomBox(modifier = Modifier.traversalOrder(sixth), color = Color.Green) {
            Text(text = "Sixth")
        }
    }
}

@Composable
fun MultiLevelDeepTraversalOrder() {
    Column(modifier = Modifier.fillMaxSize()) {
        val (
            rootFirst,
            rootSecond,
            rootThird
        ) = AccessibilityUtils.createTraversalRefs()

        Row(
            modifier = Modifier
                .weight(1f)
                .traversalOrder(rootSecond)
        ) {
            val (
                yellowFirst,
                yellowSecond,
                yellowThird,
                yellowFourth
            ) = AccessibilityUtils.createTraversalRefs()

            Column(modifier = Modifier.weight(1f)) {
                CustomBox(modifier = Modifier.traversalOrder(yellowThird), color = Color.Yellow) {
                    Text(text = "3")
                }
                CustomBox(modifier = Modifier.traversalOrder(yellowSecond), color = Color.Yellow) {
                    Text(text = "2")
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                CustomBox(modifier = Modifier.traversalOrder(yellowFirst), color = Color.Yellow) {
                    Text(text = "1")
                }
                CustomBox(modifier = Modifier.traversalOrder(yellowFourth), color = Color.Yellow) {
                    Text(text = "4")
                }
            }
        }
        CustomBox(modifier = Modifier.traversalOrder(rootFirst), color = Color.White) {
            Text(text = "First")
        }
        CustomBox(modifier = Modifier.traversalOrder(rootThird), color = Color.Green) {
            Text(text = "Third")
        }
    }
}

@Composable
fun InterruptingTraversalOrder() {
    Column(modifier = Modifier.fillMaxSize()) {
        val traversalFocusRequest = TraversalFocusRequest()

        CustomBox(color = Color.White) { Text(text = "First") }
        CustomBox(color = Color.Yellow) {
            Text(modifier = Modifier.traversalFocusRequest(traversalFocusRequest), text = "Second")
        }
        CustomBox(color = Color.Green) { Text(text = "Third") }
        CustomBox(color = Color.Magenta) { Text(text = "Fourth") }
        CustomBox(color = Color.Cyan) { Text(text = "Fifth") }
        Button(modifier = Modifier.fillMaxWidth(), onClick = { traversalFocusRequest.requestTraversalFocus() }) {
            Text(text = "Jump to Second")
        }
    }
}

@Composable
fun InterruptingLiveRegion() {
    Column(modifier = Modifier.fillMaxSize()) {
        val liveRegionRequest2 = LiveRegionRequest()
        val liveRegionRequest3 = LiveRegionRequest()
        val liveRegionRequestAnnouncement = LiveRegionRequest()

        val coroutineScope = rememberCoroutineScope()
        val counterAssertive = remember { mutableIntStateOf(0) }
        val counterPolite = remember { mutableIntStateOf(0) }
        val waitForAction: (counter: MutableIntState, action: () -> Unit) -> Unit = { counter, action ->
            coroutineScope.launch {
                counter.intValue = 3
                while (counter.intValue != 0) {
                    delay(500)
                    counter.intValue--
                }
                action.invoke()
            }
        }
        val getCounterText: (counter: MutableIntState) -> String = { counter ->
            counter.intValue.takeIf { it != 0 }?.toString() ?: ""
        }

        Announcement(
            liveRegionRequest = liveRegionRequestAnnouncement,
            contentDescription = "This is an announcement that can be invoked anytime"
        )

        CustomBox(color = Color.White) { Text(text = "White") }
        CustomBox(color = Color.Yellow) {
            Text(modifier = Modifier.liveRegionRequest(liveRegionRequest2), text = "Yellow")
        }
        CustomBox(color = Color.Green) {
            Text(modifier = Modifier.liveRegionRequest(liveRegionRequest3), text = "Green")
        }
        CustomBox(color = Color.Magenta) { Text(text = "Magenta") }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                waitForAction(counterAssertive) {
                    liveRegionRequest2.requestLiveRegionMode(LiveRegionMode.Assertive)
                }
            }
        ) {
            Text(text = "Read the Second field assertively ${getCounterText(counterAssertive)}")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                waitForAction(counterPolite) {
                    liveRegionRequest3.requestLiveRegionMode(LiveRegionMode.Polite)
                }
            }
        ) {
            Text(text = "Read the Third field politely ${getCounterText(counterPolite)}")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                liveRegionRequestAnnouncement.requestLiveRegionMode(LiveRegionMode.Assertive)
            }
        ) {
            Text(text = "Read announcement")
        }
    }
}

@Preview(widthDp = 200, heightDp = 300)
@Composable
fun NormalTraversalOrderPreview() {
    AccessibilityPlaygroundTheme {
        NormalTraversalOrder()
    }
}

@Preview(widthDp = 200, heightDp = 300)
@Composable
fun ModifiedTraversalOrder1Preview() {
    AccessibilityPlaygroundTheme {
        ModifiedTraversalOrder1()
    }
}

@Preview(widthDp = 200, heightDp = 300)
@Composable
fun ModifiedTraversalOrder2Preview() {
    AccessibilityPlaygroundTheme {
        ModifiedTraversalOrder2()
    }
}

@Preview(widthDp = 200, heightDp = 300)
@Composable
fun DeepTraversalOrder1Preview() {
    AccessibilityPlaygroundTheme {
        DeepTraversalOrder1()
    }
}

@Preview(widthDp = 200, heightDp = 300)
@Composable
fun DeepTraversalOrder2Preview() {
    AccessibilityPlaygroundTheme {
        DeepTraversalOrder2()
    }
}

@Preview(widthDp = 200, heightDp = 300)
@Composable
fun MultiLevelDeepTraversalOrderPreview() {
    AccessibilityPlaygroundTheme {
        MultiLevelDeepTraversalOrder()
    }
}

@Preview(widthDp = 200, heightDp = 300)
@Composable
fun InterruptingTraversalOrderPreview() {
    AccessibilityPlaygroundTheme {
        InterruptingTraversalOrder()
    }
}


@Preview(widthDp = 200, heightDp = 300)
@Composable
fun InterruptingLiveRegionPreview() {
    AccessibilityPlaygroundTheme {
        InterruptingLiveRegion()
    }
}
