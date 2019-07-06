package org.byron4j.cookbook.mocketio.basic;

import org.junit.Test;
import org.mockito.*;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MockitoAnnoTest extends MockitoAnnotationStartup{

    /**注解得到的mock对象*/
    @Mock
    List<String> mockList;

    @Spy
    List<String> spyList;


    @Mock
    Map<String, String> wordMap;

    @InjectMocks
    MyDictionary myDictionary = new MyDictionary();

    @Test
    public void testRaw(){
        List<String> mock = Mockito.mock(List.class);
        mock.add("one");
        mock.add("one");
        System.out.println(mock.size());
        Mockito.verify(mock, Mockito.times(2)).add("one");

        Mockito.when(mock.size()).thenReturn(100);
        assertEquals(100, mock.size());


    }

    @Test
    public void testAnno(){
        mockList.add("one");
        mockList.add("one");
        Mockito.verify(mockList, Mockito.times(2)).add("one");

        Mockito.when(mockList.size()).thenReturn(100);
        assertEquals(100, mockList.size());


    }


    @Test
    public void testRawSpy(){
        List<String> mock = Mockito.spy(List.class);
        mock.add("one");
        mock.add("one");
        System.out.println(mock.size());
        Mockito.verify(mock, Mockito.times(2)).add("one");

        Mockito.when(mock.size()).thenReturn(100);
        assertEquals(100, mock.size());
    }

    @Test
    public void testSpy(){
        spyList.add("one");
        spyList.add("one");
        Mockito.verify(spyList, Mockito.times(2)).add("one");

        Mockito.when(spyList.size()).thenReturn(100);
        assertEquals(100, spyList.size());


    }


    @Test
    public void testArgumentCaptor(){
        List mockList = Mockito.mock(List.class);

        // 创建一个参数捕获器
        ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);


        mockList.add("one");

        // capture 方法必须在一个验证中。在一个验证中使用捕获器捕获方法add的参数
        Mockito.verify(mockList).add(arg.capture());

        // 获取参数捕获器捕获到的参数
        assertEquals("one", arg.getValue());
    }


    @Test
    public void testInjectMocks(){
        Mockito.when(wordMap.get("aWord")).thenReturn("aMeaning");

        assertEquals("aMeaning", myDictionary.getMeaning("aWord"));

        System.out.println(myDictionary.getMeaning("aWord"));
    }

    class MyDictionary{
        Map<String, String> wordMap;

        public String getMeaning(String word){
            return wordMap.get(word);
        }
    }
}
