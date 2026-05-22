package org.simpmc.simppay.http;

import io.jooby.Jooby;
import io.jooby.hikari.HikariModule;
import io.jooby.netty.NettyServer;
import io.jooby.redis.RedisModule;

import javax.sql.DataSource;

public class JoobyServer extends Jooby {
    {
        install(new HikariModule());
        install(new RedisModule());
        install(new NettyServer());
        get("/", ctx -> {
            DataSource maindb = require(DataSource.class);
            return "123";
        });
    }

    public static void enable(String[] args) {
        runApp(args, JoobyServer::new);
    }
}
