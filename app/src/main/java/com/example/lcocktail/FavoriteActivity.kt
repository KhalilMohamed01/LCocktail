package com.example.lcocktail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lcocktail.Adapters.FavoriteCocktailAdapter
import com.example.lcocktail.Model.Cocktail
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavoriteActivity : AppCompatActivity() {

    private lateinit var favoritesDatabase: DatabaseReference
    private val favoriteCocktails = mutableListOf<Cocktail>()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        if (currentUser == null) {
            Log.e("FavoriteActivity", "User not logged in.")
            return
        }

        // Firebase Database Reference
        val userId = currentUser.uid
        favoritesDatabase = FirebaseDatabase.getInstance().getReference("favorites/$userId")

        // RecyclerView Setup
        val favoriteRecyclerView = findViewById<RecyclerView>(R.id.favoriteRecyclerView)
        favoriteRecyclerView.layoutManager = LinearLayoutManager(this)

        val emptyStateTextView = findViewById<TextView>(R.id.emptyStateTextView)

        // Fetch and display favorite cocktails
        favoritesDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favoriteCocktails.clear()
                for (cocktailSnapshot in snapshot.children) {
                    val cocktail = cocktailSnapshot.getValue(Cocktail::class.java)
                    if (cocktail != null) {
                        favoriteCocktails.add(cocktail)
                    }
                }

                if (favoriteCocktails.isEmpty()) {
                    favoriteRecyclerView.visibility = View.GONE
                    emptyStateTextView.visibility = View.VISIBLE
                } else {
                    favoriteRecyclerView.visibility = View.VISIBLE
                    emptyStateTextView.visibility = View.GONE
                }

                favoriteRecyclerView.adapter = FavoriteCocktailAdapter(
                    this@FavoriteActivity,
                    favoriteCocktails
                ) { cocktail ->
                    openCocktailDetails(cocktail)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FavoriteActivity", "Failed to fetch favorites: ${error.message}")
            }
        })

        // Set up Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_favorite
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navigateToHome()
                    true
                }
                R.id.nav_favorite -> true // Stay on this page
                R.id.nav_logout -> {
                    logoutUser()
                    true
                }
                else -> false
            }
        }
    }

    private fun openCocktailDetails(cocktail: Cocktail) {
        val intent = Intent(this, CocktailDetailActivity::class.java)
        intent.putExtra("idDrink", cocktail.idDrink)
        startActivity(intent)
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
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
}
