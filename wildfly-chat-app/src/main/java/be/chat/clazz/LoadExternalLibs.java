package be.chat.clazz;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

//@Singleton
//@Startup
public class LoadExternalLibs {

    //@PostConstruct
    public void init() {
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        DynamicURLClassLoader dynamicURLClassLoader = new DynamicURLClassLoader(urlClassLoader);

        File jarToAdd = new File("C:\\tmp\\glassfish-naming.jar");
        try {
            dynamicURLClassLoader.addURL(jarToAdd.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}
