// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2025-2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.subpage.xposed

import android.app.ActivityThread
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rosan.installer.domain.settings.repository.AppSettingsRepo
import com.rosan.installer.domain.settings.repository.BooleanSetting
import com.rosan.installer.domain.settings.repository.StringSetting
import com.rosan.installer.domain.settings.usecase.settings.UpdateSettingUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.rosan.installer.hook.util.PrefsProvider
import com.highcapable.yukihookapi.hook.factory.prefs

class XposedSettingsViewModel(
    appSettingsRepo: AppSettingsRepo,
    private val updateSetting: UpdateSettingUseCase
) : ViewModel() {
    val state: StateFlow<XposedSettingsState> = appSettingsRepo.preferencesFlow.map { prefs ->
        XposedSettingsState(
            isLoading = false,
            useBlur = prefs.useBlur,
            //labRootEnableModuleFlash = prefs.labRootEnableModuleFlash,
            //labRootShowModuleArt = prefs.labRootShowModuleArt,
            //labRootModuleAlwaysUseRoot = prefs.labRootModuleAlwaysUseRoot,
            //labRootImplementation = prefs.labRootImplementation,
            //labUseMiIsland = prefs.labUseMiIsland,
            lockUninstaller = prefs.lockUninstaller,
            forcelockInstaller = prefs.forcelockInstaller
            //labHttpProfile = prefs.labHttpProfile,
            //labHttpSaveFile = prefs.labHttpSaveFile
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = XposedSettingsState(isLoading = true)
    )

    private fun getApplicationContext(): Context {
        return try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentApplicationMethod = activityThreadClass.getMethod("currentApplication")
            currentApplicationMethod.invoke(null) as Context
        } catch (e: Exception) {
            throw IllegalStateException("Failed to get Application Context", e)
        }
    }

    fun dispatch(action: XposedSettingsAction) {
        when (action) {
            is XposedSettingsAction.FLockInstallerRequester -> viewModelScope.launch {
                try {
                    val context: Context = getApplicationContext()
                    context.prefs(PrefsProvider.PREFS_FILE_NAME).edit {
                        putBoolean("intercept_install", action.enable)
                    }
                } catch(_: Exception) {}
                updateSetting(
                    //BooleanSetting.LabSetInstallRequester,
                    BooleanSetting.ForceLockInstaller,
                    action.enable
                )
            }

            is XposedSettingsAction.LockUninstallerRequester -> viewModelScope.launch {
                try {
                    val context: Context = getApplicationContext()
                    context.prefs(PrefsProvider.PREFS_FILE_NAME).edit {
                        putBoolean("intercept_uninstall", action.enable)
                    }
                } catch(_: Exception) {}
                updateSetting(
                    BooleanSetting.AutoLockUninstaller,
                    action.enable
                )
            }

            // is XposedSettingsAction.LabChangeHttpProfile -> viewModelScope.launch {
            //     updateSetting(
            //         StringSetting.LabHttpProfile,
            //         action.profile.name
            //     )
            // }

            //is XposedSettingsAction.LabChangeHttpSaveFile -> viewModelScope.launch { updateSetting(BooleanSetting.LabHttpSaveFile, action.enable) }
        }
    }
}
