package adapters.outbound.notion

class CouldNotUploadToNotionException(
    message: String,
) : RuntimeException(message)
