package br.com.brewshare.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.brewshare.adapter.PostAdapter
import br.com.brewshare.auth.AuthHelper
import br.com.brewshare.dao.PostDAO
import br.com.brewshare.dao.UserDAO
import br.com.brewshare.databinding.ActivityHomeBinding
import br.com.brewshare.util.Base64Converter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val authHelper = AuthHelper()
    private val userDAO = UserDAO()
    private val postDAO = PostDAO()
    private val adapter = PostAdapter()

    private var lastTimestamp: Long? = null
    private var isLoading = false
    private var isSearchMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadUserData()
        loadFeed(reset = true)
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.recyclerPosts.layoutManager = LinearLayoutManager(this)
        binding.recyclerPosts.adapter = adapter

        // Scroll infinito: ao chegar no final, carrega mais posts
        binding.recyclerPosts.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val lm = rv.layoutManager as LinearLayoutManager
                if (!isLoading && !isSearchMode &&
                    lm.findLastVisibleItemPosition() >= adapter.itemCount - 2) {
                    loadFeed(reset = false)
                }
            }
        })
    }

    private fun loadUserData() {
        val email = authHelper.getCurrentEmail() ?: return
        userDAO.getUserByEmail(email) { user ->
            runOnUiThread {
                if (user != null) {
                    binding.tvUsername.text = "@${user.username}"
                    binding.tvNomeCompleto.text = user.nomeCompleto
                    if (user.fotoPerfil.isNotEmpty()) {
                        try {
                            binding.imgPerfil.setImageBitmap(
                                Base64Converter.stringToBitmap(user.fotoPerfil)
                            )
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }

    private fun loadFeed(reset: Boolean) {
        if (isLoading) return
        isLoading = true

        if (reset) {
            lastTimestamp = null
        }

        postDAO.getPostsPaginated(
            limit = 5,
            startAfterTimestamp = lastTimestamp,
            onSuccess = { posts ->
                runOnUiThread {
                    isLoading = false
                    if (posts.isNotEmpty()) {
                        lastTimestamp = posts.last().timestamp
                        if (reset) adapter.setPosts(posts)
                        else adapter.appendPosts(posts)
                    }
                }
            },
            onFailure = { msg ->
                runOnUiThread {
                    isLoading = false
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun setupListeners() {
        binding.btnNovoPost.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }

        binding.btnBuscar.setOnClickListener {
            val cidade = binding.edtBusca.text.toString().trim()
            if (cidade.isEmpty()) {
                isSearchMode = false
                loadFeed(reset = true)
                return@setOnClickListener
            }
            isSearchMode = true
            postDAO.getPostsByCity(cidade,
                onSuccess = { posts ->
                    runOnUiThread { adapter.setPosts(posts) }
                },
                onFailure = { msg ->
                    runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
                }
            )
        }

        binding.btnEditarPerfil.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        binding.btnSair.setOnClickListener {
            authHelper.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
        if (!isSearchMode) loadFeed(reset = true)
    }
}
