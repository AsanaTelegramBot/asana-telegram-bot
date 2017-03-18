package ru.ottepel.command;

import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResult;
import ru.ottepel.storage.AbstractStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by savetisyan on 17/03/17
 */
public class TypeAheadSearch {
    private AbstractStorage storage;

    public TypeAheadSearch(AbstractStorage storage) {
        this.storage = storage;
    }

    public AnswerInlineQuery search(Update update) {
        InlineQuery inlineQuery = update.getInlineQuery();
        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(inlineQuery.getId());
//        answer.setCacheTime(CACHE_TIME);
        answer.setSwitchPmText("Login in Asana...");
        answer.setSwitchPmParameter(String.valueOf(update.getInlineQuery().getFrom().getId()));
        answer.setResults(Collections.emptyList());
        return answer;
    }

    private List<InlineQueryResult> getResults(Long chatId, InlineQuery inlineQuery) {
        List<InlineQueryResult> results = new ArrayList<>();
        String query = inlineQuery.getQuery();
        return null;
    }
}
