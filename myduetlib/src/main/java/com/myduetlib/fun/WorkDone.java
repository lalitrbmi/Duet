package com.myduetlib.fun;

public interface WorkDone {
    public void onDone(String workName, boolean isComplete);

    public void onError(String errorName, boolean isComplete);
}
