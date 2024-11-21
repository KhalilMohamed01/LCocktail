package com.example.lcocktail.ApiService
import com.example.lcocktail.Model.CocktailResponse
import retrofit2.Call
import retrofit2.http.GET

interface CocktailApi {
    @GET("api/json/v1/1/filter.php?c=Cocktail")
    fun getCocktails(): Call<CocktailResponse>
}
