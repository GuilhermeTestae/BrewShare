package br.com.brewshare.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.brewshare.auth.AuthHelper
import br.com.brewshare.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authHelper = AuthHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnCriarConta.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val senha = binding.edtSenha.text.toString().trim()
            val confirmaSenha = binding.edtConfirmaSenha.text.toString().trim()

            when {
                email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty() ->
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                senha != confirmaSenha ->
                    Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                senha.length < 6 ->
                    Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                else -> {
                    authHelper.registerUser(email, senha,
                        onSuccess = {
                            startActivity(Intent(this, ProfileActivity::class.java))
                            finish()
                        },
                        onFailure = { msg ->
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }

        binding.btnVoltar.setOnClickListener { finish() }
    }
}
