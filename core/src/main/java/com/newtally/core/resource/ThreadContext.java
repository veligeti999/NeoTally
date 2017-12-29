package com.newtally.core.resource;

import com.newtally.core.model.User;

public class ThreadContext {

    private ThreadLocal<Long> userLocal = new ThreadLocal<>();

    public long getCurrentUserId() {
        return userLocal.get();
    }

    void setCurrentUserId(Long userId) {
        userLocal.set(userId);
    }


    public void clearContext() {
        userLocal.remove();
    }
}
