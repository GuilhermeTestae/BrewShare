package br.com.brewshare.dao

import br.com.brewshare.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserDAO {
    private val db = FirebaseFirestore.getInstance()
    private val collection = "usuarios"

    fun saveUser(user: User, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection(collection).document(user.email)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Erro ao salvar usuário") }
    }

    fun getUserByEmail(email: String, onResult: (User?) -> Unit) {
        db.collection(collection).document(email)
            .get()
            .addOnSuccessListener { doc ->
                onResult(if (doc.exists()) doc.toObject(User::class.java) else null)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun updateProfile(
        email: String,
        nomeCompleto: String,
        fotoPerfil: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val updates = hashMapOf<String, Any>(
            "nomeCompleto" to nomeCompleto,
            "fotoPerfil" to fotoPerfil
        )
        db.collection(collection).document(email)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Erro ao atualizar perfil") }
    }
}
