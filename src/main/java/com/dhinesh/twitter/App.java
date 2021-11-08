package com.dhinesh.twitter;

import com.dhinesh.twitter.db.Repository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    public final static Repository repo = new Repository();
    public final static ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
}
