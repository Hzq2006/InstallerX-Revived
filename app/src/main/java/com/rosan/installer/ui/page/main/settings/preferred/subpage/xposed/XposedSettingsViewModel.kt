// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2025-2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.subpage.xposed

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rosan.installer.domain.settings.repository.AppSettingsRepo
import com.rosan.installer.domain.settings.repository.BooleanSetting
import com.rosan.installer.domain.settings.repository.StringSetting
import com.rosan.installer.domain.settings.usecase.settings.UpdateSettingUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.rosan.installer.hook.util.PrefsSaver
import com.rosan.installer.R
//import com.highcapable.yukihookapi.hook.factory.prefs

class XposedSettingsViewModel(
    appSettingsRepo: AppSettingsRepo,
    private val updateSetting: UpdateSettingUseCase
) : ViewModel() {

    private val _uiEvents = MutableSharedFlow<XposedSettingsEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val uiEvents = _uiEvents.asSharedFlow()
    val state: StateFlow<XposedSettingsState> = appSettingsRepo.preferencesFlow.map { prefs ->
        XposedSettingsState(
            isLoading = false,
            useBlur = prefs.useBlur,
            lockUninstaller = prefs.lockUninstaller,
            forcelockInstaller = prefs.forcelockInstaller,
            interceptsessionInstall = prefs.interceptSessionInstall,
            fixPermissions = prefs.fixPermissions,
            xposedDebuglog = prefs.xposedDebuglog
            //labHttpProfile = prefs.labHttpProfile,
            //labHttpSaveFile = prefs.labHttpSaveFile
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = XposedSettingsState(isLoading = true)
    )

    fun dispatch(action: XposedSettingsAction) {
        when (action) {
            is XposedSettingsAction.FLockInstallerRequester -> viewModelScope.launch {
                PrefsSaver.putBoolean("intercept_install", action.enable)
                updateSetting(
                    //BooleanSetting.LabSetInstallRequester,
                    BooleanSetting.ForceLockInstaller,
                    action.enable
                )
                if (action.enable == false) {
                    updateSetting(BooleanSetting.InterceptSessionInstall,false)
                    updateSetting(BooleanSetting.FixPermissions,false)
                    PrefsSaver.putBoolean("intercept_session_install", false)
                    PrefsSaver.putBoolean("fix_permissions", false)
                }
            }

            is XposedSettingsAction.LockUninstallerRequester -> viewModelScope.launch {
                PrefsSaver.putBoolean("intercept_uninstall", action.enable)
                updateSetting(
                    BooleanSetting.AutoLockUninstaller,
                    action.enable
                )
            }

            is XposedSettingsAction.InterceptSessionRequester -> viewModelScope.launch {
                PrefsSaver.putBoolean("intercept_session_install", action.enable)
                updateSetting(
                    BooleanSetting.InterceptSessionInstall,
                    action.enable
                )
                if (action.enable == false) {
                    updateSetting(BooleanSetting.FixPermissions,false)
                    PrefsSaver.putBoolean("fix_permissions", false)
                }
            }

            is XposedSettingsAction.FixPermissionsRequester -> viewModelScope.launch {
                PrefsSaver.putBoolean("fix_permissions", action.enable)
                updateSetting(
                    BooleanSetting.FixPermissions,
                    action.enable
                )
            }

            is XposedSettingsAction.XposedDebugLogRequester -> viewModelScope.launch {
                PrefsSaver.putBoolean("enable_debug_log", action.enable)
                updateSetting(
                    BooleanSetting.XposedDebugLog,
                    action.enable
                )
            }

            is XposedSettingsAction.Xposed_Disabled -> viewModelScope.launch {
                _uiEvents.tryEmit(XposedSettingsEvent.ShowMessage(R.string.module_status_inactive_desc))
            }

        }
    }
}
