/**
 * 
 */
package org.syl.tryredis.subpub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author megapro
 *
 */
public class MainTestSubPub {

	public static final String CHANNEL_NAME = "MyChannel";
    //我这里的Redis是一个集群，192.168.56.101和192.168.56.102都可以使用
    //public static final String REDIS_HOST = "192.168.56.101";
    //public static final int REDIS_PORT = 7000;
	
	//我实验时redis是本地单机
	public static final String REDIS_HOST = "127.0.0.1";
	public static final int REDIS_PORT = 6379;
   
	
    private final static Logger LOGGER = LoggerFactory.getLogger(MainTestSubPub.class);
    private final static JedisPoolConfig POOL_CONFIG = new JedisPoolConfig();
    private final static JedisPool JEDIS_POOL =  new JedisPool(POOL_CONFIG, REDIS_HOST, REDIS_PORT, 0);
    
    public static void main(String[] args) throws Exception {
        final Jedis subscriberJedis = JEDIS_POOL.getResource();
        final Jedis publisherJedis = JEDIS_POOL.getResource();
        final Subscriber subscriber = new Subscriber();
        //订阅线程：接收消息
        new Thread(new Runnable() {
            public void run() {
                try {
                    LOGGER.info("Subscribing to \"MyChannel\". This thread will be blocked.");
                    //使用subscriber订阅CHANNEL_NAME上的消息，这一句之后，线程进入订阅模式，阻塞。
                    subscriberJedis.subscribe(subscriber, CHANNEL_NAME);
                    
                    //当unsubscribe()方法被调用时，才执行以下代码
                    LOGGER.info("Subscription ended.");
                } catch (Exception e) {
                    LOGGER.error("Subscribing failed.", e);
                }
            }
        }).start();
        
        //主线程：发布消息到CHANNEL_NAME频道上
        new Publisher(publisherJedis, CHANNEL_NAME).startPublish();
        publisherJedis.close();
        
        //Unsubscribe
        subscriber.unsubscribe();
        subscriberJedis.close();
    }
}
