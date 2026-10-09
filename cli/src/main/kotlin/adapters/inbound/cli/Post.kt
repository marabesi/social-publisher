package adapters.inbound.cli

import application.Output
import application.entities.SocialMedia
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.post.Create
import application.post.List
import application.post.PostSearch
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "post", mixinStandardHelpOptions = true)
class Post
    @Inject
    constructor(
        private val postsRepository: PostsRepository,
        private val schedulerRepository: SchedulerRepository,
        private val cliOutput: Output,
    ) : Callable<String> {
        @CommandLine.Option(names = ["-c"], description = ["Creates a post"])
        var text: String = ""

        @CommandLine.Option(names = ["-l"], description = ["List created posts"])
        var list: Boolean? = false

        @CommandLine.Option(
            names = ["--search"],
            description = ["List posts matching the case-insensitive text search"],
        )
        var search: String = ""

        @CommandLine.Option(names = ["--ids"], description = ["List posts whose ids match the comma separated list"])
        var ids: String = ""

        @CommandLine.Option(
            names = ["--social-media"],
            description = ["List posts scheduled for the given social media"],
        )
        var socialMedia: SocialMedia? = null

        override fun call(): String {
            if (list == true) {
                val criteria = PostSearch.from(text = search, ids = ids, socialMedia = socialMedia)
                return List(postsRepository, schedulerRepository, cliOutput, criteria).invoke()
            }

            return Create(postsRepository, cliOutput).invoke(text)
        }
    }
