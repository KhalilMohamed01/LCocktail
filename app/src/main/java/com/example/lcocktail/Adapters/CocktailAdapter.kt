package com.example.lcocktail.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lcocktail.CocktailDetailActivity
import com.example.lcocktail.Model.Cocktail
import com.example.lcocktail.R
import com.squareup.picasso.Picasso

class CocktailAdapter(
    private val context: Context,
    private val cocktails: List<Cocktail>,
    private val onItemClick: (Cocktail) -> Unit
) : RecyclerView.Adapter<CocktailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.cocktailName)
        val imageView: ImageView = view.findViewById(R.id.cocktailImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("CocktailAdapter", "Inflating layout: item_cocktail_card.xml")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cocktail_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cocktail = cocktails[position]

        // Debugging log to check the cocktail being bound
        Log.d("CocktailAdapter", "Binding cocktail: ${cocktail.strDrink}")

        // Set the name
        holder.nameTextView.text = cocktail.strDrink

        // Load the image
        Picasso.get()
            .load(cocktail.strDrinkThumb)
            .fit()
            .centerCrop()
            .into(holder.imageView)

        // Set up click listener
        holder.itemView.setOnClickListener {
            onItemClick(cocktail)
        }
    }

    override fun getItemCount(): Int = cocktails.size
}
