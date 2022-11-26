package com.example.busticketreservationsystem.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemReviewBinding
import com.example.busticketreservationsystem.entity.Reviews

class ReviewsAdapter: RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>() {

    private lateinit var reviewsList: List<Reviews>

    fun setReviewsList(reviewsList: List<Reviews>){
        this.reviewsList = reviewsList
    }

    inner class ReviewsViewHolder(val binding: ItemReviewBinding): RecyclerView.ViewHolder(binding.root) {
        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReviewBinding.inflate(layoutInflater, parent, false)
        return ReviewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        holder.binding.apply {
            ratingText.text = reviewsList[position].rating.toString()
            reviewText.text = reviewsList[position].feedback
            feedbackText.text = "Feedback #${position+1}"
        }
    }

    override fun getItemCount(): Int {
        return reviewsList.size
    }
}