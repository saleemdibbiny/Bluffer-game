package threadPerClient;

import protocol.ServerProtocol;

public interface ServerProtocolFactory<T> {
   ServerProtocol<T> create();
}
