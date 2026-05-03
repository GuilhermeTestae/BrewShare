package br.com.brewshare.model

data class Post(
    val id: String = "",
    val imageString: String = "",
    val descricao: String = "",
    val cidade: String = "",
    val autor: String = "",           // email do autor
    val autorNome: String = "",       // username público
    val autorFoto: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
