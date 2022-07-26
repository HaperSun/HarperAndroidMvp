package com.sun.base.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Harper
 * @date 2021/11/9
 * note: 账户信息
 * 注：@Entity：将我们的java普通类变为一个能够被greenDAO识别的数据库类型的实体类
 */
@Entity
public class AccountInfo {

    /**
     * id：通过这个注解标记的字段必须是Long类型的，这个字段在数据库中表示它就是主键，并且它默认就是自增的
     */
    @Id(autoincrement = true)
    private Long id;

    private String name;
    private String accountId;
    private String number;
    @Generated(hash = 38418771)
    public AccountInfo(Long id, String name, String accountId, String number) {
        this.id = id;
        this.name = name;
        this.accountId = accountId;
        this.number = number;
    }
    @Generated(hash = 1230968834)
    public AccountInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAccountId() {
        return this.accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
}
