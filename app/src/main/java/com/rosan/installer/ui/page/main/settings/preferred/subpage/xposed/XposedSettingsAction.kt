// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2025-2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.subpage.xposed

import com.rosan.installer.domain.settings.model.HttpProfile
import com.rosan.installer.domain.settings.model.RootImplementation
//import com.highcapable.yukihookapi.hook.factory.prefs

sealed class XposedSettingsAction {
   data class FLockInstallerRequester(val enable: Boolean) : XposedSettingsAction()
   data class LockUninstallerRequester(val enable: Boolean) : XposedSettingsAction()
   data class InterceptSessionRequester(val enable: Boolean) : XposedSettingsAction()
   data class FixPermissionsRequester(val enable: Boolean) : XposedSettingsAction()
   data class XposedDebugLogRequester(val enable: Boolean) : XposedSettingsAction()
   data class Xposed_Disabled(val enable: Boolean) : XposedSettingsAction()
}
