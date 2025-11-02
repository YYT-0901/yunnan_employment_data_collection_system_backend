package com.yunnancommon.entity.vo;

import lombok.Data;

@Data
public class CreatedAccountVO {
    private String username;
    private String password;

    public CreatedAccountVO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
