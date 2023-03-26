package ru.tinkoff.edu.java.link_parser.base_parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LinkParserTest {
    private LinkParser linkParser;

    @BeforeEach
    public void initialize() {
        linkParser = mock(LinkParser.class);
        when(linkParser.parse(any())).thenCallRealMethod();
    }

    @Test
    public void shouldDetectInvalidLink() throws URISyntaxException {
        URI link = new URI("a");
        assertThrows(LinkParserIncorrectLinkException.class, () -> linkParser.parse(link));
    }

    @Test
    public void shouldDetectUnsupportedURISchema() throws URISyntaxException {
        URI link = new URI("ftp://a/b");
        assertNull(linkParser.parse(link));
    }
}
