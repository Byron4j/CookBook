package org.byron4j.cookbook;




import sun.misc.Regexp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCase {
    static String regStr = "<[a-z]{1}>([\\u4E00-\\u9FFF]|\\w|\\W){2}</[a-z]{1}>";

    static String regStrSig = "<[a-z]{1}>|</[a-z]{1}>";


    public static void main(String[] args){

        Pattern pattern = Pattern.compile(regStr);
        String str = "总<b>公司</b>的<b>本</b>25名，分（子）<b>公司</b>s<b>本bu</b>86名<b>hello</b>你好<b>sa</b>";

        Matcher match = pattern.matcher(str);
        // 保存匹配到的字符串去除标签后的内容
        List<String> list = new ArrayList<>();
        boolean headFlag = false;
        while( match.find() ){
            System.out.println("==================");
            if( !headFlag && match.start() == 0){
                // 说明首位就匹配到了
                headFlag = true;
            }
            String string = match.group();
            System.out.println("匹配项输出：" + string);
            StringBuffer stringBuffer = new StringBuffer();
            for(String ele :  Pattern.compile(regStrSig).split(string)){
                stringBuffer.append(ele);
            }
            System.out.println("匹配项去除标签：" + stringBuffer.toString());
            list.add(stringBuffer.toString());


        }


        System.out.println("=============");
        for (String ele : list){
            System.out.println("需要插入的内容：" + ele);
        }

        System.out.println("=============");
        // 匹配后剩下的
        String[] matchArr = pattern.split(str);
        for(String ele : matchArr){
            System.out.println(ele);
        }

        List<String> result = new ArrayList<>();
        if(headFlag){
            // 第一个字符就匹配到了
            for( int i = 0; i < list.size() ; i++ ){
                result.add(list.get(i));
                result.add(matchArr[i]);
            }
        }else{
            // 不是第一个字符匹配到的
            for( int i = 0; i < list.size() ; i++ ){
                result.add(matchArr[i]);
                result.add(list.get(i));
            }
        }

        System.out.println("+++++++++++++++++++++++++");
        System.out.println(result);
        StringBuffer sb = new StringBuffer();
        for(String ele : result){
            sb.append(ele);
        }

        System.out.println("----------------------------------------------------------");
        System.out.println("原始：" + str);
        System.out.println("结果：" + sb.toString());
        System.out.println("----------------------------------------------------------");
    }
}
