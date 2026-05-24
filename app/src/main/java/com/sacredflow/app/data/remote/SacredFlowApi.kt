package com.sacredflow.app.data.remote

import com.sacredflow.app.data.remote.dto.ApiGenerateRequest
import com.sacredflow.app.data.remote.dto.ApiGenerateResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SacredFlowApi {

    @POST("v1/generate")
    suspend fun generate(@Body body: ApiGenerateRequest): ApiGenerateResponse
}
