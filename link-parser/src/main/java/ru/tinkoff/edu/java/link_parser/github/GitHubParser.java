package ru.tinkoff.edu.java.link_parser.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.util.List;

@Component
public class GitHubParser extends LinkParser {
    private String gitHubHost;

    @Value("${github.host}")
    public void setGitHubHost(String gitHubHost) {
        this.gitHubHost = gitHubHost;
    }

    @Override
    protected GitHubParserResult createResult(String path) {
        List<String> segments = getURLPathSegments(path);
        return new GitHubParserResult(segments.get(0), segments.get(1));
    }

    @Override
    protected String getHost() {
        return gitHubHost;
    }

    @Override
    protected boolean isPathValid(String path) {
        return getURLPathSegments(path).size() == 2;
    }
}
