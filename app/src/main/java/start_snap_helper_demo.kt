package com.redspace.startsnaphelper.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redspace.androidutils.demo.R
import com.redspace.startsnaphelper.SnapToPercentHelper
import kotlinx.android.synthetic.main.activity_start_snap_helper.*

class StartSnapHelperDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_snap_helper)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create a SnapToPercentHelper and move snapping point by 30% of the size of the RecyclerView
        val snapHelper = SnapToPercentHelper(0.3f)
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter = Adapter()
    }
}

class Adapter : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fake_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = 20

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {}
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
