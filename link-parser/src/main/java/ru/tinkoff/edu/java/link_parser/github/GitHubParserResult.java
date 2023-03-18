package ru.tinkoff.edu.java.link_parser.github;

import ru.tinkoff.edu.java.link_parser.LinkParserResult;

public record GitHubParserResult(String userName, String projectName) implements LinkParserResult {
}
