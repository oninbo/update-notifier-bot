package ru.tinkoff.edu.java.link_parser.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.util.List;

@Component
public class GitHubParser extends LinkParser {
    private final String gitHubHost;

    public GitHubParser(@Value("${github.host}") String gitHubHost) {
        this.gitHubHost = gitHubHost;
    }

    @Override
    protected GitHubParserResult createResult(Link link) {
        List<String> segments = getURIPathSegments(link.path());
        return new GitHubParserResult(segments.get(0), segments.get(1));
    }

    @Override
    protected boolean isLinkSupported(Link link) {
        return link.host().equals(gitHubHost);
    }

    @Override
    protected boolean canTakeDataFromLink(Link link) {
        return getURIPathSegments(link.path()).size() == 2;
    }
}
