/**
 * 
 */
package org.syl.tryredis;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author megapro
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext ac =  new ClassPathXmlApplicationContext("classpath:/data-source.xml");
        RedisClientTemplate redisClient = (RedisClientTemplate)ac.getBean("redisClientTemplate");
        redisClient.set("a", "abc");
        System.out.println(redisClient.get("a"));
	}

}
