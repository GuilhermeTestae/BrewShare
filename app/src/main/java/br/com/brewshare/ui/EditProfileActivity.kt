package br.com.brewshare.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.brewshare.auth.AuthHelper
import br.com.brewshare.dao.UserDAO
import br.com.brewshare.databinding.ActivityEditProfileBinding
import br.com.brewshare.util.Base64Converter

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val authHelper = AuthHelper()
    private val userDAO = UserDAO()

    private val galeria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) binding.imgPerfil.setImageURI(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        carregarDados()
        setupListeners()
    }

    private fun carregarDados() {
        val email = authHelper.getCurrentEmail() ?: return
        userDAO.getUserByEmail(email) { user ->
            runOnUiThread {
                if (user != null) {
                    binding.edtNomeCompleto.setText(user.nomeCompleto)
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

    private fun setupListeners() {
        binding.btnAlterarFoto.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSalvar.setOnClickListener {
            val email = authHelper.getCurrentEmail() ?: return@setOnClickListener
            val nomeCompleto = binding.edtNomeCompleto.text.toString().trim()

            if (nomeCompleto.isEmpty()) {
                Toast.makeText(this, "Preencha o nome completo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fotoString = try {
                Base64Converter.drawableToString(binding.imgPerfil.drawable)
            } catch (_: Exception) { "" }

            userDAO.updateProfile(email, nomeCompleto, fotoString,
                onSuccess = {
                    runOnUiThread {
                        Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                },
                onFailure = { msg ->
                    runOnUiThread {
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    }
                }
            )
        }

        // Alterar senha
        binding.btnAlterarSenha.setOnClickListener {
            val novaSenha = binding.edtNovaSenha.text.toString().trim()
            if (novaSenha.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authHelper.updatePassword(novaSenha,
                onSuccess = {
                    runOnUiThread { Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show() }
                },
                onFailure = { msg ->
                    runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_LONG).show() }
                }
            )
        }

        binding.btnVoltar.setOnClickListener { finish() }
    }
}
