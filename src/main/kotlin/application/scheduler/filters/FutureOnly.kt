package application.scheduler.filters

import java.time.Instant

class FutureOnly(private val currentTime: Instant): Criterion {
    override fun apply(): Filter {
        return Filter("publishDate", ">=", currentTime)
    }
}
