package br.com.brewshare.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthHelper {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun getCurrentEmail(): String? = firebaseAuth.currentUser?.email

    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    fun registerUser(
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess(firebaseAuth.currentUser)
                else onFailure(task.exception?.message ?: "Erro ao cadastrar")
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess(firebaseAuth.currentUser)
                else onFailure(task.exception?.message ?: "Erro no login")
            }
    }

    fun updatePassword(
        newPassword: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseAuth.currentUser?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else onFailure(task.exception?.message ?: "Erro ao alterar senha")
            } ?: onFailure("Usuário não autenticado")
    }

    fun signOut() = firebaseAuth.signOut()
}
