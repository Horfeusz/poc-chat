package be.chat.remote;

import com.sun.enterprise.util.LocalStringManagerImpl;
import org.glassfish.pfl.dynamic.codegen.spi.Type;
import org.glassfish.pfl.dynamic.codegen.spi.Wrapper;

public class GenericHomeGenerator extends Generator implements ClassGeneratorFactory {

    private static LocalStringManagerImpl localStrings = new LocalStringManagerImpl(GenericHomeGenerator.class);
    private String genericEJBHomeClassName = GlassFishEJBUtil.getGenericEJBHomeClassName();
    private ClassLoader loader;

    public String getGeneratedClass() {
        return this.genericEJBHomeClassName;
    }

    public String className() {
        return this.getGeneratedClass();
    }

    public GenericHomeGenerator(ClassLoader cl) throws GeneratorException {
        this.loader = cl;
    }

    public void evaluate() {
        Wrapper._clear();
        String packageName = this.getPackageName(this.genericEJBHomeClassName);
        String simpleName = this.getBaseName(this.genericEJBHomeClassName);
        Wrapper._package(packageName);
        Wrapper._interface(1, simpleName, new Type[]{Wrapper._t("com.sun.ejb.containers.GenericEJBHome")});
        Wrapper._method(1025, Wrapper._t("java.rmi.Remote"), "create", new Type[]{Wrapper._t("java.rmi.RemoteException")});
        Wrapper._arg(Wrapper._String(), "generatedBusinessIntf");
        Wrapper._end();
        Wrapper._classGenerator();
    }
}
