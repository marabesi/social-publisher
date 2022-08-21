package application

class Messages private constructor(){
    companion object {
        const val MISSING_REQUIRED_FIELDS = "Missing required fields"
        const val INVALID_START_DATE = "Invalid start date"
        const val INVALID_END_DATE = "Invalid end date"
        const val INVALID_GROUP_BY_PARAMETER = "Value for group-by is not valid"
    }
}
