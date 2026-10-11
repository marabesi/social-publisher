package application.order

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class OrderExpressionTest {
    private class RecordingOrder(
        val direction: Direction,
    ) : Order<String> {
        override fun apply(items: ArrayList<String>): ArrayList<String> = ArrayList(items)
    }

    @Test
    fun `should parse an ascending expression for the expected field`() {
        val order = OrderExpression.parse("id=asc", "id") { RecordingOrder(it) }

        assertEquals(Direction.ASC, (order as RecordingOrder).direction)
    }

    @Test
    fun `should parse a descending expression for the expected field`() {
        val order = OrderExpression.parse("id=desc", "id") { RecordingOrder(it) }

        assertEquals(Direction.DESC, (order as RecordingOrder).direction)
    }

    @Test
    fun `should return null when the field does not match`() {
        assertNull(OrderExpression.parse("publish_date=asc", "id") { RecordingOrder(it) })
    }

    @Test
    fun `should return null when the direction is invalid`() {
        assertNull(OrderExpression.parse("id=sideways", "id") { RecordingOrder(it) })
    }

    @Test
    fun `should return null when the expression is malformed`() {
        assertNull(OrderExpression.parse("id", "id") { RecordingOrder(it) })
    }
}
