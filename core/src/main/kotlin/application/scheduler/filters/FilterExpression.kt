package application.scheduler.filters

object FilterExpression {
    private const val EXPRESSION_SEPARATOR = "&"
    private const val KEY_VALUE_SEPARATOR = "="

    fun parse(expression: String): ArrayList<Criterion>? {
        val criteria = arrayListOf<Criterion>()
        val availableKeys = ScheduledItemProperties.availableKeys()

        for (part in expression.split(EXPRESSION_SEPARATOR)) {
            val pieces = part.split(KEY_VALUE_SEPARATOR)
            if (pieces.size != 2 || !availableKeys.contains(pieces[0])) {
                return null
            }

            criteria.add(PropertyFilter(pieces[0], pieces[1]))
        }

        return criteria
    }
}
