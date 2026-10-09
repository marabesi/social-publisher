package adapters.inbound.rest

import adapters.inbound.rest.dto.CreatePostRequest
import adapters.inbound.rest.dto.MessageResponse
import adapters.inbound.rest.dto.PostResponse
import application.Output
import application.persistence.PostsRepository
import application.post.Create
import application.post.PostLimits
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postsRepository: PostsRepository,
    private val output: Output,
) {
    @PostMapping
    fun create(
        @RequestBody request: CreatePostRequest,
    ): MessageResponse = MessageResponse(Create(postsRepository, output).invoke(request.text))

    @GetMapping
    fun list(): List<PostResponse> =
        postsRepository.findAll().map {
            PostResponse(
                id = it.id ?: "",
                text = it.text,
                characterCount = it.text.length,
                characterLimit = PostLimits.MAX_CHARACTERS,
            )
        }
}
