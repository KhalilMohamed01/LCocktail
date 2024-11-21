package com.example.lcocktail.ApiService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lcocktail.Model.Cocktail
import com.example.lcocktail.R
import com.squareup.picasso.Picasso

class CocktailAdapter(private val cocktails: List<Cocktail>) :
    RecyclerView.Adapter<CocktailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.cocktailName)
        val imageView: ImageView = view.findViewById(R.id.cocktailImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cocktail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cocktail = cocktails[position]
        holder.nameTextView.text = cocktail.strDrink
        Picasso.get().load(cocktail.strDrinkThumb).into(holder.imageView)
    }

    override fun getItemCount() = cocktails.size
}
