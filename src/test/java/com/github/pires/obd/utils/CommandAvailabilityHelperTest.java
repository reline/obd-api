/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.pires.obd.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CommandAvailabilityHelperTest {

    @Test
    public void testDigestAvailabilityString() throws Exception {
        int[] expected = new int[]{Integer.parseInt("10111110", 2), Integer.parseInt("00011111", 2),
                Integer.parseInt("10101000", 2), Integer.parseInt("00010011", 2)};
        int[] result = CommandAvailabilityHelper.INSTANCE.digestAvailabilityString("BE1FA813");
        Assert.assertEquals(expected, result);

        //Now with 16 characters
        expected = new int[]{Integer.parseInt("10111110", 2), Integer.parseInt("00011111", 2),
                Integer.parseInt("10101000", 2), Integer.parseInt("00010011", 2),
                Integer.parseInt("10111110", 2), Integer.parseInt("00011111", 2),
                Integer.parseInt("10101000", 2), Integer.parseInt("00010011", 2)};

        result = CommandAvailabilityHelper.INSTANCE.digestAvailabilityString("BE1FA813BE1FA813");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testIsAvailable() throws Exception {
        Assert.assertEquals(CommandAvailabilityHelper.INSTANCE.isAvailable("02", "BE1FA813"), false);
        Assert.assertEquals(CommandAvailabilityHelper.INSTANCE.isAvailable("07", "BE1FA813"), true);
        Assert.assertEquals(CommandAvailabilityHelper.INSTANCE.isAvailable("11", "BE1FA813"), true);
        Assert.assertEquals(CommandAvailabilityHelper.INSTANCE.isAvailable("1A", "BE1FA813"), false);
        Assert.assertEquals(CommandAvailabilityHelper.INSTANCE.isAvailable("1D", "BE1FA813"), false);
        Assert.assertEquals(CommandAvailabilityHelper.INSTANCE.isAvailable("1F", "BE1FA813"), true);
        Assert.assertEquals(CommandAvailabilityHelper.INSTANCE.isAvailable("22", "BE1FA813BE1FA813"), false);
        Assert.assertEquals(CommandAvailabilityHelper.INSTANCE.isAvailable("27", "BE1FA813BE1FA813"), true);
        Assert.assertEquals(CommandAvailabilityHelper.INSTANCE.isAvailable("3A", "BE1FA813BE1FA813"), false);
        Assert.assertEquals(CommandAvailabilityHelper.INSTANCE.isAvailable("3D", "BE1FA813BE1FA813"), false);
        Assert.assertEquals(CommandAvailabilityHelper.INSTANCE.isAvailable("3F", "BE1FA813BE1FA813"), true);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFail() throws Exception {
        CommandAvailabilityHelper.INSTANCE.digestAvailabilityString("AAA");
        CommandAvailabilityHelper.INSTANCE.digestAvailabilityString("AAAAAAAR");
        CommandAvailabilityHelper.INSTANCE.isAvailable("2F", "BE1FA813");
    }

}
