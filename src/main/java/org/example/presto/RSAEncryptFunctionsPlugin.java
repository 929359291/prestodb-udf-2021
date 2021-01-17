package org.example.presto;

import com.facebook.presto.spi.Plugin;
import java.util.HashSet;
import java.util.Set;

public class RSAEncryptFunctionsPlugin implements Plugin {

    @Override
    public Set<Class<?>> getFunctions() {
        HashSet<Class<?>> set = new HashSet<>();
        set.add(RSAEncryptFunctions.class);
        return set;
    }
}
