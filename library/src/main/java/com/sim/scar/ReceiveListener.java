package com.sim.scar;

/**
 * Created by sun on 5/12/15.
 */
public interface ReceiveListener {
    /**
     * smsCode maybe equals null or ""
     * @param smsCode the sms code we received
     */
    void onReceived(String smsCode);
}
