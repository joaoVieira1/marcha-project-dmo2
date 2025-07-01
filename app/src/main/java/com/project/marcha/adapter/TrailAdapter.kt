package com.project.marcha.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.marcha.R
import com.project.marcha.model.Trail

class TrailAdapter(private val trails: Array<Trail>) : RecyclerView.Adapter<TrailAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val nameUser : TextView = view.findViewById(R.id.textView_name_user_card)
        val nameTrail: TextView = view.findViewById(R.id.textView_name_trail_card)
        val data : TextView = view.findViewById(R.id.textView_data_card)
        val time : TextView = view.findViewById(R.id.textView_time_card)
        val steps : TextView = view.findViewById(R.id.textView_steps_card)
        val distance : TextView = view.findViewById(R.id.textView_distance_card)
        val maxHeight : TextView = view.findViewById(R.id.textView_max_height_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trail, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrailAdapter.ViewHolder, position: Int) {
        holder.nameUser.text = trails[position].getUserName()
        holder.nameTrail.text = trails[position].getTrailName()
        holder.data.text = trails[position].getData().toString()

        holder.time.text = "Tempo: ${trails[position].getTime()}"
        holder.steps.text = "Passos: ${trails[position].getSteps()}"
        holder.distance.text = "Distância: %.2f m".format(trails[position].getDistance())
        holder.maxHeight.text = "Altitude Máx: %.2f m".format(trails[position].getMaxHeight())
    }

    override fun getItemCount(): Int {
        return trails.size
    }


}