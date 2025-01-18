package com.example.lcocktail.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lcocktail.Model.Cocktail
import com.example.lcocktail.R
import com.squareup.picasso.Picasso

class FavoriteCocktailAdapter(
    private val context: Context,
    private val favoriteCocktails: List<Cocktail>,
    private val onItemClick: (Cocktail) -> Unit
) : RecyclerView.Adapter<FavoriteCocktailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cocktailImage: ImageView = view.findViewById(R.id.favoriteCocktailImage)
        val cocktailName: TextView = view.findViewById(R.id.favoriteCocktailName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_favorite_cocktail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cocktail = favoriteCocktails[position]

        holder.cocktailName.text = cocktail.strDrink
        Picasso.get().load(cocktail.strDrinkThumb).into(holder.cocktailImage)

        holder.itemView.setOnClickListener {
            onItemClick(cocktail)
        }
    }

    override fun getItemCount(): Int = favoriteCocktails.size
}
