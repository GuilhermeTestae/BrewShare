package br.com.brewshare.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.com.brewshare.auth.AuthHelper
import br.com.brewshare.dao.PostDAO
import br.com.brewshare.dao.UserDAO
import br.com.brewshare.databinding.ActivityCreatePostBinding
import br.com.brewshare.location.LocalizacaoHelper
import br.com.brewshare.model.Post
import br.com.brewshare.util.Base64Converter

class CreatePostActivity : AppCompatActivity(), LocalizacaoHelper.Callback {

    private lateinit var binding: ActivityCreatePostBinding
    private val authHelper = AuthHelper()
    private val userDAO = UserDAO()
    private val postDAO = PostDAO()

    private var cidadeAtual: String = ""
    private var imagemBase64: String = ""

    private val LOCATION_PERMISSION_CODE = 1001

    private val galeria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.imgPreview.setImageURI(uri)
            // Converter para Base64
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                imagemBase64 = Base64Converter.bitmapToString(bitmap)
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
        solicitarLocalizacao()
    }

    private fun setupListeners() {
        binding.btnSelecionarFoto.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnObterLocalizacao.setOnClickListener {
            solicitarLocalizacao()
        }

        binding.btnPublicar.setOnClickListener {
            publicarPost()
        }

        binding.btnVoltar.setOnClickListener { finish() }
    }

    private fun solicitarLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        } else {
            LocalizacaoHelper(applicationContext).obterCidadeAtual(this)
        }
    }

    override fun onCidadeRecebida(cidade: String) {
        runOnUiThread {
            cidadeAtual = cidade
            binding.tvCidade.text = "📍 $cidade"
        }
    }

    override fun onErro(mensagem: String) {
        runOnUiThread {
            binding.tvCidade.text = "Localização não disponível"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            solicitarLocalizacao()
        }
    }

    private fun publicarPost() {
        val descricao = binding.edtDescricao.text.toString().trim()
        val email = authHelper.getCurrentEmail() ?: return

        if (descricao.isEmpty()) {
            Toast.makeText(this, "Escreva algo sobre o seu café!", Toast.LENGTH_SHORT).show()
            return
        }

        userDAO.getUserByEmail(email) { user ->
            val post = Post(
                imageString = imagemBase64,
                descricao = descricao,
                cidade = cidadeAtual,
                autor = email,
                autorNome = user?.username ?: "Barista Anônimo",
                autorFoto = user?.fotoPerfil ?: "",
                timestamp = System.currentTimeMillis()
            )

            postDAO.savePost(post,
                onSuccess = {
                    runOnUiThread {
                        Toast.makeText(this, "Post publicado! ☕", Toast.LENGTH_SHORT).show()
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
    }
}
