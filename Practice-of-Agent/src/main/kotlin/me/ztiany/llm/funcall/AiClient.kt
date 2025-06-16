package me.ztiany.llm.funcall

import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.chat.completions.*
import me.ztiany.llm.config.AppConfig
import kotlin.jvm.optionals.getOrElse

object AIClient {

    private val client = OpenAIOkHttpClient
        .builder()
        .apiKey(AppConfig.API_KEY)
        .baseUrl(AppConfig.API_URL)
        .build()

    fun toolChat(message: String, tools: List<Class<*>> = emptyList()) {
        val createParamsBuilder: ChatCompletionCreateParams.Builder = ChatCompletionCreateParams.builder()
            .model("qwen-turbo")
            .maxCompletionTokens(2048)
            .apply { tools.forEach { tool -> addTool(tool) } }
            .addUserMessage(message)

        var toolCalls: List<ChatCompletionMessageToolCall>

        do {
            toolCalls = client.chat().completions().create(createParamsBuilder.build())
                .choices()
                .map(ChatCompletion.Choice::message)
                // Add each assistant message onto the builder so that we keep track of the
                // conversation for asking a follow-up question later.
                .onEach { assistant ->
                    println("Assistant: " + assistant.content().getOrElse { "no response!" })
                    createParamsBuilder.addMessage(assistant)
                }
                .flatMap { message ->
                    message.toolCalls().getOrElse { emptyList() }
                }

            toolCalls.forEach {
                val result: Any = callFunction(it.function())
                // Add the tool call result to the conversation.
                createParamsBuilder.addMessage(
                    ChatCompletionToolMessageParam.builder()
                        .toolCallId(it.id())
                        .contentAsJson(result)
                        .build()
                )
            }

        } while (toolCalls.isNotEmpty())
    }

}