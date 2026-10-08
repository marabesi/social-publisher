package application

class Messages private constructor() {
    companion object {
        const val MISSING_REQUIRED_FIELDS = "Missing required fields"
        const val INVALID_START_DATE = "Invalid start date"
        const val INVALID_END_DATE = "Invalid end date"
        const val INVALID_GROUP_BY_PARAMETER = "Value for group-by is not valid"
        const val INVALID_ORDER_BY_PARAMETER = "Value for order-by is not valid"
        const val INVALID_FILTER_PARAMETER = "Value for filter is not valid"
    }
}
