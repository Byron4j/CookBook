package org.byron4j.cookbook.mocketio.basic;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MockitoTest {

    @Test
    public void whenNotUseMockAnnotation_thenCorrect() {
        // 创建一个mock出来的ArrayList对象
        List mockList = Mockito.mock(ArrayList.class);

        // 调用mock对象的方法
        mockList.add("one");
        //mockList.add("one");

        // 获取mock对象的实际方法，获取size，结果为0
        System.out.println("mockList.size(): " + mockList.size());
        // toString方法
        System.out.println("mockList's toString is: " + mockList);

        // 验证mock对象mockList的add方法是否被调用了一次
        Mockito.verify(mockList, Mockito.times(1)).add("one");
        Mockito.verify(mockList).add("one");
        assertEquals(0, mockList.size());

        // 当调用mockList.size()的时候，总是返回100
        Mockito.when(mockList.size()).thenReturn(100);

        assertEquals(100, mockList.size());
    }

    @Test
    public void whenTest() {
        List mock = Mockito.mock(List.class);
        Mockito.when(mock.size()).thenReturn(-1);
        System.out.println("mock.size():" + mock.size());



        // 连续存根
        Mockito.when(mock.size()).thenReturn(1).thenReturn(2).thenReturn(3);
        for(int i=1; i <= 5; i++){
            System.out.println("=====连续存根方式1：=====： " + mock.size());
        }

        Mockito.when(mock.size()).thenReturn(1,2, 3);
        for(int i=1; i <= 5; i++){
            System.out.println("#####连续存根方式2：#####： " + mock.size());
        }

        // 模拟异常
        Mockito.when(mock.size()).thenThrow(new RuntimeException(), new NullPointerException());
        try{
            mock.size();
        }catch (Exception e){
            System.out.println(e);
        }
        try{
            mock.size();
        }catch (Exception e){
            System.out.println(e);
        }

    }
}
