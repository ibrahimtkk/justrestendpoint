package ut.com.veniture.plugins.tutorial;

import org.junit.Test;
import com.veniture.plugins.tutorial.api.MyPluginComponent;
import com.veniture.plugins.tutorial.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}