package cris.twoper.spookymatchy.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import cris.twoper.spookymatchy.R
import cris.twoper.spookymatchy.SpookyMatch
import cris.twoper.spookymatchy.databinding.SpookyCardsBinding

class SpookyAdapter (private val items: List<SpookyModel>) :
    RecyclerView.Adapter<SpookyAdapter.ViewHolder>() {

    private val args = Bundle()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    class ViewHolder(val binding: SpookyCardsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SpookyCardsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        fetchValue(holder, position)

        holder.binding.Onebutton.apply {
            setOnClickListener {
                args.putString("title", "webview")
                Navigation.findNavController(holder.itemView).navigate(
                    R.id.action_FirstFragment_to_SecondFragment,
                    args
                )
            }
        }

        holder.binding.Twobutton.apply {
            setOnClickListener {
                args.putString("title", "webview2")
                Navigation.findNavController(holder.itemView).navigate(
                    R.id.action_FirstFragment_to_SecondFragment,
                    args
                )
            }
        }

        holder.binding.Tributton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, SpookyMatch::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun fetchValue(holder: ViewHolder, position: Int) {
        firestore.collection("JULY")
            .document("SPOOKYST666MATCH")
            .get()
            .addOnSuccessListener { documentSnapshot: DocumentSnapshot? ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val isValueTrue = documentSnapshot.getBoolean("spookythree") ?: false
                    updateButtonText(holder, position, isValueTrue)
                }
            }
    }

    private fun toggleValue(holder: ViewHolder, position: Int) {
        val valuesMap = null
        val isValueTrue = valuesMap?.get(position) ?: false
        val newValue = !isValueTrue

        firestore.collection("JULY")
            .document("SPOOKYST666MATCH")
            .update("spookythree", newValue)
            .addOnSuccessListener {
                updateButtonText(holder, position, newValue)
            }
    }

    private fun updateButtonText(holder: ViewHolder, position: Int, isValueTrue: Boolean) {
        holder.binding.Onebutton.text = if (isValueTrue) {
            "ĐĂNG KÝ"
        } else {
            holder.itemView.context.getString(R.string.spook1)
        }
        holder.binding.Twobutton.text = if (isValueTrue) {
            "ĐĂNG NHẬP"
        } else {
            holder.itemView.context.getString(R.string.spook2)
        }
        holder.binding.Tributton.text = if (isValueTrue) {
            "Khởi đầu"
        } else {
            holder.itemView.context.getString(R.string.spook3)
        }
    }
}
