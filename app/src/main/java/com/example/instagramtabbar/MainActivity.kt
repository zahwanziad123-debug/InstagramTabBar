package com.example.instagramtabbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collect

private data class TabItem(val label: String, val icon: ImageVector)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { InstagramTabBarApp() }
    }
}

@Composable
private fun InstagramTabBarApp() {
    val tabs = remember {
        listOf(
            TabItem("Home", Icons.Outlined.Home),
            TabItem("Reels", Icons.Outlined.PlayCircleOutline),
            TabItem("Messages", Icons.Outlined.Send),
            TabItem("Search", Icons.Outlined.Search),
            TabItem("Profile", Icons.Outlined.PersonOutline)
        )
    }
    var selected by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    var minimized by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        var previous = 0
        snapshotFlow { listState.firstVisibleItemIndex * 10000 + listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { current ->
                val delta = current - previous
                if (kotlin.math.abs(delta) > 3) minimized = delta > 0
                previous = current
            }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Box(Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 18.dp, bottom = 112.dp)
                ) {
                    item {
                        Text(
                            text = tabs[selected].label,
                            modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF171717)
                        )
                    }
                    items((1..24).toList()) { index ->
                        FeedPlaceholder(index)
                    }
                }

                InstagramTabBar(
                    tabs = tabs,
                    selected = selected,
                    minimized = minimized,
                    onSelect = { selected = it; minimized = false },
                    modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun FeedPlaceholder(index: Int) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(34.dp).clip(CircleShape).background(Color(0xFFEAEAEA)))
            Spacer(Modifier.width(9.dp))
            Column {
                Box(Modifier.width(104.dp).height(9.dp).clip(CircleShape).background(Color(0xFFE8E8E8)))
                Spacer(Modifier.height(5.dp))
                Box(Modifier.width(68.dp).height(7.dp).clip(CircleShape).background(Color(0xFFF0F0F0)))
            }
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(230.dp).clip(RoundedCornerShape(5.dp)).background(if (index % 2 == 0) Color(0xFFF1F1F1) else Color(0xFFEDEDED)))
        Spacer(Modifier.height(9.dp))
        Box(Modifier.width(190.dp).height(8.dp).clip(CircleShape).background(Color(0xFFEAEAEA)))
    }
}

@Composable
private fun InstagramTabBar(
    tabs: List<TabItem>,
    selected: Int,
    minimized: Boolean,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val width by animateFloatAsState(
        targetValue = if (minimized) 0.20f else 1f,
        animationSpec = tween(320, easing = FastOutSlowInEasing), label = "barWidth"
    )
    Surface(
        modifier = modifier
            .fillMaxWidth(width)
            .height(52.dp)
            .animateContentSize(tween(320, easing = FastOutSlowInEasing)),
        shape = RoundedCornerShape(30.dp),
        color = Color(0xFFFAFAFA),
        shadowElevation = 5.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = if (minimized) 4.dp else 7.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (minimized) {
                TabButton(tabs[selected], true, Modifier.fillMaxWidth(), onClick = { onSelect(selected) })
            } else {
                tabs.forEachIndexed { index, tab ->
                    TabButton(tab, selected == index, Modifier.weight(1f), onClick = { onSelect(index) })
                }
            }
        }
    }
}

@Composable
private fun TabButton(tab: TabItem, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier = modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (selected) Color(0xFFE8E8E8) else Color.Transparent)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = Color(0xFF151515),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}