package com.bajdcc.cmd.remoteconsole.event;

public class ApiEvent {

    private final String host;

    public ApiEvent(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }
}
