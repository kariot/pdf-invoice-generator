package me.kariot.invoicegenerator

import android.Manifest

object Constants {
    val storagePermission = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}