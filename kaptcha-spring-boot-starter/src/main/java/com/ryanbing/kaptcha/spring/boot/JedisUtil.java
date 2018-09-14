package com.ryanbing.kaptcha.spring.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class JedisUtil {
    private static Logger logger = LoggerFactory.getLogger(JedisUtil.class);

    private static final int DEFAULT_EXPIRE_TIME = 60;
    private static String address;
    private static ShardedJedisPool shardedJedisPool;
    private static ReentrantLock INSTANCE_INIT_LOCL = new ReentrantLock(false);

    public static void init(String address) {
        JedisUtil.address = address;
    }


    private static ShardedJedis getInstance() {
        if (shardedJedisPool == null) {
            try {
                if (INSTANCE_INIT_LOCL.tryLock(2, TimeUnit.SECONDS)) {

                    try {

                        if (shardedJedisPool == null) {
                            JedisPoolConfig config = new JedisPoolConfig();
                            config.setMaxTotal(200);
                            config.setMaxIdle(50);
                            config.setMinIdle(10);
                            config.setMaxWaitMillis(10000);
                            config.setTestOnBorrow(true);
                            config.setTestOnReturn(true);
                            config.setTestWhileIdle(true);
                            config.setTimeBetweenEvictionRunsMillis(30000);
                            config.setNumTestsPerEvictionRun(10);
                            config.setMinEvictableIdleTimeMillis(60000);

                            List<JedisShardInfo> jedisShardInfos = new LinkedList<JedisShardInfo>();

                            String[] addressArr = address.split(",");
                            for (String anAddressArr : addressArr) {
                                String[] addressInfo = anAddressArr.split(":");
                                String host = addressInfo[0];
                                int port = Integer.valueOf(addressInfo[1]);
                                JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port, 10000);
                                jedisShardInfos.add(jedisShardInfo);
                            }
                            shardedJedisPool = new ShardedJedisPool(config, jedisShardInfos);
                        }

                    } finally {
                        INSTANCE_INIT_LOCL.unlock();
                    }
                }

            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

        if (shardedJedisPool == null) {
            throw new NullPointerException(" JedisUtil ShardedJedisPool is null.");
        }

        return shardedJedisPool.getResource();
    }

    // ------------------------ serialize and unserialize ------------------------

    private static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            logger.error("{}", e);
        } finally {
            try {
                oos.close();
                baos.close();
            } catch (IOException e) {
                logger.error("{}", e);
            }
        }
        return null;
    }

    private static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            logger.error("{}", e);
        } finally {
            try {
                bais.close();
            } catch (IOException e) {
                logger.error("{}", e);
            }
        }
        return null;
    }

    // ------------------------ jedis util ------------------------

    public static String setStringValue(String key, String value, int seconds) {
        String result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.setex(key, seconds, value);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            client.close();
        }
        return result;
    }

    public static String setStringValue(String key, String value) {
        return setStringValue(key, value, DEFAULT_EXPIRE_TIME);
    }


    public static String setObjectValue(String key, Object obj, int seconds) {
        String result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.setex(key.getBytes(), seconds, serialize(obj));
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            client.close();
        }
        return result;
    }


    public static String setObjectValue(String key, Object obj) {
        return setObjectValue(key, obj, DEFAULT_EXPIRE_TIME);
    }


    public static String getStringValue(String key) {
        String value = null;
        ShardedJedis client = getInstance();
        try {
            value = client.get(key);
        } catch (Exception e) {
            logger.info("", e);
        } finally {
            client.close();
        }
        return value;
    }


    public static Object getObjectValue(String key) {
        Object obj = null;
        ShardedJedis client = getInstance();
        try {
            byte[] bytes = client.get(key.getBytes());
            if (bytes != null && bytes.length > 0) {
                obj = unserialize(bytes);
            }
        } catch (Exception e) {
            logger.info("", e);
        } finally {
            client.close();
        }
        return obj;
    }

    public static Long del(String key) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.del(key);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            client.close();
        }
        return result;
    }

    public static Long incrBy(String key, int i) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.incrBy(key, i);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            client.close();
        }
        return result;
    }

    public static Long decrBy(String key, int i) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.incrBy(key, -i);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            client.close();
        }
        return result;
    }


    public static boolean exists(String key) {
        Boolean result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.exists(key);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            client.close();
        }
        return result;
    }


    public static long expire(String key, int seconds) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.expire(key, seconds);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            client.close();
        }
        return result;
    }


    public static long expireAt(String key, long unixTime) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.expireAt(key, unixTime);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            client.close();
        }
        return result;
    }


}
