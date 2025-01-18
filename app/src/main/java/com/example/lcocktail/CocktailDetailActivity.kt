package com.example.lcocktail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lcocktail.ApiService.CocktailApi
import com.example.lcocktail.ApiService.RetrofitClient
import com.example.lcocktail.Model.CocktailDetailResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CocktailDetailActivity : AppCompatActivity() {

    private lateinit var favoritesDatabase: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cocktail_detail)

        if (currentUser == null) {
            Toast.makeText(this, "Please log in to use this feature.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = currentUser.uid
        favoritesDatabase = FirebaseDatabase.getInstance().getReference("favorites/$userId")

        val idDrink = intent.getStringExtra("idDrink") ?: return finishWithError()

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val favoriteButton = findViewById<ImageButton>(R.id.favoriteButton)
        val imageView = findViewById<ImageView>(R.id.cocktailImageView)
        val nameTextView = findViewById<TextView>(R.id.cocktailNameTextView)
        val glassTextView = findViewById<TextView>(R.id.cocktailGlassTextView)
        val alcoholicTextView = findViewById<TextView>(R.id.cocktailAlcoholicTextView)
        val ingredientsTable = findViewById<TableLayout>(R.id.ingredientsTable)
        val instructionsTextView = findViewById<TextView>(R.id.cocktailInstructionsTextView)

        backButton.setOnClickListener { finish() }

        // Set up Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navigateToHome()
                    true
                }
                R.id.nav_favorite -> {
                    navigateToFavorites()
                    true
                }
                R.id.nav_logout -> {
                    logoutUser()
                    true
                }
                else -> false
            }
        }

        // Check if cocktail is already in favorites
        checkIfFavorite(idDrink, favoriteButton)

        // Fetch cocktail details
        RetrofitClient.instance.create(CocktailApi::class.java)
            .getCocktailDetails(idDrink)
            .enqueue(object : Callback<CocktailDetailResponse> {
                override fun onResponse(call: Call<CocktailDetailResponse>, response: Response<CocktailDetailResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.drinks?.firstOrNull()?.let { cocktail ->
                            Picasso.get().load(cocktail.strDrinkThumb).fit().centerCrop().into(imageView)
                            nameTextView.text = cocktail.strDrink
                            glassTextView.text = cocktail.strGlass
                            alcoholicTextView.text = cocktail.strAlcoholic
                            instructionsTextView.text = cocktail.strInstructions
                            setupIngredientsTable(cocktail, ingredientsTable)

                            favoriteButton.setOnClickListener {
                                toggleFavorite(idDrink, cocktail, favoriteButton)
                            }
                        } ?: finishWithError()
                    } else finishWithError()
                }

                override fun onFailure(call: Call<CocktailDetailResponse>, t: Throwable) {
                    finishWithError()
                }
            })
    }

    private fun setupIngredientsTable(drink: CocktailDetailResponse.Drink, tableLayout: TableLayout) {
        // Clear existing rows
        tableLayout.removeAllViews()

        // List of ingredients and measures
        val ingredients = listOfNotNull(
            drink.strIngredient1 to drink.strMeasure1,
            drink.strIngredient2 to drink.strMeasure2,
            drink.strIngredient3 to drink.strMeasure3,
            drink.strIngredient4 to drink.strMeasure4,
            drink.strIngredient5 to drink.strMeasure5,
            drink.strIngredient6 to drink.strMeasure6,
            drink.strIngredient7 to drink.strMeasure7,
            drink.strIngredient8 to drink.strMeasure8,
            drink.strIngredient9 to drink.strMeasure9,
            drink.strIngredient10 to drink.strMeasure10
        )

        // Inflate a new row for each ingredient and add it to the table
        for ((ingredient, measure) in ingredients) {
            val row = layoutInflater.inflate(R.layout.ingredient_row, tableLayout, false) as TableRow

            val ingredientNameTextView = row.findViewById<TextView>(R.id.ingredientNameTextView)
            val ingredientMeasureTextView = row.findViewById<TextView>(R.id.ingredientMeasureTextView)

            // Set text for ingredient and measure
            ingredientNameTextView.text = ingredient
            ingredientMeasureTextView.text = measure ?: "" // Handle null measures

            // Add the row to the table layout
            tableLayout.addView(row)
        }
    }


    private fun checkIfFavorite(idDrink: String, favoriteButton: ImageButton) {
        favoritesDatabase.child(idDrink).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    favoriteButton.setColorFilter(Color.RED)
                } else {
                    favoriteButton.setColorFilter(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CocktailDetailActivity, "Failed to check favorites: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleFavorite(idDrink: String, cocktail: CocktailDetailResponse.Drink, favoriteButton: ImageButton) {
        favoritesDatabase.child(idDrink).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    favoritesDatabase.child(idDrink).removeValue()
                        .addOnSuccessListener {
                            favoriteButton.setColorFilter(null)
                            Toast.makeText(this@CocktailDetailActivity, "Removed from favorites", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this@CocktailDetailActivity, "Failed to remove favorite: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    val favoriteCocktail = mapOf(
                        "idDrink" to (cocktail.idDrink ?: ""),
                        "strDrink" to (cocktail.strDrink ?: ""),
                        "strDrinkThumb" to (cocktail.strDrinkThumb ?: "")
                    )
                    favoritesDatabase.child(idDrink).setValue(favoriteCocktail)
                        .addOnSuccessListener {
                            favoriteButton.setColorFilter(Color.RED)
                            Toast.makeText(this@CocktailDetailActivity, "Added to favorites", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this@CocktailDetailActivity, "Failed to add to favorites: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CocktailDetailActivity, "Failed to toggle favorite: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun navigateToFavorites() {
        val intent = Intent(this, FavoriteActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun finishWithError() {
        Toast.makeText(this, "Error loading cocktail details", Toast.LENGTH_SHORT).show()
        finish()
    }
}
