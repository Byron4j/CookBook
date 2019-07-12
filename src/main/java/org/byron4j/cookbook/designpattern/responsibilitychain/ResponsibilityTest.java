package org.byron4j.cookbook.designpattern.responsibilitychain;

/**
 * @program: cookbook
 * @description: 责任链模式测试案例
 * @author: Byron
 * @create: 2019/07/11 09:59
 */
public class ResponsibilityTest {
    public static void main(String[] args) {
        LeaveRequest request = LeaveRequest.builder().leaveDays(3).name("小明").build();


        AbstractLeaveHandler directLeaderLeaveHandler = new DirectLeaderLeaveHandler("县令");
        DeptManagerLeaveHandler deptManagerLeaveHandler = new DeptManagerLeaveHandler("知府");
        GManagerLeaveHandler gManagerLeaveHandler = new GManagerLeaveHandler("京兆尹");

        directLeaderLeaveHandler.setNextHandler(deptManagerLeaveHandler);
        deptManagerLeaveHandler.setNextHandler(gManagerLeaveHandler);

        directLeaderLeaveHandler.handlerRequest(request);


    }
}
