package com.muc;

public interface IUserStatusListener {
    void online(String userName);
    void offline(String userName);
}
