package ru.tinkoff.edu.java.scrapper.dto;

import java.net.URI;
import java.util.List;

public record StackOverflowAnswerUpdate(URI questionUrl, URI answerUrl, List<Long> chatIds) {
}
