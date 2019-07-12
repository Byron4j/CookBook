package org.byron4j.cookbook.designpattern.responsibilitychain;

/**
 * @program: cookbook
 * @description: 总经理处理类
 * @author: Byron
 * @create: 2019/07/11 09:49
 */
public class GManagerLeaveHandler extends AbstractLeaveHandler {
    public GManagerLeaveHandler(String name) {
        this.handlerName = name;
    }

    @Override
    protected void handlerRequest(LeaveRequest request) {
        if(request.getLeaveDays() <= this.MAX){
            System.out.println("总经理:" + handlerName + ",已经处理;流程结束。");
            return;
        }

        if(null != this.nextHandler){
            this.nextHandler.handlerRequest(request);
        }else{
            System.out.println("审批拒绝！");
        }
    }
}
