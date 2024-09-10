package com.rubens.salonadminpro.data.storage.repositories

import android.net.Uri

interface StorageRepository {
    fun sendPhotoToStorage(imgUri: Uri, fileExtension: String?, enviouFotoParaOStorage: (url: String?)->Unit)

}