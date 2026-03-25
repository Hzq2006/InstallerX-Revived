// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2023-2026 iamr0s, InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.subpage.xposed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rosan.installer.R
import com.rosan.installer.core.env.AppConfig
import com.rosan.installer.domain.device.provider.DeviceCapabilityProvider
import com.rosan.installer.ui.icons.AppIcons
import com.rosan.installer.ui.page.main.widget.card.InfoTipCard
import com.rosan.installer.ui.page.main.widget.dialog.RootImplementationSelectionDialog
import com.rosan.installer.ui.page.main.widget.setting.AppBackButton
import com.rosan.installer.ui.page.main.widget.setting.LabHttpProfileWidget
import com.rosan.installer.ui.page.main.widget.setting.LabRootImplementationWidget
import com.rosan.installer.ui.page.main.widget.setting.LabelWidget
import com.rosan.installer.ui.page.main.widget.setting.SwitchWidget
//import com.rosan.installer.ui.page.main.widget.setting.UninstallerLocker
import com.rosan.installer.util.toast
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.rosan.installer.ui.page.main.widget.card.ModuleStatusCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegacyXposedPage(
    navController: NavHostController,
    viewModel: XposedSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val capabilityProvider = koinInject<DeviceCapabilityProvider>()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.xposed_1)) },
                navigationIcon = {
                    AppBackButton(onClick = { navController.navigateUp() })
                },
                scrollBehavior = scrollBehavior
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
                        .padding(paddingValues)
                ) {
                    item { ModuleStatusCard(isActive) }
                    item { InfoTipCard(text = stringResource(R.string.lab_tip)) }
                    item { LabelWidget(stringResource(R.string.global)) }
                    item {
                        SwitchWidget(
                            icon = AppIcons.AutoLockDefault,
                            title = stringResource(R.string.Finstaller_locker),
                            description = stringResource(R.string.Finstaller_locker_desc),
                            checked = uiState.forcelockInstaller && isActive,
                            isM3E = false,
                            onCheckedChange = {viewModel.dispatch(if(isActive)XposedSettingsAction.FLockInstallerRequester(it) else XposedSettingsAction.Xposed_Disabled(it)) }
                        )
                    }
                    item {
                        AnimatedVisibility(
                            visible = uiState.forcelockInstaller && isActive,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            SwitchWidget(
                                icon = AppIcons.Intercept,
                                title = stringResource(R.string.intercept_session_install_title),
                                description = stringResource(R.string.intercept_session_install_desc),
                                checked = uiState.interceptsessionInstall,
                                isM3E = false,
                                onCheckedChange = { viewModel.dispatch(XposedSettingsAction.InterceptSessionRequester(it)) }
                            )
                        }
                    }
                    item {
                        AnimatedVisibility(
                            visible = uiState.interceptsessionInstall && isActive,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            SwitchWidget(
                                icon = AppIcons.Lock,
                                title = stringResource(R.string.fix_permissions_title),
                                description = stringResource(R.string.fix_permissions_desc),
                                checked = uiState.fixPermissions,
                                isM3E = false,
                                onCheckedChange = { viewModel.dispatch(XposedSettingsAction.FixPermissionsRequester(it)) }
                            )
                        }
                    }
                    item {
                        SwitchWidget(
                            icon = AppIcons.Delete,
                            title = stringResource(R.string.uninstaller_locker),
                            description = stringResource(R.string.uninstaller_locker_desc),
                            checked = uiState.lockUninstaller && isActive,
                            isM3E = false,
                            onCheckedChange = { viewModel.dispatch(if (isActive) XposedSettingsAction.LockUninstallerRequester(it) else XposedSettingsAction.Xposed_Disabled(it)) }
                        )
                    }
                    // --- Unstable Features Section ---
                    item { LabelWidget(stringResource(R.string.debug)) }
                    item {
                        SwitchWidget(
                            icon = AppIcons.BugReport,
                            title = stringResource(R.string.debug_log_title),
                            description = stringResource(R.string.debug_log_desc),
                            checked = uiState.xposedDebuglog && isActive,
                            isM3E = false,
                            onCheckedChange = { viewModel.dispatch(if (isActive) XposedSettingsAction.XposedDebugLogRequester(it) else XposedSettingsAction.Xposed_Disabled(it)) }
                        )
                    }
                    item { Spacer(Modifier.navigationBarsPadding()) }
                }
            }
        }
    }
}