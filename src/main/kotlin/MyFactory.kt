import persistence.FileSystemRepository
import picocli.CommandLine

class MyFactory: CommandLine.IFactory {
    override fun <K : Any?> create(cls: Class<K>?): K {
        if (cls != null) {
            if (cls == Post::class.java) {
                return Post(FileSystemRepository("data/social-production.csv")) as K
            }
            if (cls == Scheduler::class.java) {
                return Scheduler(FileSystemRepository("data/social-production.csv")) as K
            }
        }
        return CommandLine.defaultFactory().create(cls)
    }
}