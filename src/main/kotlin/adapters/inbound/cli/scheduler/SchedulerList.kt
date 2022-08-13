package adapters.inbound.cli.scheduler

import application.Output
import application.persistence.SchedulerRepository
import application.scheduler.List
import com.google.inject.Inject
import picocli.CommandLine
import java.time.Instant
import java.util.concurrent.Callable

@CommandLine.Command(name = "list")
open class SchedulerList(
    @Inject
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output,
    currentTime: Instant
): Callable<String> {
    override fun call(): String {
        return List(scheduleRepository, cliOutput).invoke()
    }
}
