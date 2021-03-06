package com.app.rectonote.adapter

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.ProjectSelectActivity
import com.app.rectonote.R
import com.app.rectonote.database.ProjectEntity


class ProjectSelectAdapter(
    private val projectDataset: List<ProjectEntity>,
    private val activity: ProjectSelectActivity
) : RecyclerView.Adapter<ProjectSelectAdapter.ProjectViewHolder>() {

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val projectName = itemView.findViewById<TextView>(R.id.project_name)
        val projectData = itemView.findViewById<TextView>(R.id.project_data)
        val projectCard = itemView.findViewById<CardView>(R.id.project_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
        //grab layout
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        var project = projectDataset[position]
        holder.projectName.text = project.name
        holder.projectData.text = "${project.tempo} bpm\n${project.key.label}"
        holder.projectCard.setCardBackgroundColor(Color.parseColor(project.color))
        // holder.projectKey.text = project.key
        holder.projectCard.setOnClickListener { _ ->
            val intent = Intent()
            intent.putExtra("project", project)
            activity.apply {
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun getItemCount() = projectDataset.size


}