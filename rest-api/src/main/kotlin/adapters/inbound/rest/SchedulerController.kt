package adapters.inbound.rest

import adapters.inbound.rest.dto.MessageResponse
import adapters.inbound.rest.dto.SchedulePostRequest
import adapters.inbound.rest.dto.ScheduledItemResponse
import application.Output
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.scheduler.Create
import application.scheduler.filters.Criterion
import application.scheduler.filters.DateTimeValidation
import application.scheduler.filters.FilterExpression
import application.scheduler.filters.StartDate
import application.scheduler.filters.UntilDate
import application.scheduler.order.OrderExpression
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/schedules")
class SchedulerController(
    private val postsRepository: PostsRepository,
    private val schedulerRepository: SchedulerRepository,
    private val configurationRepository: ConfigurationRepository,
    private val output: Output,
) {
    @PostMapping
    fun create(
        @RequestBody request: SchedulePostRequest,
    ): MessageResponse =
        MessageResponse(
            Create(postsRepository, schedulerRepository, configurationRepository, output)
                .invoke(request.postId, request.publishDate),
        )

    @GetMapping
    fun list(
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?,
        @RequestParam(required = false) filter: String?,
        @RequestParam(required = false) orderBy: String?,
    ): List<ScheduledItemResponse> {
        val filters = buildFilters(startDate, endDate, filter)
        val order = orderBy?.let { OrderExpression.parse(it) ?: invalid("order-by") }
        val items = schedulerRepository.findAll(filters)
        val ordered = order?.apply(items) ?: items

        return ordered.map {
            ScheduledItemResponse(
                id = it.id,
                postId = it.post.id,
                text = it.post.text,
                publishDate = it.publishDate.toString(),
                published = it.published,
            )
        }
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: String,
    ): MessageResponse {
        val deleted =
            schedulerRepository.deleteById(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule $id not found")

        return MessageResponse("Schedule $id has been removed from post ${deleted.post.id}")
    }

    private fun buildFilters(
        startDate: String?,
        endDate: String?,
        filter: String?,
    ): ArrayList<Criterion> {
        val filters = arrayListOf<Criterion>()

        startDate?.let {
            val validation = DateTimeValidation(it)
            if (!validation.isDateTimeValid()) {
                invalid("start-date")
            }
            filters.add(StartDate(validation.value()))
        }

        endDate?.let {
            val validation = DateTimeValidation(it)
            if (!validation.isDateTimeValid()) {
                invalid("end-date")
            }
            filters.add(UntilDate(validation.value()))
        }

        filter?.let {
            filters.addAll(FilterExpression.parse(it) ?: invalid("filter"))
        }

        return filters
    }

    private fun invalid(name: String): Nothing = throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid $name")
}
