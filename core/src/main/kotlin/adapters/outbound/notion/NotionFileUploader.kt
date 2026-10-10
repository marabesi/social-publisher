package adapters.outbound.notion

import java.io.File

interface NotionFileUploader {
    fun upload(file: File): String
}
