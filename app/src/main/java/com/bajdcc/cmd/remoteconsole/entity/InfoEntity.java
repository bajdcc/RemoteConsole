package com.bajdcc.cmd.remoteconsole.entity;

public class InfoEntity {

    private String server;
    private String ip;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Info{" +
                "server='" + server + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
