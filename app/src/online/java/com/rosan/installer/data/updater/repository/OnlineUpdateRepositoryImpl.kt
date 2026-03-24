// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2025-2026 InstallerX Revived contributors
package com.rosan.installer.data.updater.repository

import android.content.Context
import com.rosan.installer.core.env.AppConfig
import com.rosan.installer.core.env.AppConfig.OFFICIAL_PACKAGE_NAME
import com.rosan.installer.data.updater.model.GithubRelease
import com.rosan.installer.domain.device.model.Level
import com.rosan.installer.domain.updater.model.UpdateInfo
import com.rosan.installer.domain.updater.repository.UpdateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.InputStream

class OnlineUpdateRepositoryImpl(
    private val context: Context,
    private val client: OkHttpClient,
    private val json: Json
) : UpdateRepository {
    override suspend fun checkUpdate(): UpdateInfo? {
        Timber.d("Update check disabled: Offline build")
        return null
    }

    override suspend fun downloadUpdate(url: String): Pair<InputStream, Long>? {
        Timber.w("Download is not supported in Offline build")
        return null
    }
}