interface ConfigurationRepository {
    fun save(configuration: Any): Boolean
}