package com.example.lcocktail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lcocktail.Adapters.CocktailAdapter
import com.example.lcocktail.ApiService.CocktailApi
import com.example.lcocktail.ApiService.RetrofitClient
import com.example.lcocktail.Model.Cocktail
import com.example.lcocktail.Model.CocktailResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var alcoholicRecyclerView: RecyclerView
    private lateinit var nonAlcoholicRecyclerView: RecyclerView
    private lateinit var favoritesDatabase: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Database reference
        favoritesDatabase = FirebaseDatabase.getInstance().getReference("favorites")

        // Initialize RecyclerViews
        alcoholicRecyclerView = findViewById(R.id.alcoholicRecyclerView)
        nonAlcoholicRecyclerView = findViewById(R.id.nonAlcoholicRecyclerView)

        // Set up RecyclerViews with horizontal layouts
        alcoholicRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        nonAlcoholicRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Fetch cocktails
        fetchAlcoholicCocktails()
        fetchNonAlcoholicCocktails()

        // Initialize Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true // Stay on the home page
                R.id.nav_favorite -> {
                    // Navigate to FavoriteActivity
                    startActivity(Intent(this, FavoriteActivity::class.java))
                    true
                }
                R.id.nav_logout -> {
                    // Logout logic
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

    }

    private fun fetchAlcoholicCocktails() {
        val api = RetrofitClient.instance.create(CocktailApi::class.java)
        api.getAlcoholicCocktails("Alcoholic").enqueue(object : Callback<CocktailResponse> {
            override fun onResponse(call: Call<CocktailResponse>, response: Response<CocktailResponse>) {
                if (response.isSuccessful) {
                    val cocktails = response.body()?.drinks ?: emptyList()
                    setupRecyclerView(alcoholicRecyclerView, cocktails)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch alcoholic cocktails", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CocktailResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchNonAlcoholicCocktails() {
        val api = RetrofitClient.instance.create(CocktailApi::class.java)
        api.getAlcoholicCocktails("Non_Alcoholic").enqueue(object : Callback<CocktailResponse> {
            override fun onResponse(call: Call<CocktailResponse>, response: Response<CocktailResponse>) {
                if (response.isSuccessful) {
                    val cocktails = response.body()?.drinks ?: emptyList()
                    setupRecyclerView(nonAlcoholicRecyclerView, cocktails)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch non-alcoholic cocktails", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CocktailResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, cocktails: List<Cocktail>) {
        recyclerView.adapter = CocktailAdapter(this, cocktails) { cocktail ->
            openCocktailDetails(cocktail)
        }
    }


    private fun openCocktailDetails(cocktail: Cocktail) {
        Log.d("MainActivity", "Opening details for: ${cocktail.idDrink}")
        val intent = Intent(this, CocktailDetailActivity::class.java)
        intent.putExtra("idDrink", cocktail.idDrink)
        startActivity(intent)
    }
}
