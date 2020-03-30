package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;


@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    System.out.println("线程开始运行了-----------");
                    String key = RedisKeyUtil.getEventQueueKey();
                    System.out.println("开始阻塞,队列里没有元素");
                    List<String> events = jedisAdapter.brpop(0, key);
                    System.out.println("解除,弹出一个元素");
                    for (String message : events) {
                        if (message.equals(key)) {
                            continue;
                        }
                        System.out.println("我正在消费一个event------------------");
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件-----------------");
                            continue;
                        }
                        for (EventHandler handler : config.get(eventModel.getType())) {
                            handler.doHandler(eventModel);
                        }
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
