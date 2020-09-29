package com.app.rectonote.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.app.rectonote.R
import com.app.rectonote.RecordingActivity
import com.app.rectonote.adapter.DraftTracksAdapter
import com.app.rectonote.adapter.ProjectDetailTabAdapter
import com.app.rectonote.containsSpecialCharacters
import com.app.rectonote.database.Key
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.runBlocking

class ProjectDataFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var projectDetailTabAdapter: ProjectDetailTabAdapter
    private lateinit var viewPager: ViewPager2
    lateinit var projectData: ProjectEntity
    private var recyclerView: RecyclerView? = null
    lateinit var projectDatabase: ProjectsDatabase
    lateinit var adapter: DraftTracksAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectData = it.getSerializable("THIS_PROJECT") as ProjectEntity
        }
        projectDatabase = activity?.applicationContext?.let { ProjectsDatabase.getInstance(it) }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("view", view.toString())

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

    }

    override fun onResume() {
        super.onResume()

        val projectTempo = view?.findViewById<TextView>(R.id.project_tempo)
        val projectKey = view?.findViewById<TextView>(R.id.project_key)
        recyclerView = view?.findViewById<RecyclerView>(R.id.tracks_list_view)
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
        view?.findViewById<CardView>(R.id.add_track_to_project_button)
            ?.setOnClickListener(newTrackFormProjectDetail(projectData))
        if (projectData != null) {

            projectTempo?.text = projectData.tempo.toString()
            projectKey?.text = Key.reduceKey(projectData.key)
        }
        runBlocking {
            projectData.projectId.let {
                if (it != null) {
                    adapter = DraftTracksAdapter(
                        projectDatabase.drafttracksDAO().loadTracksFromProject(it)
                    )
                }
            }
            recyclerView?.adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }

    private fun newTrackFormProjectDetail(projectData: ProjectEntity?) = View.OnClickListener {
        val intent = Intent(activity, RecordingActivity::class.java)
        intent.putExtra("project", projectData?.name)
        startActivity(intent)
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = item.groupId

        return when (item.itemId) {
            1 -> {
                spawnDialogChangeName(position)
                true
            }
            2 -> {
                spawnDialogDeleteTrack(position)
                true
            }
            else -> {
                super.onContextItemSelected(item)
            }
        }
    }

    private fun spawnDialogChangeName(trackViewId: Int) {
        val builder = context?.let { AlertDialog.Builder(it) }
        val input = EditText(context)
        builder?.apply {
            setTitle("Edit Name")
            setView(input)
            setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                val changedName = input.text.toString()
                val isNameExisted = runBlocking {
                    projectDatabase.drafttracksDAO().loadTrackNames()
                }.any { eachName -> eachName == changedName }
                when {
                    changedName.isEmpty() -> {
                        Toast.makeText(
                            context,
                            "Name cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    isNameExisted -> {
                        Toast.makeText(
                            context,
                            "This name is existed on this project.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    changedName.containsSpecialCharacters() -> {
                        Toast.makeText(
                            context,
                            "Name Invalid",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    else -> {
                        changeTrackName(changedName, trackViewId)
                    }
                }


            })
            setNegativeButton("Cancel") { _, _ ->

            }
        }
        builder?.create()?.show()
    }

    private fun changeTrackName(changedName: String, trackViewId: Int) {
        var trackData = adapter.getDatasetPosition(trackViewId)
        trackData.name = changedName
        runBlocking {
            projectDatabase.drafttracksDAO().changeData(trackData)
        }
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    private fun spawnDialogDeleteTrack(trackViewId: Int) {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.apply {
            setMessage("Are you sure want to delete a track?")
            setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                deleteTrack(trackViewId)
            })
            setNegativeButton("No", DialogInterface.OnClickListener { _, _ ->

            })
        }
        builder?.create()?.show()
    }

    private fun deleteTrack(trackViewId: Int) {
        val trackData = adapter.getDatasetPosition(trackViewId)
        runBlocking {
            projectDatabase.drafttracksDAO().deleteTrack(trackData)
        }
        adapter.removeAt(trackViewId)
    }


    companion object {
        @JvmStatic
        fun newInstance(projectData: ProjectEntity) =
            ProjectDataFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("THIS_PROJECT", projectData)
                }
            }
    }
}