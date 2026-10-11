package application.order

interface Order<T> {
    fun apply(items: ArrayList<T>): ArrayList<T>
}
