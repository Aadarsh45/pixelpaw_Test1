package com.example.test1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class SessionAdapter(private val sessionList: List<Session>) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sessionTimestamp: TextView = itemView.findViewById(R.id.sessionTimestamp)
        val sessionDetail: TextView = itemView.findViewById(R.id.sessionDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.session_history_item, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessionList[position]
        holder.sessionTimestamp.text = "Login time: ${session.timestamp}"
        holder.sessionDetail.text = "Session ${Math.abs((sessionList.size - position) ) }"
    }

    override fun getItemCount(): Int {
        return sessionList.size
    }
}

