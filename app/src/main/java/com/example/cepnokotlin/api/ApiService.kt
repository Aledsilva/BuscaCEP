package com.example.cepnokotlin.api

import com.example.cepnokotlin.model.CEPResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("{cep}/json/")
    open fun recuperarCEP(@Path("cep") cep: String?): Call<CEPResponse>

}