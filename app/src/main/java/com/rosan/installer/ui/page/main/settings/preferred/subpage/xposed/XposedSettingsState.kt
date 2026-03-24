// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2025-2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.subpage.xposed

import com.rosan.installer.domain.settings.model.HttpProfile
import com.rosan.installer.domain.settings.model.RootImplementation

data class XposedSettingsState(
    val isLoading: Boolean = true,
    val useBlur: Boolean = true,
   // val labRootEnableModuleFlash: Boolean = false,
   // val labRootShowModuleArt: Boolean = true,
   // val labRootModuleAlwaysUseRoot: Boolean = false,
   // val labRootImplementation: RootImplementation = RootImplementation.Magisk,
   // val labUseMiIsland: Boolean = false,
    val lockUninstaller: Boolean = false,
    val forcelockInstaller: Boolean = false
    //val labHttpProfile: HttpProfile = HttpProfile.ALLOW_SECURE,
   // val labHttpSaveFile: Boolean = false
)
