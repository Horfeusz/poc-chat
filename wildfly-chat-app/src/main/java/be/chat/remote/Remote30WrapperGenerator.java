package be.chat.remote;

import com.sun.enterprise.util.LocalStringManagerImpl;
import org.glassfish.pfl.dynamic.codegen.spi.Expression;
import org.glassfish.pfl.dynamic.codegen.spi.Type;
import org.glassfish.pfl.dynamic.codegen.spi.Wrapper;

import javax.ejb.EJBObject;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Remote30WrapperGenerator extends Generator implements ClassGeneratorFactory {

    private static LocalStringManagerImpl localStrings = new LocalStringManagerImpl(Remote30WrapperGenerator.class);
    private String remoteInterfaceName;
    private Class businessInterface;
    private String remoteClientClassName;
    private String remoteClientPackageName;
    private String remoteClientSimpleName;
    private Method[] bizMethods;
    private ClassLoader loader;

    public String getGeneratedClass() {
        return this.remoteClientClassName;
    }

    public String className() {
        return this.getGeneratedClass();
    }

    public Remote30WrapperGenerator(ClassLoader cl, String businessIntfName, String remoteIntfName) throws GeneratorException {
        this.remoteInterfaceName = remoteIntfName;
        this.loader = cl;

        try {
            this.businessInterface = cl.loadClass(businessIntfName);
        } catch (ClassNotFoundException var5) {
            throw new InvalidBean(localStrings.getLocalString("generator.remote_interface_not_found", "Business interface " + this.businessInterface + " not found "));
        }

        if (EJBObject.class.isAssignableFrom(this.businessInterface)) {
            throw new GeneratorException("Invalid Remote Business Interface " + this.businessInterface + ". A Remote Business interface MUST not extend javax.ejb.EJBObject.");
        } else {
            this.remoteClientClassName = GlassFishEJBUtil.getGeneratedRemoteWrapperName(this.businessInterface.getName());
            this.remoteClientPackageName = this.getPackageName(this.remoteClientClassName);
            this.remoteClientSimpleName = this.getBaseName(this.remoteClientClassName);
            this.bizMethods = this.removeDups(this.businessInterface.getMethods());
        }
    }

    public void evaluate() {
        Wrapper._clear();
        if (this.remoteClientPackageName != null) {
            Wrapper._package(this.remoteClientPackageName);
        } else {
            Wrapper._package();
        }

        Wrapper._class(1, this.remoteClientSimpleName, Wrapper._t("com.sun.ejb.containers.RemoteBusinessWrapperBase"), new Type[]{Wrapper._t(this.businessInterface.getName())});
        Wrapper._data(2, Wrapper._t(this.remoteInterfaceName), "delegate_");
        Wrapper._constructor(1, new Type[0]);
        Wrapper._arg(Wrapper._t(this.remoteInterfaceName), "stub");
        Wrapper._arg(Wrapper._String(), "busIntf");
        Wrapper._body();
        Wrapper._expr(Wrapper._super(Wrapper._s(Wrapper._void(), new Type[]{Wrapper._t("java.rmi.Remote"), Wrapper._String()}), new Expression[]{Wrapper._v("stub"), Wrapper._v("busIntf")}));
        Wrapper._assign(Wrapper._v("delegate_"), Wrapper._v("stub"));
        Wrapper._end();

        for (int i = 0; i < this.bizMethods.length; ++i) {
            this.printMethodImpl(this.bizMethods[i]);
        }

        Wrapper._end();

        try {
            Properties p = new Properties();
            p.put("Wrapper.DUMP_AFTER_SETUP_VISITOR", "true");
            p.put("Wrapper.TRACE_BYTE_CODE_GENERATION", "true");
            p.put("Wrapper.USE_ASM_VERIFIER", "true");
            Wrapper._byteCode(this.loader, p);
        } catch (Exception var2) {
            System.out.println("Got exception when generating byte code");
            var2.printStackTrace();
        }

        Wrapper._classGenerator();
    }

    private void printMethodImpl(Method m) {
        List<Type> exceptionList = new LinkedList();
        Class[] var3 = m.getExceptionTypes();
        int var4 = var3.length;

        Class returnType;
        for (int var5 = 0; var5 < var4; ++var5) {
            returnType = var3[var5];
            exceptionList.add(Type.type(returnType));
        }

        Wrapper._method(1, Type.type(m.getReturnType()), m.getName(), exceptionList);
        int i = 0;
        List<Type> expressionListTypes = new LinkedList();
        List<Expression> expressionList = new LinkedList();
        Class[] var14 = m.getParameterTypes();
        int var7 = var14.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            Class param = var14[var8];
            String paramName = "param" + i;
            Wrapper._arg(Type.type(param), paramName);
            ++i;
            expressionListTypes.add(Type.type(param));
            expressionList.add(Wrapper._v(paramName));
        }

        Wrapper._body();
        Wrapper._try();
        returnType = m.getReturnType();
        if (returnType == Void.TYPE) {
            Wrapper._expr(Wrapper._call(Wrapper._v("delegate_"), m.getName(), Wrapper._s(Type.type(returnType), expressionListTypes), expressionList));
        } else {
            Wrapper._return(Wrapper._call(Wrapper._v("delegate_"), m.getName(), Wrapper._s(Type.type(returnType), expressionListTypes), expressionList));
        }

        boolean doExceptionTranslation = !Remote.class.isAssignableFrom(this.businessInterface);
        if (doExceptionTranslation) {
            Wrapper._catch(Wrapper._t("javax.transaction.TransactionRolledbackException"), "trex");
            Wrapper._define(Wrapper._t("java.lang.RuntimeException"), "r", Wrapper._new(Wrapper._t("javax.ejb.EJBTransactionRolledbackException"), Wrapper._s(Wrapper._void(), new Type[0]), new Expression[0]));
            Wrapper._expr(Wrapper._call(Wrapper._v("r"), "initCause", Wrapper._s(Wrapper._t("java.lang.Throwable"), new Type[]{Wrapper._t("java.lang.Throwable")}), new Expression[]{Wrapper._v("trex")}));
            Wrapper._throw(Wrapper._v("r"));
            Wrapper._catch(Wrapper._t("javax.transaction.TransactionRequiredException"), "treqex");
            Wrapper._define(Wrapper._t("java.lang.RuntimeException"), "r", Wrapper._new(Wrapper._t("javax.ejb.EJBTransactionRequiredException"), Wrapper._s(Wrapper._void(), new Type[0]), new Expression[0]));
            Wrapper._expr(Wrapper._call(Wrapper._v("r"), "initCause", Wrapper._s(Wrapper._t("java.lang.Throwable"), new Type[]{Wrapper._t("java.lang.Throwable")}), new Expression[]{Wrapper._v("treqex")}));
            Wrapper._throw(Wrapper._v("r"));
            Wrapper._catch(Wrapper._t("java.rmi.NoSuchObjectException"), "nsoe");
            Wrapper._define(Wrapper._t("java.lang.RuntimeException"), "r", Wrapper._new(Wrapper._t("javax.ejb.NoSuchEJBException"), Wrapper._s(Wrapper._void(), new Type[0]), new Expression[0]));
            Wrapper._expr(Wrapper._call(Wrapper._v("r"), "initCause", Wrapper._s(Wrapper._t("java.lang.Throwable"), new Type[]{Wrapper._t("java.lang.Throwable")}), new Expression[]{Wrapper._v("nsoe")}));
            Wrapper._throw(Wrapper._v("r"));
            Wrapper._catch(Wrapper._t("java.rmi.AccessException"), "accex");
            Wrapper._define(Wrapper._t("java.lang.RuntimeException"), "r", Wrapper._new(Wrapper._t("javax.ejb.EJBAccessException"), Wrapper._s(Wrapper._void(), new Type[0]), new Expression[0]));
            Wrapper._expr(Wrapper._call(Wrapper._v("r"), "initCause", Wrapper._s(Wrapper._t("java.lang.Throwable"), new Type[]{Wrapper._t("java.lang.Throwable")}), new Expression[]{Wrapper._v("accex")}));
            Wrapper._throw(Wrapper._v("r"));
            Wrapper._catch(Wrapper._t("com.sun.ejb.containers.InternalEJBContainerException"), "iejbcEx");
            Wrapper._throw(Wrapper._cast(Wrapper._t("javax.ejb.EJBException"), Wrapper._call(Wrapper._v("iejbcEx"), "getCause", Wrapper._s(Wrapper._t("java.lang.Throwable"), new Type[0]), new Expression[0])));
            Wrapper._catch(Wrapper._t("java.rmi.RemoteException"), "re");
            Wrapper._throw(Wrapper._new(Wrapper._t("javax.ejb.EJBException"), Wrapper._s(Wrapper._void(), new Type[]{Wrapper._t("java.lang.Exception")}), new Expression[]{Wrapper._v("re")}));
            Wrapper._catch(Wrapper._t("org.omg.CORBA.SystemException"), "corbaSysEx");
            Wrapper._define(Wrapper._t("java.lang.RuntimeException"), "r", Wrapper._new(Wrapper._t("javax.ejb.EJBException"), Wrapper._s(Wrapper._void(), new Type[0]), new Expression[0]));
            Wrapper._expr(Wrapper._call(Wrapper._v("r"), "initCause", Wrapper._s(Wrapper._t("java.lang.Throwable"), new Type[]{Wrapper._t("java.lang.Throwable")}), new Expression[]{Wrapper._v("corbaSysEx")}));
            Wrapper._throw(Wrapper._v("r"));
            Wrapper._end();
        } else {
            Wrapper._catch(Wrapper._t("com.sun.ejb.containers.InternalEJBContainerException"), "iejbcEx");
            Wrapper._throw(Wrapper._new(Wrapper._t("com.sun.ejb.containers.InternalRemoteException"), Wrapper._s(Wrapper._void(), new Type[]{Wrapper._t("com.sun.ejb.containers.InternalEJBContainerException")}), new Expression[]{Wrapper._v("iejbcEx")}));
            Wrapper._end();
        }

        Wrapper._end();
    }

}
