package com.github.pim.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>启动类</p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/4/28 10:51
 * @since 1.0
 */
@SpringBootApplication
public class NettyServerStarter {

    public static void main(String[] args) {
        SpringApplication.run(NettyServerStarter.class, args);
    }


}
