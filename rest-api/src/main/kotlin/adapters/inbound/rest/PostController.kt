package adapters.inbound.rest

import adapters.inbound.rest.dto.CreatePostRequest
import adapters.inbound.rest.dto.MessageResponse
import adapters.inbound.rest.dto.PostResponse
import application.Output
import application.entities.SocialMedia
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.post.Create
import application.post.PostLimits
import application.post.PostSearch
import application.post.Search
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postsRepository: PostsRepository,
    private val schedulerRepository: SchedulerRepository,
    private val output: Output,
) {
    @PostMapping
    fun create(
        @RequestBody request: CreatePostRequest,
    ): MessageResponse = MessageResponse(Create(postsRepository, output).invoke(request.text))

    @GetMapping
    fun list(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) ids: String?,
        @RequestParam(required = false) socialMedia: SocialMedia?,
    ): List<PostResponse> {
        val criteria = PostSearch.from(text = search.orEmpty(), ids = ids.orEmpty(), socialMedia = socialMedia)

        return Search(postsRepository, schedulerRepository).invoke(criteria).map {
            PostResponse(
                id = it.id ?: "",
                text = it.text,
                characterCount = it.text.length,
                characterLimit = PostLimits.MAX_CHARACTERS,
            )
        }
    }
}
