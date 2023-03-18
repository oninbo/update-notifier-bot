package ru.tinkoff.edu.java.scrapper.client;

public class StackOverflowClient extends StackExchangeSiteClient {
    public StackOverflowClient(StackExchangeClient stackExchangeClient) {
        super(stackExchangeClient);
    }

    @Override
    protected String getSite() {
        return "stackoverflow";
    }
}
