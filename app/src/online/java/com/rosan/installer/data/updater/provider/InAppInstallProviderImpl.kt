// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2025-2026 InstallerX Revived contributors
package com.rosan.installer.data.updater.provider

import android.content.Context
import android.os.Process
import timber.log.Timber
import com.rosan.installer.domain.engine.model.DataEntity
import com.rosan.installer.domain.engine.model.DataType
import com.rosan.installer.domain.engine.model.InstallEntity
import com.rosan.installer.domain.engine.model.InstallExtraInfoEntity
import com.rosan.installer.domain.engine.repository.InstallerRepository
import com.rosan.installer.domain.settings.model.ConfigModel
import com.rosan.installer.domain.updater.provider.InAppInstallProvider
import java.io.InputStream

class InAppInstallProviderImpl(
    private val context: Context,
    private val installerRepository: InstallerRepository
) : InAppInstallProvider {
    override suspend fun executeInstall(
        fileName: String,
        inputStream: InputStream,
        contentLength: Long,
        config: ConfigModel
    ) {
        Timber.d("Offline build: executeInstall called but safely ignored.")
    }
}