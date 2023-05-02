package me.kariot.invoicegenerator

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.requestMultiplePermissions(permissionListener: (Boolean) -> Unit): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
        val isGranted = permission.entries.all {
            it.value
        }
        return@registerForActivityResult permissionListener.invoke(isGranted)
    }
}