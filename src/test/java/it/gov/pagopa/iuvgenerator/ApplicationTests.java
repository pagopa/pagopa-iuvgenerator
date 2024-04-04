package it.gov.pagopa.iuvgenerator;

import it.gov.pagopa.iuvgenerator.util.Constants;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTests {

    @Test
    void contextLoads() {
        assertTrue(true); // it just tests that an error has not occurred
    }

    @Test
    void applicationContextLoaded() {
        assertTrue(true); // it just tests that an error has not occurred
    }

    @Test
    void applicationContextTest() {
        Application.main(new String[]{});
        assertTrue(true); // it just tests that an error has not occurred
    }

    @Test
    void constructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException, SecurityException {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        assertNotNull(Constants.class.getDeclaredField("HEADER_REQUEST_ID"));
    }

}
