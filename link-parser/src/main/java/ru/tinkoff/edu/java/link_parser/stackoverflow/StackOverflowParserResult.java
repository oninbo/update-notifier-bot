package ru.tinkoff.edu.java.link_parser.stackoverflow;

import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserResult;

public record StackOverflowParserResult(String questionId) implements LinkParserResult {
}
