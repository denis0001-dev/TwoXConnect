package ru.twoxconnect.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.denis0001dev.components.Category
import ru.denis0001dev.components.ListItem
import ru.denis0001dev.components.SwitchListItem
import ru.denis0001dev.utils.invoke
import ru.denis0001dev.utils.materialYouAvailable
import ru.twoxconnect.Res
import ru.twoxconnect.about
import ru.twoxconnect.as_system
import ru.twoxconnect.dark
import ru.twoxconnect.light
import ru.twoxconnect.materialYou
import ru.twoxconnect.materialYou_d
import ru.twoxconnect.settings
import ru.twoxconnect.theme
import ru.twoxconnect.ui.LocalNavController
import ru.twoxconnect.ui.Theme
import ru.twoxconnect.ui.dynamicThemeEnabled
import ru.twoxconnect.ui.theme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsTab() {
    TabBase {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        val navController = LocalNavController()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    title = {
                        Text(
                            stringResource(Res.string.settings),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("about") }) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = stringResource(Res.string.about)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
        ) { innerPadding ->
            Column(
                Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                var materialYouSwitch by remember {
                    mutableStateOf(
                        /*materialYouEnabled*/ materialYouAvailable // TODO replace with persistent setting
                    )
                }

                Category(Modifier.padding(top = 16.dp)) {
                    // Material You
                    SwitchListItem(
                        headline = stringResource(Res.string.materialYou),
                        supportingText = stringResource(Res.string.materialYou_d),
                        enabled = materialYouAvailable,
                        checked = materialYouSwitch,
                        onCheckedChange = {
                            materialYouSwitch = it
                            /*materialYouEnabled = it*/ // TODO replace with persistent setting
                            dynamicThemeEnabled = it
                        },
                        divider = true,
                        dividerColor = MaterialTheme.colorScheme.surface,
                        dividerThickness = 2.dp,
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Wallpaper,
                                contentDescription = null
                            )
                        }
                    )


                    // Theme
                    ListItem(
                        headline = stringResource(Res.string.theme),
                        divider = true,
                        dividerColor = MaterialTheme.colorScheme.surface,
                        dividerThickness = 2.dp,
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Brush,
                                contentDescription = null
                            )
                        },
                        bottomContent = {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                var selectedIndex by remember { mutableIntStateOf(/*appTheme.ordinal*/ Theme.AsSystem.ordinal) } // TODO repalce with persistent setting
                                val options = listOf(
                                    stringResource(Res.string.as_system),
                                    stringResource(Res.string.light),
                                    stringResource(Res.string.dark)
                                )
                                options.forEachIndexed { index, label ->
                                    FilterChip(
                                        onClick = {
                                            selectedIndex = index
                                            /*appTheme = Theme.entries[index]*/ // TODO replace with persistent setting
                                            theme = Theme.entries[index]
                                        },
                                        selected = index == selectedIndex,
                                        leadingIcon = {
                                            if (index == 0) {
                                                Spacer(Modifier.width(16.dp))
                                            }
                                            when (index) {
                                                0 -> Icon(Icons.Filled.Settings, null)
                                                1 -> Icon(Icons.Filled.LightMode, null)
                                                2 -> Icon(Icons.Filled.DarkMode, null)
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = label,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}