// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2025-2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.subpage.xposed

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rosan.installer.R
import com.rosan.installer.core.env.AppConfig
import com.rosan.installer.domain.device.provider.DeviceCapabilityProvider
import com.rosan.installer.ui.icons.AppIcons
import com.rosan.installer.ui.page.main.widget.card.InfoTipCard
import com.rosan.installer.ui.page.main.widget.dialog.RootImplementationSelectionDialog
import com.rosan.installer.ui.page.main.widget.setting.AppBackButton
//import com.rosan.installer.ui.page.main.widget.setting.LabHttpProfileWidget
//import com.rosan.installer.ui.page.main.widget.setting.LabRootImplementationWidget
import com.rosan.installer.ui.page.main.widget.setting.SplicedColumnGroup
import com.rosan.installer.ui.page.main.widget.setting.SwitchWidget
import com.rosan.installer.ui.theme.getM3TopBarColor
import com.rosan.installer.ui.theme.installerHazeEffect
import com.rosan.installer.ui.theme.none
import com.rosan.installer.ui.theme.rememberMaterial3HazeStyle
//import com.rosan.installer.ui.page.main.widget.setting.UninstallerLocker
import com.rosan.installer.util.toast
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.rosan.installer.ui.page.main.widget.card.ModuleStatusCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewXposedPage(
    navController: NavHostController,
    viewModel: XposedSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val capabilityProvider = koinInject<DeviceCapabilityProvider>()
    val context = LocalContext.current
    val topAppBarState = rememberTopAppBarState()
    val hazeState = if (uiState.useBlur) remember { HazeState() } else null
    val hazeStyle = rememberMaterial3HazeStyle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
    val isActive = runCatching { YukiHookAPI.Status.isXposedModuleActive }.getOrDefault(false)

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is XposedSettingsEvent.ShowMessage -> context.toast(event.resId)
            }
        }
    }
    /*val showRootImplementationDialog = remember { mutableStateOf(false) }
    val isMiIslandSupported = remember { capabilityProvider.isSupportMiIsland }
    if (showRootImplementationDialog.value) {
        RootImplementationSelectionDialog(
            currentSelection = uiState.labRootImplementation,
            onDismiss = { showRootImplementationDialog.value = false },
            onConfirm = { selectedImplementation ->
                showRootImplementationDialog.value = false
                // 1. Save the selected implementation
                viewModel.dispatch(XposedSettingsAction.LabChangeRootImplementation(selectedImplementation))
                // 2. Enable the flash module feature
                viewModel.dispatch(XposedSettingsAction.LabChangeRootModuleFlash(true))
            }
        )
    }*/

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.none,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.installerHazeEffect(hazeState, hazeStyle),
                windowInsets = TopAppBarDefaults.windowInsets.add(WindowInsets(left = 12.dp)),
                title = {
                    Text(stringResource(R.string.xposed_1))
                },
                navigationIcon = {
                    Row {
                        AppBackButton(
                            onClick = { navController.navigateUp() },
                            icon = Icons.AutoMirrored.TwoTone.ArrowBack,
                            modifier = Modifier.size(36.dp),
                            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = hazeState.getM3TopBarColor(),
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    scrolledContainerColor = hazeState.getM3TopBarColor()
                )
            )
        }
    ) { paddingValues ->
        Crossfade(
            targetState = uiState.isLoading,
            label = "XposedPageContent",
            animationSpec = tween(durationMillis = 150)
        ) { isLoading ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(hazeState?.let { Modifier.hazeSource(it) } ?: Modifier),
                    contentPadding = PaddingValues(
                        top = paddingValues.calculateTopPadding() + 12.dp
                    )
                ) {
                    item { ModuleStatusCard(isActive) }
                    item { InfoTipCard(text = stringResource(R.string.lab_tip)) }
                    item {
                        SplicedColumnGroup(
                            title = stringResource(R.string.global)
                        ) {
                            item {
                                SwitchWidget(
                                    icon = AppIcons.AutoLockDefault,
                                    title = stringResource(R.string.Finstaller_locker),
                                    description = stringResource(R.string.Finstaller_locker_desc),
                                    checked = uiState.forcelockInstaller && isActive,
                                    onCheckedChange = { viewModel.dispatch(if (isActive) XposedSettingsAction.FLockInstallerRequester(it) else XposedSettingsAction.Xposed_Disabled(it)) }
                                )
                            }
                            item (visible = uiState.forcelockInstaller && isActive){
                                SwitchWidget(
                                    icon = AppIcons.Intercept,
                                    title = stringResource(R.string.intercept_session_install_title),
                                    description = stringResource(R.string.intercept_session_install_desc),
                                    checked = uiState.interceptsessionInstall,
                                    onCheckedChange = { viewModel.dispatch(XposedSettingsAction.InterceptSessionRequester(it)) }
                                )
                            }
                            item (visible = uiState.interceptsessionInstall && isActive){
                                SwitchWidget(
                                    icon = AppIcons.Lock,
                                    title = stringResource(R.string.fix_permissions_title),
                                    description = stringResource(R.string.fix_permissions_desc),
                                    checked = uiState.fixPermissions,
                                    onCheckedChange = { viewModel.dispatch(XposedSettingsAction.FixPermissionsRequester(it)) }
                                )
                            }
                            item {
                                SwitchWidget(
                                    icon = AppIcons.Delete,
                                    title = stringResource(R.string.uninstaller_locker),
                                    description = stringResource(R.string.uninstaller_locker_desc),
                                    checked = uiState.lockUninstaller && isActive,
                                    onCheckedChange = { viewModel.dispatch(if (isActive) XposedSettingsAction.LockUninstallerRequester(it) else XposedSettingsAction.Xposed_Disabled(it)) }
                                )
                            }
                        }
                    }
                    item {
                        SplicedColumnGroup(
                            title = stringResource(R.string.debug)
                        ) {
                            item {
                                SwitchWidget(
                                    icon = AppIcons.BugReport,
                                    title = stringResource(R.string.debug_log_title),
                                    description = stringResource(R.string.debug_log_desc),
                                    checked = uiState.xposedDebuglog && isActive,
                                    onCheckedChange = { viewModel.dispatch(if (isActive) XposedSettingsAction.XposedDebugLogRequester(it) else XposedSettingsAction.Xposed_Disabled(it)) }
                                )
                            }
                        }
                    }
                    item { Spacer(Modifier.navigationBarsPadding()) }
                }
            }
        }
    }
}