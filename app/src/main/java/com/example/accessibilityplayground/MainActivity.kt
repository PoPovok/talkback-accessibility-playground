package com.example.accessibilityplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.accessibilityplayground.ui.theme.AccessibilityPlaygroundTheme
import com.example.accessibilityplayground.util.AccessibilityUtils
import com.example.accessibilityplayground.util.AccessibilityUtils.traversalFocusRequest
import com.example.accessibilityplayground.util.AccessibilityUtils.traversalOrder
import com.example.accessibilityplayground.util.TraversalFocusRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Page {
    NORMAL,
    MODIFIED1,
    MODIFIED2,
    DEEP1,
    DEEP2,
    MULTI_LEVEL,
    INTERRUPTING
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccessibilityPlaygroundTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CustomPager()
                }
            }
        }
    }
}

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomPager() {
    val pagerState = rememberPagerState(pageCount = { Page.entries.size })
    val coroutineScope = rememberCoroutineScope()

    // only needed to reset the position of the talkback service to default after navigation
    var areButtonsVisible by remember { mutableStateOf(true) }

    val scrollToPage: (Int) -> Unit = { index ->
        coroutineScope.launch {
            pagerState.scrollToPage(index)
            areButtonsVisible = false
            delay(1500)
            areButtonsVisible = true
        }
    }
    Column {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (Page.entries[page]) {
                Page.NORMAL -> NormalTraversalOrder()
                Page.MODIFIED1 -> ModifiedTraversalOrder1()
                Page.MODIFIED2 -> ModifiedTraversalOrder2()
                Page.DEEP1 -> DeepTraversalOrder1()
                Page.DEEP2 -> DeepTraversalOrder2()
                Page.MULTI_LEVEL -> MultiLevelDeepTraversalOrder()
                Page.INTERRUPTING -> InterruptingTraversalOrder()
            }
        }
        if (areButtonsVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier.clearAndSetSemantics {
                        contentDescription = "Previous page"
                        role = Role.Button
                    },
                    enabled = pagerState.currentPage != 0,
                    onClick = { scrollToPage(pagerState.currentPage - 1) }
                ) {
                    Text("<")
                }
                Text(
                    modifier = Modifier.semantics { this.liveRegion = LiveRegionMode.Polite },
                    text = Page.entries[pagerState.currentPage].toString().lowercase()
                )
                Button(
                    modifier = Modifier.clearAndSetSemantics {
                        contentDescription = "Next page"
                        role = Role.Button
                    },
                    enabled = pagerState.currentPage != Page.entries.size - 1,
                    onClick = { scrollToPage(pagerState.currentPage + 1) }
                ) {
                    Text(">")
                }
            }
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .heightIn(min = ButtonDefaults.MinHeight)
                    .padding(vertical = 12.dp),
                text = "Reseting talback position",
                textAlign = TextAlign.Center
            )
        }
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
        ) = AccessibilityUtils.createRefs()

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
        ) = AccessibilityUtils.createRefs()

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
        ) = AccessibilityUtils.createRefs()

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
        ) = AccessibilityUtils.createRefs()

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
        ) = AccessibilityUtils.createRefs()

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
            ) = AccessibilityUtils.createRefs()

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
        CustomBox(color = Color.Yellow) { Text(text = "Second") }
        CustomBox(color = Color.Green) { Text(text = "Third") }
        CustomBox(color = Color.Magenta) { Text(text = "Fourth") }
        CustomBox(color = Color.Cyan) {
            Text(modifier = Modifier.traversalFocusRequest(traversalFocusRequest), text = "Fifth")
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = { traversalFocusRequest.requestTraversalFocus() }) {
            Text(text = "Jump to Fifth")
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