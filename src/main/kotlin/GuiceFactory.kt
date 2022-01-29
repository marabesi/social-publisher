import com.google.inject.AbstractModule
import com.google.inject.ConfigurationException
import com.google.inject.Guice
import picocli.CommandLine

class GuiceFactory: CommandLine.IFactory {
    private val injector = Guice.createInjector(DemoModule())

    @Throws(Exception::class)
    override fun <K> create(aClass: Class<K>): K {
        return try {
            injector.getInstance(aClass)
        } catch (ex: ConfigurationException) { // no implementation found in Guice configuration
            CommandLine.defaultFactory().create(aClass) // fallback if missing
        }
    }

    class DemoModule : AbstractModule() {
        override fun configure() {
            bind(PostsRepository::class.java).toInstance(PostsRepository())
        }
    }
}