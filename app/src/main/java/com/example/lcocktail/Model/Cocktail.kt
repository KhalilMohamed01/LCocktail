package com.example.lcocktail.Model

data class Cocktail(
    val idDrink: String,
    val strDrink: String,
    val strDrinkThumb: String
)

data class CocktailResponse(
    val drinks: List<Cocktail>
)