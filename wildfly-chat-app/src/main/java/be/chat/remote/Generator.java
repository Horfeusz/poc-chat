package be.chat.remote;

import com.sun.enterprise.deployment.EjbDescriptor;
import com.sun.enterprise.deployment.EjbSessionDescriptor;
import com.sun.enterprise.deployment.MethodDescriptor;
import com.sun.logging.LogDomains;
import lombok.NoArgsConstructor;
import org.glassfish.api.deployment.DeploymentContext;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

@NoArgsConstructor
public abstract class Generator {

    protected static final Logger _logger = LogDomains.getLogger(Generator.class, "javax.enterprise.system.container.ejb");

    protected String ejbClassSymbol;

    public abstract String getGeneratedClass();

    protected String getPackageName(String className) {
        int dot = className.lastIndexOf(46);
        return dot == -1 ? null : className.substring(0, dot);
    }

    protected String getBaseName(String className) {
        int dot = className.lastIndexOf(46);
        return dot == -1 ? className : className.substring(dot + 1);
    }

    protected String printType(Class cls) {
        return cls.isArray() ? this.printType(cls.getComponentType()) + "[]" : cls.getName();
    }

    protected Method[] removeDups(Method[] orig) {
        Vector nodups = new Vector();

        for (int i = 0; i < orig.length; ++i) {
            Method m1 = orig[i];
            boolean dup = false;
            Enumeration e = nodups.elements();

            label50:
            while (true) {
                Method m2;
                Class[] m1parms;
                Class[] m2parms;
                do {
                    do {
                        if (!e.hasMoreElements()) {
                            break label50;
                        }

                        m2 = (Method) e.nextElement();
                    } while (!m1.getName().equals(m2.getName()));

                    m1parms = m1.getParameterTypes();
                    m2parms = m2.getParameterTypes();
                } while (m1parms.length != m2parms.length);

                boolean parmsDup = true;

                for (int j = 0; j < m2parms.length; ++j) {
                    if (m1parms[j] != m2parms[j]) {
                        parmsDup = false;
                        break;
                    }
                }

                if (parmsDup) {
                    dup = true;
                    if (m2.getDeclaringClass().isAssignableFrom(m1.getDeclaringClass())) {
                        nodups.remove(m2);
                        nodups.add(m1);
                    }
                    break;
                }
            }

            if (!dup) {
                nodups.add(m1);
            }
        }

        return (Method[]) ((Method[]) nodups.toArray(new Method[nodups.size()]));
    }

    protected boolean isEJBIntfMethod(Class ejbIntfClz, Method methodToCheck) {
        boolean isEJBIntfMethod = false;
        Method[] ejbIntfMethods = ejbIntfClz.getMethods();

        for (int i = 0; i < ejbIntfMethods.length; ++i) {
            Method next = ejbIntfMethods[i];
            if (this.methodCompare(methodToCheck, next)) {
                isEJBIntfMethod = true;
                String ejbIntfClzName = ejbIntfClz.getName();
                Class methodToCheckClz = methodToCheck.getDeclaringClass();
                if (!methodToCheckClz.getName().equals(ejbIntfClzName)) {
                    String[] logParams = new String[]{next.toString(), methodToCheck.toString()};
                    _logger.log(Level.WARNING, "ejb.illegal_ejb_interface_override", logParams);
                }
                break;
            }
        }

        return isEJBIntfMethod;
    }

    private boolean methodCompare(Method factoryMethod, Method homeMethod) {
        if (!factoryMethod.getName().equals(homeMethod.getName())) {
            return false;
        } else {
            Class[] factoryParamTypes = factoryMethod.getParameterTypes();
            Class[] beanParamTypes = homeMethod.getParameterTypes();
            if (factoryParamTypes.length != beanParamTypes.length) {
                return false;
            } else {
                for (int i = 0; i < factoryParamTypes.length; ++i) {
                    if (factoryParamTypes[i] != beanParamTypes[i]) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    protected String getUniqueClassName(DeploymentContext context, String origName, String origSuffix, Vector existingClassNames) {
        String newClassName = null;
        boolean foundUniqueName = false;
        int count = 0;

        while (!foundUniqueName) {
            String suffix = origSuffix;
            if (count > 0) {
                suffix = origSuffix + count;
            }

            newClassName = origName + suffix;
            if (!existingClassNames.contains(newClassName)) {
                foundUniqueName = true;
                existingClassNames.add(newClassName);
            } else {
                ++count;
            }
        }

        return newClassName;
    }

    protected String getTxAttribute(EjbDescriptor dd, Method method) {
        if (dd instanceof EjbSessionDescriptor && ((EjbSessionDescriptor) dd).getTransactionType().equals("Bean")) {
            return "TX_BEAN_MANAGED";
        } else {
            String txAttr = null;
            MethodDescriptor mdesc = new MethodDescriptor(method, this.ejbClassSymbol);
            txAttr = "TX_NOT_SUPPORTED";
            /*
            ContainerTransaction ct = dd.getContainerTransactionFor(mdesc);
            if (ct != null) {
                String attr = ct.getTransactionAttribute();
                if (attr.equals("NotSupported")) {
                    txAttr = "TX_NOT_SUPPORTED";
                } else if (attr.equals("Supports")) {
                    txAttr = "TX_SUPPORTS";
                } else if (attr.equals("Required")) {
                    txAttr = "TX_REQUIRED";
                } else if (attr.equals("RequiresNew")) {
                    txAttr = "TX_REQUIRES_NEW";
                } else if (attr.equals("Mandatory")) {
                    txAttr = "TX_MANDATORY";
                } else if (attr.equals("Never")) {
                    txAttr = "TX_NEVER";
                }
            }
             */

            if (txAttr == null) {
                throw new RuntimeException("Transaction Attribute not found for method " + method);
            } else {
                return txAttr;
            }
        }
    }

    protected String getSecurityAttribute(EjbDescriptor dd, Method m) {
        /*
        MethodDescriptor thisMethodDesc = new MethodDescriptor(m, this.ejbClassSymbol);
        Set unchecked = dd.getUncheckedMethodDescriptors();
        if (unchecked != null) {
            Iterator i = unchecked.iterator();

            while (i.hasNext()) {
                MethodDescriptor md = (MethodDescriptor) i.next();
                if (thisMethodDesc.equals(md)) {
                    return "SEC_UNCHECKED";
                }
            }
        }

        Set excluded = dd.getExcludedMethodDescriptors();
        if (excluded != null) {
            Iterator i = excluded.iterator();

            while (i.hasNext()) {
                MethodDescriptor md = (MethodDescriptor) i.next();
                if (thisMethodDesc.equals(md)) {
                    return "SEC_EXCLUDED";
                }
            }
        }
         */

        return "SEC_CHECKED";
    }

}
