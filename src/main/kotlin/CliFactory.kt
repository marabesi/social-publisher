import cli.Post
import cli.Scheduler
import persistence.FileSystemRepository
import persistence.FileSystemSchedulerRepository
import picocli.CommandLine

class CliFactory: CommandLine.IFactory {
    override fun <K : Any?> create(cls: Class<K>?): K {
        if (cls != null) {
            val postsRepository = FileSystemRepository("data/social-production.csv")

            if (cls == Post::class.java) {
                return Post(postsRepository) as K
            }
            if (cls == Scheduler::class.java) {
                val filePath = "data/scheduler-social-production.csv"
                val scheduler = FileSystemSchedulerRepository(filePath)
                return Scheduler(postsRepository, scheduler) as K
            }
        }
        return CommandLine.defaultFactory().create(cls)
    }
}