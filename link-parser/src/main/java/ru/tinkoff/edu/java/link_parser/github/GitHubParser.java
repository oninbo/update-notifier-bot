package ru.tinkoff.edu.java.link_parser.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.net.URI;
import java.util.List;

@Component
public class GitHubParser extends LinkParser {
    private final String gitHubHost;

    public GitHubParser(@Value("${github.host}") String gitHubHost) {
        this.gitHubHost = gitHubHost;
    }

    @Override
    protected GitHubParserResult createResult(URI uri) {
        List<String> segments = getURIPathSegments(uri);
        return new GitHubParserResult(segments.get(0), segments.get(1));
    }

    @Override
    protected boolean isURISupported(URI uri) {
        return super.isURISupported(uri) && getURIHost(uri).equals(gitHubHost);
    }

    @Override
    protected boolean canTakeDataFromURI(URI uri) {
        return getURIPathSegments(uri).size() == 2;
    }
}
