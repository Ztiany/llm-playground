package me.ztiany.llm.funcall

fun main() {
    AIClient.toolChat(
        "1+2+3+4-10+1+4+5=? Just give me a number result.",
        listOf(
            AdditionOperation::class.java,
            SubtractionOperation::class.java
        )
    )
}