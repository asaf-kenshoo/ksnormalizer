package com.kenshoo.integrations.service;

import java.io.IOException;

public interface KsNormalizerClient {
    String normalize(String ksId) throws IOException;
}
