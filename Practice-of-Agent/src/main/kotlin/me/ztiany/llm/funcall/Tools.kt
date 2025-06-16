package me.ztiany.llm.funcall

import com.fasterxml.jackson.annotation.JsonClassDescription
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.openai.models.chat.completions.ChatCompletionMessageToolCall

/*
 * 计算工具定义，参考：
 *
 *  https://github.com/openai/openai-java/blob/main/openai-java-example/src/main/java/com/openai/example/FunctionCallingExample.java
 */
@JsonClassDescription("Adds two numbers together.")
class AdditionOperation {

    @JsonPropertyDescription("All numbers to add together.")
    var numbers: List<Int> = emptyList()

    fun execute(): Int {
        return numbers.sum()
    }

}

@JsonClassDescription("Subtracts a list of numbers from a total.")
class SubtractionOperation {

    @JsonPropertyDescription("The number to subtract from.")
    var total: Int = 0

    @JsonPropertyDescription("The number to subtract.")
    var numbers: List<Int> = emptyList()

    fun execute(): Int {
        return total - numbers.sum()
    }

}

internal fun callFunction(function: ChatCompletionMessageToolCall.Function): Any {
    return when (function.name()) {
        "AdditionOperation" -> function.arguments(AdditionOperation::class.java).execute()
        "SubtractionOperation" -> function.arguments(SubtractionOperation::class.java).execute()
        else -> throw IllegalArgumentException("Unknown function: " + function.name())
    }.also {
        println("    Calling function: $function. Result: $it")
    }
}