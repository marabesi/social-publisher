package adapters.cli.scheduler

import MockedOutput
import adapters.inbound.cli.scheduler.SchedulerList
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.Messages
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialMedia
import application.entities.SocialPosts
import buildCommandLine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant

class SchedulerListTest {
    private lateinit var app: SchedulerList
    private lateinit var cmd: CommandLine
    private val scheduleRepository = InMemorySchedulerRepository()
    private val postsRepository = InMemoryPostRepository()
    private val configurationRepository = ConfigurationInMemoryRepository()

    @BeforeEach
    fun setUp() {
        configurationRepository.save(SocialConfiguration(timezone = "UTC"))
        app = SchedulerList(scheduleRepository, configurationRepository, MockedOutput())
        cmd = CommandLine(app)
    }

    @Test
    fun `should show message if scheduler is empty`() {
        cmd.execute()
        assertEquals("No posts scheduled", cmd.getExecutionResult())
    }

    @Test
    fun `should show help message for scheduler list command`() {
        val cmd = buildCommandLine()

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)
        cmd.err = PrintWriter(sw)

        cmd.execute("scheduler", "list", "--help")
        assertEquals(
            listOf(
                "Usage: social scheduler list [-hV] [--end-date=<endDate>] [-f=<filter>]",
                "                             [--group-by=<groupBy>] [--ids=<ids>]",
                "                             [-o=<orderBy>] [--search=<search>]",
                "                             [--social-media=<socialMedia>]",
                "                             [--start-date=<startDate>]",
                "      --end-date=<endDate>   list posts until this date",
                "  -f, --filter=<filter>      Filters the scheduled posts by any property, e.g.",
                "                               post.text=draft",
                "      --group-by=<groupBy>   Outputs the scheduled posts grouped by a given",
                "                               criteria",
                "  -h, --help                 Show this help message and exit.",
                "      --ids=<ids>            Only schedules for the comma separated post ids",
                "  -o, --order-by=<orderBy>   Orders the scheduled posts by publish_date asc or",
                "                               desc",
                "      --search=<search>      Only schedules whose post matches the",
                "                               case-insensitive text search",
                "      --social-media=<socialMedia>",
                "                             Only schedules for the given social media",
                "      --start-date=<startDate>",
                "                             list posts that has they publish date starting",
                "                               with this value",
                "  -V, --version              Print version information and exit.",
                "",
            ).joinToString("\n"),
            sw.toString(),
        )
    }

    @Test
    fun `should list post with id 1 to be scheduled`() {
        val post = SocialPosts(text = "anything")
        postsRepository.save(
            arrayListOf(
                post,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post,
                Instant.parse("2022-10-02T09:00:00Z"),
            ),
        )

        cmd.execute()

        assertEquals(
            """
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should schedule same post twice each on different days`() {
        val post = SocialPosts(text = "first day")
        postsRepository.save(
            arrayListOf(
                post,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post,
                Instant.parse("2022-10-02T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post,
                Instant.parse("2022-10-03T09:00:00Z"),
            ),
        )

        cmd.execute()

        assertEquals(
            """
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z (Twitter)
            2. Post with id 1 will be published on 2022-10-03T09:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should list posts to be scheduled`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        postsRepository.save(
            arrayListOf(
                post1,
                post2,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2022-10-02T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post2,
                Instant.parse("2022-11-02T10:00:00Z"),
            ),
        )

        cmd.execute()

        assertEquals(
            """
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z (Twitter)
            2. Post with id 2 will be published on 2022-11-02T10:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should list posts in the future given a start date only`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        postsRepository.save(
            arrayListOf(
                post1,
                post2,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2021-10-02T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post2,
                Instant.parse("2023-11-02T10:00:00Z"),
            ),
        )

        cmd.execute("--start-date", "2023-11-01T10:00:00Z")

        assertEquals(
            """
            1. Post with id 2 will be published on 2023-11-02T10:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should list posts until a given date based on an end date`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        postsRepository.save(
            arrayListOf(
                post1,
                post2,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2024-01-02T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post2,
                Instant.parse("2024-11-02T10:00:00Z"),
            ),
        )

        cmd.execute("--end-date", "2024-02-02T09:00:00Z")

        assertEquals(
            """
            1. Post with id 1 will be published on 2024-01-02T09:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should handle attempt to offer Invalid end date`() {
        cmd.execute("--end-date", "123")

        assertEquals(Messages.INVALID_END_DATE, cmd.getExecutionResult())
    }

    @Test
    fun `should handle attempt to offer invalid date in --start-date`() {
        cmd.execute("--start-date", "123")

        assertEquals(Messages.INVALID_START_DATE, cmd.getExecutionResult())
    }

    @Test
    fun `should group list by posts with single post`() {
        val post1 = SocialPosts(text = "anything")
        postsRepository.save(
            arrayListOf(
                post1,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2021-10-02T09:00:00Z"),
            ),
        )

        cmd.execute("--group-by", "post")

        assertEquals(
            """
            1. Post with id 1 posted 1 time(s)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should group list by posts with multiple post`() {
        val post1 = SocialPosts(id = "1", text = "anything")
        val post2 = SocialPosts(id = "2", text = "anything-2")
        postsRepository.save(
            arrayListOf(
                post1,
                post2,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2021-10-02T09:00:00Z"),
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post2,
                Instant.parse("2022-11-02T09:00:00Z"),
            ),
        )

        cmd.execute("--group-by", "post")

        assertEquals(
            """
            1. Post with id 1 posted 1 time(s)
            2. Post with id 2 posted 1 time(s)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should validate if entry given for group by is valid`() {
        cmd.execute("--group-by", "whatever")

        assertEquals("Value for group-by is not valid".trimIndent(), cmd.getExecutionResult())
    }

    @Test
    fun `should order scheduled posts by publish date ascending`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        postsRepository.save(
            arrayListOf(
                post1,
                post2,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2023-11-02T10:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post2,
                Instant.parse("2022-10-02T09:00:00Z"),
            ),
        )

        cmd.execute("-o", "publish_date=asc")

        assertEquals(
            """
            1. Post with id 2 will be published on 2022-10-02T09:00:00Z (Twitter)
            2. Post with id 1 will be published on 2023-11-02T10:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should order scheduled posts by publish date descending`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        postsRepository.save(
            arrayListOf(
                post1,
                post2,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2022-10-02T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post2,
                Instant.parse("2023-11-02T10:00:00Z"),
            ),
        )

        cmd.execute("--order-by", "publish_date=desc")

        assertEquals(
            """
            1. Post with id 2 will be published on 2023-11-02T10:00:00Z (Twitter)
            2. Post with id 1 will be published on 2022-10-02T09:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should filter and order the scheduled posts together`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        postsRepository.save(
            arrayListOf(
                post1,
                post2,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2021-10-02T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post2,
                Instant.parse("2023-11-02T10:00:00Z"),
            ),
        )

        cmd.execute("--start-date", "2022-01-01T00:00:00Z", "-o", "publish_date=asc")

        assertEquals(
            """
            1. Post with id 2 will be published on 2023-11-02T10:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should validate if entry given for order by is valid`() {
        cmd.execute("-o", "whatever")

        assertEquals(Messages.INVALID_ORDER_BY_PARAMETER, cmd.getExecutionResult())
    }

    @Test
    fun `should validate if direction given for order by is valid`() {
        cmd.execute("-o", "publish_date=whatever")

        assertEquals(Messages.INVALID_ORDER_BY_PARAMETER, cmd.getExecutionResult())
    }

    @Test
    fun `should filter scheduled posts by day month and year`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        val post3 = SocialPosts(text = "anything-3")
        postsRepository.save(
            arrayListOf(
                post1,
                post2,
                post3,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2022-07-10T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post2,
                Instant.parse("2022-07-11T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post3,
                Instant.parse("2023-07-10T09:00:00Z"),
            ),
        )

        cmd.execute("-f", "day=10&month=07&year=2022")

        assertEquals(
            """
            1. Post with id 1 will be published on 2022-07-10T09:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should filter scheduled posts by a single date part`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        val post3 = SocialPosts(text = "anything-3")
        postsRepository.save(
            arrayListOf(
                post1,
                post2,
                post3,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2022-07-10T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post2,
                Instant.parse("2022-07-11T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post3,
                Instant.parse("2023-07-10T09:00:00Z"),
            ),
        )

        cmd.execute("--filter", "year=2022")

        assertEquals(
            """
            1. Post with id 1 will be published on 2022-07-10T09:00:00Z (Twitter)
            2. Post with id 2 will be published on 2022-07-11T09:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should combine the filter and order criteria`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        val post3 = SocialPosts(text = "anything-3")
        postsRepository.save(
            arrayListOf(
                post1,
                post2,
                post3,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2022-07-10T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post2,
                Instant.parse("2022-07-11T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post3,
                Instant.parse("2023-07-10T09:00:00Z"),
            ),
        )

        cmd.execute("-f", "year=2022", "-o", "publish_date=desc")

        assertEquals(
            """
            1. Post with id 2 will be published on 2022-07-11T09:00:00Z (Twitter)
            2. Post with id 1 will be published on 2022-07-10T09:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should validate if entry given for filter is not a key value pair`() {
        cmd.execute("-f", "whatever")

        assertEquals(Messages.INVALID_FILTER_PARAMETER, cmd.getExecutionResult())
    }

    @Test
    fun `should validate if property given for filter is not supported`() {
        cmd.execute("-f", "week=10")

        assertEquals(Messages.INVALID_FILTER_PARAMETER, cmd.getExecutionResult())
    }

    @Test
    fun `should filter scheduled posts by any property`() {
        val post1 = SocialPosts(text = "release notes")
        val post2 = SocialPosts(text = "random")
        postsRepository.save(
            arrayListOf(
                post1,
                post2,
            ),
        )

        scheduleRepository.save(
            ScheduledItem(
                post1,
                Instant.parse("2022-07-10T09:00:00Z"),
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post2,
                Instant.parse("2022-07-11T09:00:00Z"),
            ),
        )

        cmd.execute("-f", "post.text=release notes")

        assertEquals(
            """
            1. Post with id 1 will be published on 2022-07-10T09:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should show no posts scheduled when the filter matches nothing`() {
        val post = SocialPosts(text = "anything")
        postsRepository.save(
            arrayListOf(
                post,
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post,
                Instant.parse("2022-07-10T09:00:00Z"),
            ),
        )

        cmd.execute("-f", "post.text=missing")

        assertEquals("No posts scheduled", cmd.getExecutionResult())
    }

    @Test
    fun `should interpret date filters using the configured timezone`() {
        configurationRepository.save(SocialConfiguration(timezone = "Europe/Madrid"))
        val post = SocialPosts(text = "anything")
        postsRepository.save(
            arrayListOf(
                post,
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post,
                Instant.parse("2022-10-02T09:00:00Z"),
            ),
        )

        cmd.execute("--start-date", "2022-10-02T08:30:00")

        assertEquals(
            """
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should exclude posts before the local end date using the configured timezone`() {
        configurationRepository.save(SocialConfiguration(timezone = "Europe/Madrid"))
        val post = SocialPosts(text = "anything")
        postsRepository.save(
            arrayListOf(
                post,
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post,
                Instant.parse("2022-10-02T09:00:00Z"),
            ),
        )

        cmd.execute("--end-date", "2022-10-02T08:30:00")

        assertEquals("No posts scheduled", cmd.getExecutionResult())
    }

    @Test
    fun `should search scheduled posts by fuzzy post text`() {
        val post1 = SocialPosts(text = "hello desktop")
        val post2 = SocialPosts(text = "release notes")
        postsRepository.save(arrayListOf(post1, post2))
        scheduleRepository.save(ScheduledItem(post1, Instant.parse("2022-10-02T09:00:00Z")))
        scheduleRepository.save(ScheduledItem(post2, Instant.parse("2022-11-02T09:00:00Z")))

        cmd.execute("--search", "hello desk")

        assertEquals(
            """
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should search scheduled posts by more than one post id`() {
        val post1 = SocialPosts(text = "first")
        val post2 = SocialPosts(text = "second")
        val post3 = SocialPosts(text = "third")
        postsRepository.save(arrayListOf(post1, post2, post3))
        scheduleRepository.save(ScheduledItem(post1, Instant.parse("2022-10-02T09:00:00Z")))
        scheduleRepository.save(ScheduledItem(post2, Instant.parse("2022-10-03T09:00:00Z")))
        scheduleRepository.save(ScheduledItem(post3, Instant.parse("2022-10-04T09:00:00Z")))

        cmd.execute("--ids", "1,3")

        assertEquals(
            """
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z (Twitter)
            2. Post with id 3 will be published on 2022-10-04T09:00:00Z (Twitter)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }

    @Test
    fun `should search scheduled posts by social media`() {
        val post = SocialPosts(text = "release notes")
        postsRepository.save(arrayListOf(post))
        scheduleRepository.save(
            ScheduledItem(post, Instant.parse("2022-10-02T09:00:00Z"), socialMedia = SocialMedia.TWITTER),
        )
        scheduleRepository.save(
            ScheduledItem(post, Instant.parse("2022-10-03T09:00:00Z"), socialMedia = SocialMedia.LINKEDIN),
        )

        cmd.execute("--social-media", "LINKEDIN")

        assertEquals(
            """
            1. Post with id 1 will be published on 2022-10-03T09:00:00Z (LinkedIn)
            """.trimIndent(),
            cmd.getExecutionResult(),
        )
    }
}
