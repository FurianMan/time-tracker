package com.github.FurianMan.time_tracker;

import com.google.protobuf.Any;

import java.security.Key;
import java.util.Map;

public class Utilities {
    static private Map<String, String> env = System.getenv();
    static String getConstants (String keyName) {
        return env.get(keyName);
    }
}
