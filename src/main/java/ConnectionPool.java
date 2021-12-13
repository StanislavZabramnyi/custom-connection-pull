import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionPool extends PGSimpleDataSource {

    private final int DEFAULT_POOL_SIZE = 10;
    private final Queue<Connection> connectionPool = new ConcurrentLinkedQueue<>();

    public ConnectionPool(String url, String userName, String password) {
        super();
        init(url, userName, password);
    }

    @Override
    public Connection getConnection() {
        return connectionPool.poll();
    }

    @SneakyThrows
    private void init(String url, String userName, String password) {
        this.setURL(url);
        this.setUser(userName);
        this.setPassword(password);

        for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
            try (Connection connection = super.getConnection()) {
                ConnectionProxy proxyConnection = new ConnectionProxy(connection, connectionPool);
                connectionPool.add(proxyConnection);
            }
        }
    }
}
