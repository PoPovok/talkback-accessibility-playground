package com.example.accessibilityplayground.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Page {
    NORMAL,
    MODIFIED1,
    MODIFIED2,
    DEEP1,
    DEEP2,
    MULTI_LEVEL,
    INTERRUPTING_FOCUS,
    INTERRUPTING_READ
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
                Page.INTERRUPTING_FOCUS -> InterruptingTraversalOrder()
                Page.INTERRUPTING_READ -> InterruptingLiveRegion()
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
                    onClick = {
                        pagerState.currentPage.let {
                            if (it != 0) {
                                scrollToPage(it - 1)
                            } else {
                                scrollToPage(pagerState.pageCount - 1)
                            }
                        }
                    }
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
                    onClick = {
                        pagerState.currentPage.let {
                            if (it != pagerState.pageCount - 1) {
                                scrollToPage(it + 1)
                            } else {
                                scrollToPage(0)
                            }
                        }
                    }
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
