package adapters.outbound.googledrive

import java.io.File

interface GoogleDriveFileUploader {
    fun upload(file: File): String
}
