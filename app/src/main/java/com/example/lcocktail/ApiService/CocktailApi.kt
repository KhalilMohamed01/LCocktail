package com.example.lcocktail.ApiService

import com.example.lcocktail.Model.CocktailDetailResponse
import com.example.lcocktail.Model.CocktailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApi {

    @GET("filter.php")
    fun getAlcoholicCocktails(@Query("a") alcoholicType: String): Call<CocktailResponse>

    @GET("search.php")
    fun searchCocktailsByName(@Query("s") query: String): Call<CocktailResponse>

    @GET("api/json/v1/1/filter.php?c=Cocktail")
    fun getCocktails(): Call<CocktailResponse>

    @GET("lookup.php")
    fun getCocktailDetails(@Query("i") id: String): Call<CocktailDetailResponse>

}
