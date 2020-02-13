package be.chat.remote;

import com.sun.logging.LogDomains;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.glassfish.pfl.dynamic.codegen.spi.Wrapper;

import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlassFishEJBUtil {

    private static final Logger _logger = LogDomains.getLogger(GlassFishEJBUtil.class, "javax.enterprise.system.container.ejb");
    private static final String EJB_USE_STATIC_CODEGEN_PROP = "com.sun.ejb.UseStaticCodegen";
    private static final String REMOTE30_HOME_JNDI_SUFFIX = "__3_x_Internal_RemoteBusinessHome__";
    private static Boolean ejbUseStaticCodegen_ = null;
    private static final String CORBA_INS_PREFIX = "corbaname:";
    private static final String JAVA_GLOBAL_PREFIX = "java:global/";
    private static final String PORTABLE_JNDI_NAME_SEP = "!";
    private static final String GLASSFISH_JNDI_NAME_SEP = "#";


    public static Object lookupRemote30BusinessObject(Object jndiObj, String businessInterface) throws NamingException {
        RemoteBusinessWrapperBase returnObject = null;

        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class genericEJBHome = loadGeneratedGenericEJBHomeClass(loader);
            Object genericHomeObj = PortableRemoteObject.narrow(jndiObj, genericEJBHome);
            loadGeneratedRemoteBusinessClasses(businessInterface);
            String generatedRemoteIntfName = getGeneratedRemoteIntfName(businessInterface);
            Method createMethod = genericEJBHome.getMethod("create", String.class);
            Remote delegate = (Remote) createMethod.invoke(genericHomeObj, generatedRemoteIntfName);
            returnObject = createRemoteBusinessObject(loader, businessInterface, delegate);
            return returnObject;
        } catch (Exception var9) {
            NamingException ne = new NamingException("ejb ref resolution error for remote business interface" + businessInterface);
            ne.initCause((Throwable) (var9 instanceof InvocationTargetException ? var9.getCause() : var9));
            throw ne;
        }
    }

    public static String getGenericEJBHomeClassName() {
        return "com.sun.ejb.codegen.GenericEJBHome_Generated";
    }


    private static Class generateAndLoad(ClassGeneratorFactory cgf, String actualClassName, final ClassLoader loader, final Class protectionDomainBase) {
        cgf.evaluate();
        final Properties props = new Properties();
        ByteArrayOutputStream baos;
        if (_logger.isLoggable(Level.FINE)) {
            props.put("org.glassfish.dynamic.codegen.debug.dumpAfterSetupVisitor", "true");
            props.put("org.glassfish.dynamic.codegen.debug.traceByteCodeGeneration", "true");
            props.put("org.glassfish.dynamic.codegen.debug.useAsmVerifier", "true");

            try {
                baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                Wrapper._sourceCode(ps, props);
                _logger.fine(baos.toString());
            } catch (Exception var10) {
                _logger.log(Level.FINE, "exception generating src", var10);
            }
        }

        baos = null;

        Class result;
        try {
            if (System.getSecurityManager() == null) {
                result = Wrapper._generate(loader, protectionDomainBase.getProtectionDomain(), props);
            } else {
                result = (Class) AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        return Wrapper._generate(loader, protectionDomainBase.getProtectionDomain(), props);
                    }
                });
            }
        } catch (RuntimeException var9) {
            RuntimeException runEx = var9;

            try {
                result = loader.loadClass(actualClassName);
                _logger.log(Level.FINE, "[EJBUtils] Got exception ex: " + runEx + " but loaded class: " + result.getName());
            } catch (ClassNotFoundException var8) {
                throw var9;
            }
        }

        return result;
    }

    public static Class loadGeneratedGenericEJBHomeClass(ClassLoader appClassLoader) throws Exception {
        String className = getGenericEJBHomeClassName();
        Class generatedGenericEJBHomeClass = null;

        try {
            generatedGenericEJBHomeClass = appClassLoader.loadClass(className);
        } catch (Exception var4) {
        }

        if (generatedGenericEJBHomeClass == null) {
            GenericHomeGenerator gen = new GenericHomeGenerator(appClassLoader);
            generatedGenericEJBHomeClass = generateAndLoad(gen, className, appClassLoader, GlassFishEJBUtil.class);
        }

        return generatedGenericEJBHomeClass;
    }

    private static ClassLoader getBusinessIntfClassLoader(String businessInterface) throws Exception {
        ClassLoader contextLoader = null;
        if (System.getSecurityManager() == null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            contextLoader = cl != null ? cl : ClassLoader.getSystemClassLoader();
        } else {
            contextLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    return cl != null ? cl : ClassLoader.getSystemClassLoader();
                }
            });
        }

        final Class businessInterfaceClass = contextLoader.loadClass(businessInterface);
        ClassLoader appClassLoader = null;
        if (System.getSecurityManager() == null) {
            appClassLoader = businessInterfaceClass.getClassLoader();
        } else {
            appClassLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return businessInterfaceClass.getClassLoader();
                }
            });
        }

        return appClassLoader;
    }

    private static String getClassPackageName(String intf) {
        int dot = intf.lastIndexOf(46);
        return dot == -1 ? null : intf.substring(0, dot);
    }

    private static String getClassSimpleName(String intf) {
        int dot = intf.lastIndexOf(46);
        return dot == -1 ? intf : intf.substring(dot + 1);
    }

    public static String getGeneratedRemoteIntfName(String businessIntf) {
        String packageName = getClassPackageName(businessIntf);
        String simpleName = getClassSimpleName(businessIntf);
        String generatedSimpleName = "_" + simpleName + "_Remote";
        return packageName != null ? packageName + "." + generatedSimpleName : generatedSimpleName;
    }

    public static void loadGeneratedRemoteBusinessClasses(String businessInterfaceName) throws Exception {
        ClassLoader appClassLoader = getBusinessIntfClassLoader(businessInterfaceName);
        loadGeneratedRemoteBusinessClasses(appClassLoader, businessInterfaceName);
    }

    public static String getGeneratedRemoteWrapperName(String businessIntf) {
        String packageName = getClassPackageName(businessIntf);
        String simpleName = getClassSimpleName(businessIntf);
        String generatedSimpleName = "_" + simpleName + "_Wrapper";
        return packageName != null ? packageName + "." + generatedSimpleName : generatedSimpleName;
    }

    public static void loadGeneratedRemoteBusinessClasses(ClassLoader appClassLoader, String businessInterfaceName) throws Exception {
        String generatedRemoteIntfName = getGeneratedRemoteIntfName(businessInterfaceName);
        String wrapperClassName = getGeneratedRemoteWrapperName(businessInterfaceName);
        Class generatedRemoteIntf = null;

        try {
            generatedRemoteIntf = appClassLoader.loadClass(generatedRemoteIntfName);
        } catch (Exception var13) {
        }

        Class generatedRemoteWrapper = null;

        try {
            generatedRemoteWrapper = appClassLoader.loadClass(wrapperClassName);
        } catch (Exception var12) {
        }

        if (generatedRemoteIntf == null || generatedRemoteWrapper == null) {
            Wrapper._setClassLoader(appClassLoader);

            try {
                Class developerClass;
                if (generatedRemoteIntf == null) {
                    RemoteGenerator gen = new RemoteGenerator(appClassLoader, businessInterfaceName);
                    developerClass = appClassLoader.loadClass(businessInterfaceName);
                    generateAndLoad(gen, generatedRemoteIntfName, appClassLoader, developerClass);
                }

                if (generatedRemoteWrapper == null) {
                    Remote30WrapperGenerator gen = new Remote30WrapperGenerator(appClassLoader, businessInterfaceName, generatedRemoteIntfName);
                    developerClass = appClassLoader.loadClass(businessInterfaceName);
                    generateAndLoad(gen, wrapperClassName, appClassLoader, developerClass);
                }
            } finally {
                Wrapper._setClassLoader((ClassLoader) null);
            }

        }
    }

    public static RemoteBusinessWrapperBase createRemoteBusinessObject(String businessInterface, Remote delegate) throws Exception {
        ClassLoader appClassLoader = getBusinessIntfClassLoader(businessInterface);
        return createRemoteBusinessObject(appClassLoader, businessInterface, delegate);
    }

    public static RemoteBusinessWrapperBase createRemoteBusinessObject(ClassLoader loader, String businessInterface, Remote delegate) throws Exception {
        String wrapperClassName = getGeneratedRemoteWrapperName(businessInterface);
        Class clientWrapperClass = loader.loadClass(wrapperClassName);
        Constructor[] ctors = clientWrapperClass.getConstructors();
        Constructor ctor = null;
        Constructor[] var7 = ctors;
        int var8 = ctors.length;

        for (int var9 = 0; var9 < var8; ++var9) {
            Constructor next = var7[var9];
            if (next.getParameterTypes().length > 0) {
                ctor = next;
                break;
            }
        }

        Object obj = null;
        if (ctor != null) {
            obj = ctor.newInstance(delegate, businessInterface);
        }

        return (RemoteBusinessWrapperBase) obj;
    }

}
