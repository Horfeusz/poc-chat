package be.chat.remote;

import java.io.*;
import java.rmi.Remote;

public class RemoteBusinessWrapperBase implements Serializable {

    private String businessInterface_;

    private Remote stub_;

    private transient int hashCode_;

    public RemoteBusinessWrapperBase(Remote stub, String busIntf) {
        this.stub_ = stub;
        this.businessInterface_ = busIntf;
        this.hashCode_ = busIntf.hashCode();
    }

    public int hashCode() {
        return this.hashCode_;
    }

    public boolean equals(Object obj) {
        boolean result = obj == this;
        if (!result && obj != null && obj instanceof RemoteBusinessWrapperBase) {
            RemoteBusinessWrapperBase remoteBWB = (RemoteBusinessWrapperBase)obj;
            boolean hasSameBusinessInterface = remoteBWB.hashCode_ == this.hashCode_ && remoteBWB.businessInterface_.equals(this.businessInterface_);
            if (hasSameBusinessInterface) {
                org.omg.CORBA.Object other = (org.omg.CORBA.Object)remoteBWB.stub_;
                org.omg.CORBA.Object me = (org.omg.CORBA.Object)this.stub_;
                result = me._is_equivalent(other);
            }
        }

        return result;
    }

    public String getBusinessInterfaceName() {
        return this.businessInterface_;
    }

    public Object writeReplace() throws ObjectStreamException {
        return new RemoteBusinessWrapperBase(this.stub_, this.businessInterface_);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(this.businessInterface_);
        oos.writeObject(this.stub_);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        try {
            this.businessInterface_ = (String)ois.readObject();
            this.hashCode_ = this.businessInterface_.hashCode();
            GlassFishEJBUtil.loadGeneratedRemoteBusinessClasses(this.businessInterface_);
            this.stub_ = (Remote)ois.readObject();
        } catch (Exception var4) {
            IOException ioe = new IOException("RemoteBusinessWrapper.readObj  error");
            ioe.initCause(var4);
            throw ioe;
        }
    }

    public Object readResolve() throws ObjectStreamException {
        try {
            return GlassFishEJBUtil.createRemoteBusinessObject(this.businessInterface_, this.stub_);
        } catch (Exception var3) {
            WriteAbortedException wae = new WriteAbortedException("RemoteBusinessWrapper.readResolve error", var3);
            throw wae;
        }
    }
}
