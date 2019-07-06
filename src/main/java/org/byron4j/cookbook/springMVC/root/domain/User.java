package org.byron4j.cookbook.springMVC.root.domain;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class User implements Serializable {
    private Long id;
    private String email;
    private String nickName;
    private String passWord;
    private String regTime;
    private String userName;


}
