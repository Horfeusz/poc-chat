package be.chat.remote;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public class RemoteAsyncResult implements Serializable {

    public Long asyncID;

    public Object resultValue;

    public Throwable resultException;
}
