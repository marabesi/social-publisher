package application.order

object OrderExpression {
    private const val EXPRESSION_SEPARATOR = "="
    private const val ASC = "asc"
    private const val DESC = "desc"

    fun <T> parse(
        expression: String,
        field: String,
        create: (Direction) -> Order<T>,
    ): Order<T>? {
        val parts = expression.split(EXPRESSION_SEPARATOR)
        if (parts.size != 2 || parts[0] != field) {
            return null
        }

        return when (parts[1]) {
            ASC -> create(Direction.ASC)
            DESC -> create(Direction.DESC)
            else -> null
        }
    }
}
