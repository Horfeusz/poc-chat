package be.chat.remote;

import com.sun.enterprise.util.LocalStringManagerImpl;
import org.glassfish.pfl.dynamic.codegen.spi.Type;
import org.glassfish.pfl.dynamic.codegen.spi.Wrapper;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class RemoteGenerator extends Generator implements ClassGeneratorFactory {

    private static LocalStringManagerImpl localStrings = new LocalStringManagerImpl(RemoteGenerator.class);
    private Class businessInterface;
    private Method[] bizMethods;
    private String remoteInterfacePackageName;
    private String remoteInterfaceSimpleName;
    private String remoteInterfaceName;

    public String getGeneratedClass() {
        return this.remoteInterfaceName;
    }

    public String className() {
        return this.getGeneratedClass();
    }

    public RemoteGenerator(ClassLoader cl, String businessIntf) throws GeneratorException {
        try {
            this.businessInterface = cl.loadClass(businessIntf);
        } catch (ClassNotFoundException var4) {
            throw new InvalidBean(localStrings.getLocalString("generator.remote_interface_not_found", "Remote interface not found "));
        }

        this.remoteInterfaceName = GlassFishEJBUtil.getGeneratedRemoteIntfName(this.businessInterface.getName());
        this.remoteInterfacePackageName = this.getPackageName(this.remoteInterfaceName);
        this.remoteInterfaceSimpleName = this.getBaseName(this.remoteInterfaceName);
        this.bizMethods = this.removeDups(this.businessInterface.getMethods());
    }

    public void evaluate() {
        Wrapper._clear();
        if (this.remoteInterfacePackageName != null) {
            Wrapper._package(this.remoteInterfacePackageName);
        } else {
            Wrapper._package();
        }

        Wrapper._interface(1, this.remoteInterfaceSimpleName, new Type[]{Wrapper._t("java.rmi.Remote"), Wrapper._t("com.sun.ejb.containers.RemoteBusinessObject")});

        for (int i = 0; i < this.bizMethods.length; ++i) {
            this.printMethod(this.bizMethods[i]);
        }

        Wrapper._end();
        Wrapper._classGenerator();
    }

    private void printMethod(Method m) {
        boolean throwsRemoteException = false;
        List<Type> exceptionList = new LinkedList();
        Class[] var4 = m.getExceptionTypes();
        int var5 = var4.length;

        int var6;
        for (var6 = 0; var6 < var5; ++var6) {
            Class exception = var4[var6];
            exceptionList.add(Type.type(exception));
            if (exception.getName().equals("java.rmi.RemoteException")) {
                throwsRemoteException = true;
            }
        }

        if (!throwsRemoteException) {
            exceptionList.add(Wrapper._t("java.rmi.RemoteException"));
        }

        exceptionList.add(Wrapper._t("com.sun.ejb.containers.InternalEJBContainerException"));
        Wrapper._method(1025, Type.type(m.getReturnType()), m.getName(), exceptionList);
        int i = 0;
        Class[] var10 = m.getParameterTypes();
        var6 = var10.length;

        for (int var11 = 0; var11 < var6; ++var11) {
            Class param = var10[var11];
            Wrapper._arg(Type.type(param), "param" + i);
            ++i;
        }

        Wrapper._end();
    }
}
