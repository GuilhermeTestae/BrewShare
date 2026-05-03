package br.com.brewshare.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.brewshare.auth.AuthHelper
import br.com.brewshare.dao.UserDAO
import br.com.brewshare.databinding.ActivityProfileBinding
import br.com.brewshare.model.User
import br.com.brewshare.util.Base64Converter

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val authHelper = AuthHelper()
    private val userDAO = UserDAO()

    private val galeria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.imgPerfil.setImageURI(uri)
        } else {
            Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnAlterarFoto.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSalvar.setOnClickListener {
            val email = authHelper.getCurrentEmail() ?: return@setOnClickListener
            val username = binding.edtUsername.text.toString().trim()
            val nomeCompleto = binding.edtNomeCompleto.text.toString().trim()

            if (username.isEmpty() || nomeCompleto.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fotoString = try {
                Base64Converter.drawableToString(binding.imgPerfil.drawable)
            } catch (_: Exception) { "" }

            val user = User(
                email = email,
                username = username,
                nomeCompleto = nomeCompleto,
                fotoPerfil = fotoString
            )

            userDAO.saveUser(user,
                onSuccess = {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                },
                onFailure = { msg ->
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}
