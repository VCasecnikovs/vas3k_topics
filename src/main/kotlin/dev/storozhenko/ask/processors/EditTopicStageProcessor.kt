package dev.storozhenko.ask.processors

import dev.storozhenko.ask.EditButton
import dev.storozhenko.ask.QuestionStorage
import dev.storozhenko.ask.Stage
import dev.storozhenko.ask.StageProcessor
import dev.storozhenko.ask.Topic
import dev.storozhenko.ask.send
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

class EditTopicStageProcessor(
    private val questionStorage: QuestionStorage
) : StageProcessor {
    override fun ownedStage() = Stage.TOPIC_EDIT

    override fun process(update: Update): (AbsSender) -> Stage {
        val text = update.message.text
            ?: return {
                it.send(update, "Чтобы исправить топик надо нажать кнопку")
                Stage.TOPIC_EDIT
            }
        val topic = Topic.getByName(text)
            ?: return {
                it.send(update, "Нажми на кнопку, глупышка")
                Stage.TOPIC_EDIT
            }
        questionStorage.addTopic(update, topic)
        return {
            it.send(update, questionStorage.getQuestion(update).toString())
            it.send(update, "Топик исправлен, что дальше?") { replyMarkup = EditButton.getKeyboard() }
            Stage.FINAL
        }
    }
}