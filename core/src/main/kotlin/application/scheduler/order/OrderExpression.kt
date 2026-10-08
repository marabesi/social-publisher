package application.scheduler.order

object OrderExpression {
    private const val EXPRESSION_SEPARATOR = "="
    private const val PUBLISH_DATE_FIELD = "publish_date"
    private const val ASC = "asc"
    private const val DESC = "desc"

    fun parse(expression: String): Order? {
        val parts = expression.split(EXPRESSION_SEPARATOR)
        if (parts.size != 2 || parts[0] != PUBLISH_DATE_FIELD) {
            return null
        }

        return when (parts[1]) {
            ASC -> PublishDateOrder(Direction.ASC)
            DESC -> PublishDateOrder(Direction.DESC)
            else -> null
        }
    }
}
