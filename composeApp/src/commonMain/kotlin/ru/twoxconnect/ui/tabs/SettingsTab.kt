package ru.twoxconnect.ui.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import org.jetbrains.compose.resources.stringResource
import ru.denis0001dev.utils.invoke
import ru.twoxconnect.Res
import ru.twoxconnect.about
import ru.twoxconnect.settings
import ru.twoxconnect.ui.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
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
                Text("""
                    wejtwgwag
                    wagesrlkgjeasrkghaegha\
                    haerHaer
                    Haerh
                    aer
                    haer
                    haeh
                    er
                    hae
                    haserh
                    sethn
                    agawryhagawe
                    gaweg
                    aer
                    g
                    aerg
                    aer
                    ghaer
                    haer
                    hae
                    haeh
                    aerh
                    aserhER
                    Zhaer
                    h
                    ae
                    herth
                    serhserth
                    srthsrt
                    hser
                    hwsrt
                    htsr
                    yh
                    sdr
                    h
                    srt
                    hstrh
                    strhtrhsrhgsrthsrt
                    hr
                    gsrth
                    th
                    srh
                    srhsrthtsrhs
                    rh
                """.trimIndent())
            }
        }
    }
}