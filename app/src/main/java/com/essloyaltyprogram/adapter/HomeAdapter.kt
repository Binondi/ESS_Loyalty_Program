package com.essloyaltyprogram.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.essloyaltyprogram.dataClasses.HomeItems
import com.essloyaltyprogram.databinding.HomeItemsBinding

class HomeAdapter(
    private var context: Context,
    private var list: List<HomeItems>
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HomeItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = list[position]
        animateView(holder.binding.bg, list.size)
        holder.binding.text.text = items.title
        Glide.with(context)
            .load(items.resource)
            .into(holder.binding.image)

        holder.binding.bg.setOnClickListener {
            if (position == 0){
                Toast.makeText(context, "Promotional offers", Toast.LENGTH_SHORT).show()
            }
            if (position == 1){
                Toast.makeText(context, "Redeem", Toast.LENGTH_SHORT).show()
            }
            if (position == 2){
                Toast.makeText(context, "Bank", Toast.LENGTH_SHORT).show()
            }
            if (position == 3){
                Toast.makeText(context, "Catalog", Toast.LENGTH_SHORT).show()
            }
            if (position == 4){
                Toast.makeText(context, "Transactions", Toast.LENGTH_SHORT).show()
            }
            if (position == 5){
                Toast.makeText(context, "Refer & earn", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun animateView(bg: View, count: Int) {
        bg.alpha = 0f
        val duration = 400L

        bg.animate()
            .alpha(1f)
            .setDuration(duration)
            .start()
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: HomeItemsBinding) : RecyclerView.ViewHolder(binding.root)
}
