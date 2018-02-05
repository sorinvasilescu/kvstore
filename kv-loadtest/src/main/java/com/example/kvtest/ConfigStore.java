package com.example.kvtest;

import org.springframework.stereotype.Component;

@Component
public class ConfigStore {
    public static int requestPutTotal;
    public static int requestsGetTotal;
    public static int requestsDeleteTotal;
    public static int requestsSizeTotal;
    public static int rampUp;
    public static int payloadSize;
}
