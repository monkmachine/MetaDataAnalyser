package org.dsc.utilties;

import javafx.scene.control.TextField;

public class Singleton {
    private static Singleton instance = new Singleton();
    public static Singleton getInstance(){
        return instance;
    }

    public void setjDBCUrlformat(String jDBCUrlformat) {
        this.jDBCUrlformat = jDBCUrlformat;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String jdbcUrl;

    public String getjDBCUrlformat() {
        return jDBCUrlformat;
    }

    public String getPassword() {
        return password;
    }

    private String jDBCUrlformat;
    private String password;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

}