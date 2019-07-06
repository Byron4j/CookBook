package org.byron4j.cookbook.designpattern.objectpool;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * 对象池
 */
public abstract  class ObjectPool<T> {

    /**过期时间*/
    private int expirationTime;

    /**unlocked 是可用对象列表*/
    private Hashtable<T, Long> locked, unlocked;

    /**初始化默认超时时间、初始化可用对象列表、已锁定列表*/
    public ObjectPool(){
        expirationTime = 30000;// 30 seconds
        locked = new Hashtable<>();
        unlocked = new Hashtable<>();
    }

    /**创建一个对象*/
    protected abstract T create();

    /**校验对象是否还是有效的*/
    public abstract  boolean validate(T o);

    /**超时移除对象*/
    public abstract void expire(T o);

    /**
     * 从对象池中获取一个可用对象
     *
     * */
    public synchronized  T checkout(){
        System.out.println("准备\"检出\"可用连接对象");
        // 新的起始时间，当前有使用且有效，则延长其有效时间
        long now = System.currentTimeMillis();
        T t;

        if( unlocked.size() > 0 ){
            Enumeration<T> e = unlocked.keys();
            while( e.hasMoreElements() ){
                t = e.nextElement();
                if( (now - unlocked.get(t)) > expirationTime ){
                    // 存在时间过长了，移除对象
                    unlocked.remove(t);
                    // 且关闭连接
                    expire(t);
                    t = null;
                }else{
                    if( validate(t) ){
                        // 可用，则将其从可用列表中移除
                        unlocked.remove(t);
                        // 并且放入锁定列表中，表示该对象已经被占用了
                        locked.put(t, now);
                        // 返回该可用对象
                        return (t);
                    }else{
                        // 校验不通过，则从可用列表中移除
                        unlocked.remove(t);
                        // 且关闭连接
                        expire(t);
                        t = null;
                    }
                }
            }
        }

        // 没有找到可用的对象，则创建新的对象
        t = create();
        // 将新对象放入锁定列表中
        locked.put(t, now);
        // 返回新对象
        return (t);
    }


    /**
     * 归还对象
     * @param t
     */
    public synchronized void checkin(T t){
        System.out.println("准备\"归还\"当前连接对象");
        // 将对象从锁定列表中移除
        locked.remove(t);
        // 将对象加入到可用列表中，时间从当前开始
        unlocked.put(t, System.currentTimeMillis());
    }


}
