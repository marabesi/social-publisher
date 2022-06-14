package cli

import persistence.FileSystemConfigurationRepository
import persistence.FileSystemPostRepository
import persistence.FileSystemSchedulerRepository
import picocli.CommandLine

class CliFactory: CommandLine.IFactory {
    override fun <K : Any?> create(cls: Class<K>?): K {
        if (cls != null) {
            val postsRepository = FileSystemPostRepository("data/social-production.csv")

            if (cls == Post::class.java) {
                return Post(postsRepository, CliOutput()) as K
            }
            if (cls == Scheduler::class.java) {
                val filePath = "data/scheduler-social-production.csv"
                val scheduler = FileSystemSchedulerRepository(filePath)
                return Scheduler(postsRepository, scheduler, CliOutput()) as K
            }
            if (cls == Poster::class.java) {
                return Poster(postsRepository, CliOutput()) as K
            }

            if (cls == Configuration::class.java) {
                return Configuration(CliOutput(), FileSystemConfigurationRepository()) as K
            }
        }
        return CommandLine.defaultFactory().create(cls)
    }
}