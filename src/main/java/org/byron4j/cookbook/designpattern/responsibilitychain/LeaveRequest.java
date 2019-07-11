package org.byron4j.cookbook.designpattern.responsibilitychain;

import lombok.*;

/**
 * @program: cookbook
 * @description: 请假请求类
 * @author: Byron
 * @create: 2019/07/11 09:44
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {
    /**天数*/
    private int leaveDays;

    /**姓名*/
    private String name;
}
