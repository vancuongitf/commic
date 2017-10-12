package com.example.cuongcaov.comicbook.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
class RetrofitClient {
    companion object {
        private var retrofit: Retrofit? = null
        private fun getClient(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder().baseUrl("http://freestory.000webhostapp.com/api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit!!
        }

        fun getAPIService(): APIService {
            return getClient().create(APIService::class.java)
        }
    }

}