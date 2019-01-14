package org.byron4j.cookbook.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * 演示 Netty 传输对象 ByteBuf 的方法使用示例
 *   <pre>
 *        +-------------------+------------------+------------------+
 *        | discardable bytes |  readable bytes  |  writable bytes  |
 *        |                   |     (CONTENT)    |                  |
 *        +-------------------+------------------+------------------+
 *        |                   |                  |                  |
 *        0      <=      readerIndex   <=   writerIndex    <=    capacity
 *   </pre>
 *
 *  ByteBuf 数据结构分析：
 *      共分为四部分：
 *              <ul>
 *                  <li>废弃字节部分</li>
 *                  <li>可读字节部分</li>
 *                  <li>可写字节部分</li>
 *                  <li>可扩容部分</li>
 *              </ul>
 *     <b><font color=red>容量 capacity</font></b> = 废弃字节 + 可读字节 + 可写字节
 *     <br/>
 *     <b><font color=red>最大容量 maxcapacity</font></b> = 容量 capacity + 可扩容字节
 *     <br/>
 *     <b><font color=red>可读区间</font></b> 指 readerIndex 到 writerIndex 区间
 *     <br/>
 *     <b><font color=red>可写区间</font></b> 指 writerIndex 到 capacity 区间
 *     <br/>
 *     <b><font color=red>可扩容区间</font></b> 指的是 capacity 到 maxcapacity 区间
 *
 *
 */
public class ByteBufTest {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);

        print("allocate ByteBuf(9, 100)", buffer);

        // write 方法改变写指针，写完之后写指针未到 capacity 的时候，buffer 仍然可写
        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        print("writeBytes(1,2,3,4)", buffer);

        // write 方法改变写指针，写完之后写指针未到 capacity 的时候，buffer 仍然可写, 写完 int 类型之后，写指针增加4
        buffer.writeInt(12);
        print("writeInt(12)", buffer);

        // write 方法改变写指针, 写完之后写指针等于 capacity 的时候，buffer 不可写
        buffer.writeBytes(new byte[]{5});
        print("writeBytes(5)", buffer);

        // write 方法改变写指针，写的时候发现 buffer 不可写则开始扩容，扩容之后 capacity 随即改变
        buffer.writeBytes(new byte[]{6});
        print("writeBytes(6)", buffer);

        // get 方法不改变读写指针
        System.out.println("getByte(3) return: " + buffer.getByte(3));
        System.out.println("getShort(3) return: " + buffer.getShort(3));
        System.out.println("getInt(3) return: " + buffer.getInt(3));
        print("getByte()", buffer);


        // set 方法不改变读写指针
        buffer.setByte(buffer.readableBytes() + 1, 0);
        print("setByte()", buffer);

        // read 方法改变读指针
        byte[] dst = new byte[buffer.readableBytes()];
        buffer.readBytes(dst);
        print("readBytes(" + dst.length + ")", buffer);

    }

    private static void print(String action, ByteBuf buffer) {
        System.out.println("after ===========" + action + "============");
        System.out.println("capacity(): " + buffer.capacity());
        System.out.println("maxCapacity(): " + buffer.maxCapacity());
        System.out.println("readerIndex(): " + buffer.readerIndex());
        System.out.println("readableBytes(): " + buffer.readableBytes());
        System.out.println("isReadable(): " + buffer.isReadable());
        System.out.println("writerIndex(): " + buffer.writerIndex());
        System.out.println("writableBytes(): " + buffer.writableBytes());
        System.out.println("isWritable(): " + buffer.isWritable());
        System.out.println("maxWritableBytes(): " + buffer.maxWritableBytes());
        System.out.println();
    }
}
