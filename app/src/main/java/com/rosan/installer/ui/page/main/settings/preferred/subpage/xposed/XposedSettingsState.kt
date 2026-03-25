// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2025-2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.subpage.xposed

import com.rosan.installer.domain.settings.model.HttpProfile
import com.rosan.installer.domain.settings.model.RootImplementation

data class XposedSettingsState(
    val isLoading: Boolean = true,
    val useBlur: Boolean = true,
    val lockUninstaller: Boolean = false,
    val forcelockInstaller: Boolean = false,
    val interceptsessionInstall: Boolean = false,
    val fixPermissions: Boolean = false,
    val xposedDebuglog: Boolean = false
)
