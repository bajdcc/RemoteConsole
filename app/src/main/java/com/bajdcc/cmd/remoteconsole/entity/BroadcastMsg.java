package com.bajdcc.cmd.remoteconsole.entity;

import com.baoyz.pg.Parcelable;

@Parcelable
public class BroadcastMsg {

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
