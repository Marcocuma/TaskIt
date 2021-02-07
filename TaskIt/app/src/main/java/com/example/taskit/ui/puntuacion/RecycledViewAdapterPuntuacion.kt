package com.example.taskit.ui.puntuacion

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.taskit.R
import com.example.taskit.model.Tareas
import kotlinx.android.synthetic.main.tarea_card.view.*
import java.io.InputStream
import java.net.URL

class RecycledViewAdapterPuntuacion(val context:Context, val listaNota:List<Tareas>): RecyclerView.Adapter<BaseViewHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return NotasViewHolder(LayoutInflater.from(context).inflate(R.layout.tarea_card, parent, false))
    }
    //Si la funcion solo devuelve un dato
    override fun getItemCount(): Int = listaNota.size

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if(holder is NotasViewHolder){
            holder.bind(listaNota[position],position)
        }
    }

    inner class NotasViewHolder(itemView: View):BaseViewHolder<Tareas>(itemView){
        override fun bind(item: Tareas, position: Int) {
            itemView.textViewNombreTarea.text = item.titulo
            itemView.textViewDescripcion.text =  item.descripcion
            itemView.textViewPuntos.text = item.puntuacion.toString()
            itemView.button_completar.setOnClickListener(null)
            itemView.button_completar.isVisible = false

        }
    }
}