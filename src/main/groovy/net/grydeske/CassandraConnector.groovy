package net.grydeske

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Cluster.Builder
import com.datastax.driver.core.Session

class CassandraConnector {

    Cluster cluster

    Session session

    void connect(String node, Integer port) {
        Builder b = Cluster.builder().addContactPoint(node)
        if (port) {
            b.withPort(port)
        }
        cluster = b.build()

        session = cluster.connect()
    }

    void close() {
        session.close()
        cluster.close()
    }
}
