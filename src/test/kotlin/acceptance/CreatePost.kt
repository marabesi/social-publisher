package acceptance

import io.cucumber.java8.En
import io.cucumber.java8.PendingException

class CreatePost: En {
    init {
        When(
            "I create a post with the title {string}"
        ) { _: String? ->

        }
        Then(
            "Show successfull menssage {string}"
        ) { _: String? -> throw PendingException() }
    }
}