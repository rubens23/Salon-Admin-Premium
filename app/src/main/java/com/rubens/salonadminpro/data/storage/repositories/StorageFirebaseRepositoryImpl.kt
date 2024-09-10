package com.rubens.salonadminpro.data.storage.repositories

import android.net.Uri
import com.google.firebase.storage.StorageReference

class StorageFirebaseRepositoryImpl(
    private val storageReference: StorageReference
): StorageRepository {
    override fun sendPhotoToStorage(imgUri: Uri,
                                    fileExtension: String?,
                                    enviouFotoParaOStorage: (url: String?)->Unit) {
        val fileReference = storageReference.child(
            "uploads/" + System.currentTimeMillis().toString()
                    + "." + fileExtension
        )

        fileReference.putFile(imgUri).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                    downloadUrl->
                enviouFotoParaOStorage(downloadUrl.toString())

            }.addOnFailureListener {
                enviouFotoParaOStorage("")

            }
        }.addOnFailureListener {
            enviouFotoParaOStorage("")

        }
    }
}