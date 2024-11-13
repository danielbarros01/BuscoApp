package com.practica.buscov2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.buscov2.navigation.TabItem
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal
import kotlinx.coroutines.launch

@Composable
fun TabsComponent(
    tabs: List<TabItem>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    onClick: (Int) -> Unit = {}
) {
    val selectedTab = pagerState.currentPage
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .padding(bottom = 8.dp)
            .shadow(10.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            indicator = {tabPositions ->
                TabRowDefaults.PrimaryIndicator(
                    Modifier.fillMaxWidth().tabIndicatorOffset(tabPositions[selectedTab]),
                    color = OrangePrincipal,
                    width = tabPositions[selectedTab].width,
                    height = 2.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, item ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                        onClick(index)
                    },
                    text = {
                        Text(
                            text = item.title,
                            fontSize = fontSize,
                            color = if (selectedTab == index) OrangePrincipal else GrayText
                        )
                    }
                )
            }
        }
    }
}