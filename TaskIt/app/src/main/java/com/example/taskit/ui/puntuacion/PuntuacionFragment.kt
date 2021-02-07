package com.example.taskit.ui.puntuacion

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskit.MainActivity
import com.example.taskit.R
import com.example.taskit.model.Tareas
import com.example.taskit.ui.home.RecycledViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*

import kotlinx.android.synthetic.main.fragment_puntuacion.*
import kotlinx.coroutines.Dispatchers.Default
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random.*
import kotlin.random.Random.Default.nextInt


class PuntuacionFragment : Fragment() {
    lateinit var completadas: ArrayList<Tareas>
    var total:Long =0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_puntuacion, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recLista.layoutManager = LinearLayoutManager(context)
        val c: Calendar = Calendar.getInstance()
        calendarView.date = c.timeInMillis
        calendarView.setOnDateChangeListener(CalendarView.OnDateChangeListener { _: CalendarView, anio: Int, mes: Int, dia: Int ->
            recLista.isVisible = false
            textViewScore.isVisible = false
            constraintBarra.isVisible = false
            imageViewSpinner.isVisible = true
            val c:Calendar = Calendar.getInstance()
            c.set(Calendar.MONTH,mes)

            val fecha:String = dia.toString()+c.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault())+anio.toString()
            setBar(fecha)
        })
        setBar(""+c.get(Calendar.DAY_OF_MONTH)+""+c.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault())+""+c.get(Calendar.YEAR))
    }

    fun setBar(fecha:String){
        completadas = ArrayList()
        var puntos:Int = 0
        val myRef = FirebaseDatabase.getInstance("https://taskit-e835d-default-rtdb.europe-west1.firebasedatabase.app/").getReference(FirebaseAuth.getInstance().currentUser?.uid+"")
        myRef.child("completadas").child(fecha).child("tareas").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children
                children.forEach {
                    val e = it.getValue(Tareas::class.java)
                    if (e != null) {
                    }
                    if (e != null) {
                        completadas.add(e)
                        puntos += e.puntuacion.toInt()
                    }
                }
                getTotal(fecha,puntos)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }
        })

    }
    fun getTotal(fecha: String,puntos: Int){
        val database:FirebaseDatabase = FirebaseDatabase.getInstance("https://taskit-e835d-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference(FirebaseAuth.getInstance().currentUser?.uid+"").child("completadas")
            .child(fecha).child("total")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                total = snapshot.value as Long
                setProgress(puntos)
                cargarLista()
            }

        })
    }
    fun setProgress(puntos:Int){
        textViewMax.text = "100% - "+total+" -"
        vertical_progressbar.max= total.toInt()
        textViewMiddle.text = "50% -"+total.div(2)+" -"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            vertical_progressbar.setProgress(puntos,true)
        } else{
            vertical_progressbar.progress = puntos
        }
    }
    fun cargarLista(){
        if (!completadas.isNullOrEmpty()) {
            imageViewSpinner.isVisible = false
            recLista.isVisible = true
            textViewScore.isVisible = true
            constraintBarra.isVisible = true
            recLista.adapter = context?.let {
                RecycledViewAdapterPuntuacion(it, completadas)
            }
        }else {
            recLista.isVisible = false
            textViewScore.isVisible = false
            constraintBarra.isVisible = false
            imageViewSpinner.isVisible = false
            Toast.makeText(context, "No hay registro ese dia", Toast.LENGTH_SHORT).show()
        }
    }
}