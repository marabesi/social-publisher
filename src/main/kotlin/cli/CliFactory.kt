package cli

import persistence.FileSystemConfigurationRepository
import persistence.FileSystemPostRepository
import persistence.FileSystemSchedulerRepository
import picocli.CommandLine
import socialPosts.SocialConfiguration

class CliFactory: CommandLine.IFactory {
    override fun <K : Any?> create(cls: Class<K>?): K {
        if (cls != null) {
            val configuration = FileSystemConfigurationRepository()
            val currentConfiguration: SocialConfiguration = try {
                configuration.find()
            } catch (_:MissingConfiguration) {
                SocialConfiguration("production", "csv")
            }

            val postsRepository = FileSystemPostRepository("data/posts-${currentConfiguration.fileName}.csv")

            if (cls == Post::class.java) {
                return Post(postsRepository, CliOutput()) as K
            }
            if (cls == Scheduler::class.java) {
                val filePath = "data/scheduler-${currentConfiguration.fileName}.csv"
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