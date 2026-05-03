package br.com.brewshare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.brewshare.databinding.ItemPostBinding
import br.com.brewshare.model.Post
import br.com.brewshare.util.Base64Converter

class PostAdapter(
    private var posts: MutableList<Post> = mutableListOf()
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    fun setPosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    fun appendPosts(newPosts: List<Post>) {
        val start = posts.size
        posts.addAll(newPosts)
        notifyItemRangeInserted(start, newPosts.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.tvUsername.text = post.autorNome.ifEmpty { "Anônimo" }
            binding.tvCidade.text = if (post.cidade.isNotEmpty()) "☕ ${post.cidade}" else "Sem localização"
            binding.tvDescricao.text = post.descricao

            if (post.imageString.isNotEmpty()) {
                try {
                    binding.imgPost.setImageBitmap(Base64Converter.stringToBitmap(post.imageString))
                } catch (_: Exception) {}
            }

            if (post.autorFoto.isNotEmpty()) {
                try {
                    binding.imgAutorFoto.setImageBitmap(Base64Converter.stringToBitmap(post.autorFoto))
                } catch (_: Exception) {}
            }
        }
    }
}
