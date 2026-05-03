package br.com.brewshare.dao

import br.com.brewshare.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PostDAO {
    private val db = FirebaseFirestore.getInstance()
    private val collection = "posts"

    fun savePost(post: Post, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection(collection)
            .add(post)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Erro ao salvar post") }
    }

    fun getPostsPaginated(
        limit: Long = 5,
        startAfterTimestamp: Long? = null,
        onSuccess: (List<Post>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        var query = db.collection(collection)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)

        if (startAfterTimestamp != null) {
            query = query.startAfter(startAfterTimestamp)
        }

        query.get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                }
                onSuccess(posts)
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Erro ao carregar posts") }
    }

    fun getPostsByCity(
        cidade: String,
        limit: Long = 20,
        onSuccess: (List<Post>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection(collection)
            .whereEqualTo("cidade", cidade)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                }
                onSuccess(posts)
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Erro na busca") }
    }
}
