package com.suminchoi.coachapp.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.Spacing
import com.suminchoi.coachapp.design.rcColors

@Composable
fun RcScreen(
    title: String,
    eyebrow: String? = null,
    actions: @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit,
) {
    val listState = rememberLazyListState()
    val showNavBar by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 80 }
    }

    Box(modifier = Modifier.fillMaxSize().background(rcColors.bg)) {
        AmbientBackground()

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = Spacing.bottomContentPad),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                if (eyebrow != null) {
                    Text(
                        text = eyebrow.uppercase(),
                        style = RcTypography.labelSmall,
                        color = rcColors.textMuted,
                        modifier = Modifier.padding(
                            start = Spacing.screenHorizontal,
                            end = Spacing.screenHorizontal,
                            top = 56.dp,
                            bottom = 4.dp,
                        ),
                    )
                }
                Text(
                    text = title,
                    style = RcTypography.displayLarge,
                    color = rcColors.text,
                    modifier = Modifier.padding(
                        start = Spacing.screenHorizontal,
                        end = Spacing.screenHorizontal,
                        top = if (eyebrow == null) 56.dp else 0.dp,
                        bottom = 20.dp,
                    ),
                )
            }
            content()
        }

        RcNavBar(
            title = title,
            visible = showNavBar,
            actions = actions,
        )
    }
}
