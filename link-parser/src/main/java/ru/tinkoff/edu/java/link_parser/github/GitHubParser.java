package ru.tinkoff.edu.java.link_parser.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.net.URI;
import java.util.List;

@Component
public final class GitHubParser extends LinkParser {
    private final String gitHubHost;

    public GitHubParser(final @Value("${github.host}") String gitHubHost) {
        this.gitHubHost = gitHubHost;
    }

    @Override
    protected GitHubParserResult createResult(final URI uri) {
        List<String> segments = getURIPathSegments(uri);
        return new GitHubParserResult(segments.get(0), segments.get(1));
    }

    @Override
    protected boolean isURISupported(final URI uri) {
        return super.isURISupported(uri) && getURIHost(uri).equals(gitHubHost);
    }

    @Override
    protected boolean canTakeDataFromURI(final URI uri) {
        return getURIPathSegments(uri).size() == 2;
    }
}
