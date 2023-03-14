package ru.tinkoff.edu.java.link_parser.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.net.URL;
import java.util.List;

@Component
public class GitHubParser extends LinkParser {
    private String gitHubHost;

    @Value("${github.host}")
    public void setGitHubHost(String gitHubHost) {
        this.gitHubHost = gitHubHost;
    }

    @Override
    protected GitHubParserResult createResult(URL url) {
        List<String> segments = getURLPathSegments(url.getPath());
        return new GitHubParserResult(segments.get(0), segments.get(1));
    }

    @Override
    protected boolean isURLSupported(URL url) {
        return url.getHost().equals(gitHubHost);
    }

    @Override
    protected boolean canTakeDataFromURL(URL url) {
        return getURLPathSegments(url.getPath()).size() == 2;
    }
}
