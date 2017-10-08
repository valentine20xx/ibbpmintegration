package org.example.connector;

import org.junit.Assert;
import org.junit.Test;

public final class ConnectorUtilsTest {
    @Test
    public void generateCorrelationKeyTest() {
        String correlationKey = ConnectorUtils.generateCorrelationKey();

        Assert.assertEquals(8, correlationKey.length());
    }
}