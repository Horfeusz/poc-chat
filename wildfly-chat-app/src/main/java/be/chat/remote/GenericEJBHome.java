package be.chat.remote;

import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;

public interface GenericEJBHome extends EJBHome {

    RemoteAsyncResult get(long var1) throws RemoteException;

    RemoteAsyncResult getWithTimeout(long var1, long var3, String var5) throws RemoteException, TimeoutException;

    RemoteAsyncResult cancel(long var1) throws RemoteException;

    RemoteAsyncResult isDone(long var1) throws RemoteException;
}
