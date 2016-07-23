package com.bajdcc.cmd.remoteconsole.service;

import com.bajdcc.cmd.remoteconsole.entity.InfoEntity;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiService {

    @GET("cmd/info.php")
    Observable<InfoEntity> getInfo(@Query("ip") String ip);
}
