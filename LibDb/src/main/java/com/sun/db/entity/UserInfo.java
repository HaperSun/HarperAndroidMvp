package com.sun.db.entity;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.converter.PropertyConverter;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Harper
 * @date 2021/11/9
 * note: 用户信息
 * 注：@Entity：将我们的java普通类变为一个能够被greenDAO识别的数据库类型的实体类
 */

@Entity
public class UserInfo {
    /**
     * id：通过这个注解标记的字段必须是Long类型的，这个字段在数据库中表示它就是主键，并且它默认就是自增的
     */
    @Id(autoincrement = true)
    private Long id;

    /**
     * 用户相关
     */
    @NotNull
    private String userName;
    private String passWord;
    private String accessToken;
    private long userId;
    /**
     * 用户状态，这个值是要传非空的，GreenDAO目前不支持注解设置字段dafault值
     */
    @NotNull
    @Convert(converter = StateConverter.class, columnType = Integer.class)
    private LoginState loginState = LoginState.LOGOUT;

    public UserInfo(String userName, String passWord, String accessToken, long userId, LoginState loginState) {
        this.userName = userName;
        this.passWord = passWord;
        this.accessToken = accessToken;
        this.userId = userId;
        this.loginState = loginState;
    }

    @Generated(hash = 258410548)
    public UserInfo(Long id, @NotNull String userName, String passWord, String accessToken,
            long userId, @NotNull LoginState loginState) {
        this.id = id;
        this.userName = userName;
        this.passWord = passWord;
        this.accessToken = accessToken;
        this.userId = userId;
        this.loginState = loginState;
    }

    @Generated(hash = 1279772520)
    public UserInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return this.passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public LoginState getLoginState() {
        return this.loginState;
    }

    public void setLoginState(LoginState loginState) {
        this.loginState = loginState;
    }

    public static class StateConverter implements PropertyConverter<LoginState, Integer> {

        @Override
        public LoginState convertToEntityProperty(Integer databaseValue) {
            if (databaseValue == null) {
                return LoginState.LOGOUT;
            }
            for (LoginState loginState : LoginState.values()) {
                if (loginState.value == databaseValue) {
                    return loginState;
                }
            }
            return LoginState.LOGOUT;
        }

        @Override
        public Integer convertToDatabaseValue(LoginState entityProperty) {
            return entityProperty == null ? LoginState.LOGOUT.value : entityProperty.value;
        }
    }

    public enum LoginState {
        /**
         * 登出
         */
        LOGOUT(1),
        /**
         * 登录
         */
        LOGIN(2);

        final int value;

        LoginState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
