package com.github.reline.obd;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import okio.Buffer;

import static org.testng.Assert.assertEquals;

public class JavaTest {

    private Buffer output;
    private Buffer input;
    private ObdSocket socket;

    @BeforeMethod
    public void setup() {
        output = new Buffer();
        input = new Buffer();
        socket = new ObdSocket(output, input);
    }

    @Ignore
    @Test
    public void test() {
        input.writeUtf8("41 11 95>");
        // fixme
//        assertEquals(58.431374f, Obd.getThrottlePosition(socket));
    }
}
