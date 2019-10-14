package com.noble.activity.RAZA_3.Network


object ApiFactory{

    val placeholderApi : PlaceholderApi = RetrofitFactory.retrofit("http://ec2-15-164-104-42.ap-northeast-2.compute.amazonaws.com:8000/")
                                                .create(PlaceholderApi::class.java)
/*
    val tmdbApi : TmdbApi = RetrofitFactory.retrofit(AppConstants.TMDB_BASE_URL)
        .create(TmdbApi::class.java)
        */
}