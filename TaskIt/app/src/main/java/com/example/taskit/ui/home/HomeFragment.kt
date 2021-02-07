package com.example.taskit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskit.MainActivity
import com.example.taskit.R
import com.example.taskit.model.Tareas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), RecycledViewAdapter.OnNotaClickListener{
    lateinit var database : FirebaseDatabase
    lateinit var completadas : ArrayList<Tareas>
    lateinit var myRef : DatabaseReference


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance("https://taskit-e835d-default-rtdb.europe-west1.firebasedatabase.app/")
        myRef = database.getReference(FirebaseAuth.getInstance().currentUser?.uid+"")
        reciclerView.layoutManager = LinearLayoutManager(context)
        imageViewSpinnerHome.isVisible = true
        cargarTareas()
    }
    fun cargarTareas(){
         return myRef.child("tareas").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (activity as MainActivity?)?.listaTareas?.clear()
                val children = snapshot.children
                var puntosTotales:Long = 0
                children.forEach {
                    val e = it.getValue(Tareas::class.java)
                    if (e != null) {
                        println(e.titulo)
                        (activity as MainActivity?)?.listaTareas?.add(e)
                        puntosTotales += e.puntuacion
                    }
                }
                guardarTotal(puntosTotales)
                recargarListaCompletadas()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }
        })

    }
    fun guardarTotal(numero:Long){
        val database:FirebaseDatabase = FirebaseDatabase.getInstance("https://taskit-e835d-default-rtdb.europe-west1.firebasedatabase.app/")
        val c: Calendar = Calendar.getInstance()
        val myRef = database.getReference(FirebaseAuth.getInstance().currentUser?.uid+"").child("completadas")
            .child(""+c.get(Calendar.DAY_OF_MONTH)+""+c.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault())+""+c.get(Calendar.YEAR)).child("total")
        myRef.setValue(numero)
        (activity as MainActivity?)?.total = numero
    }
    fun recargarListaCompletadas(){
        val c: Calendar = Calendar.getInstance()
        completadas = ArrayList()
        myRef.child("completadas").child(""+c.get(Calendar.DAY_OF_MONTH)+""+c.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault())+""+c.get(Calendar.YEAR)).child("tareas").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val children = dataSnapshot.children
                children.forEach {
                    val e = it.getValue(Tareas::class.java)
                    if (e != null) {
                        completadas.add(e)
                    }
                }
                recargarLista()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }
        })
    }
    fun recargarLista(){
        imageViewSpinnerHome.isVisible = false
        reciclerView.adapter = context?.let { RecycledViewAdapter(it,
            (activity as MainActivity?)?.listaTareas!!,completadas, this)}
    }

    override fun completar(item: Tareas) {
        val database:FirebaseDatabase = FirebaseDatabase.getInstance("https://taskit-e835d-default-rtdb.europe-west1.firebasedatabase.app/")
        val c: Calendar = Calendar.getInstance()
        val myRef = database.getReference(FirebaseAuth.getInstance().currentUser?.uid+"").child("completadas")
            .child(""+c.get(Calendar.DAY_OF_MONTH)+""+c.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault())+""+c.get(Calendar.YEAR)).child("tareas")
        completadas.add(item)
        myRef.setValue(completadas)
        recargarLista()
    }

    override fun borrar(item: Tareas) {
        var tareaBorrar : Tareas? = null
        for(tare in (activity as MainActivity?)?.listaTareas!!){
            if (tare.titulo.equals(item.titulo))
                tareaBorrar = tare
        }
        if(tareaBorrar != null) {
            (activity as MainActivity?)?.listaTareas!!.remove(tareaBorrar)
            val myRef = database.getReference(FirebaseAuth.getInstance().currentUser?.uid + "")
                .child("tareas")
            myRef.setValue((activity as MainActivity?)?.listaTareas!!)
            var total: Long = 0
            for (tarea in (activity as MainActivity?)?.listaTareas!!)
                total += tarea.puntuacion
            guardarTotal(total)
            var tareaBorrarComp : Tareas? = null
            for (tarea in completadas) {
                if (tarea.titulo.equals(item.titulo))
                    tareaBorrarComp = tarea
            }
            if(tareaBorrarComp != null) {
                completadas.remove(tareaBorrarComp)

                //Elimina la nota de las completadas si esta ahí
                val database: FirebaseDatabase =
                    FirebaseDatabase.getInstance("https://taskit-e835d-default-rtdb.europe-west1.firebasedatabase.app/")
                val c: Calendar = Calendar.getInstance()
                val myRef2 = database.getReference(FirebaseAuth.getInstance().currentUser?.uid + "")
                    .child("completadas")
                    .child(
                        "" + c.get(Calendar.DAY_OF_MONTH) + "" + c.getDisplayName(
                            Calendar.MONTH,
                            Calendar.LONG,
                            Locale.getDefault()
                        ) + "" + c.get(Calendar.YEAR)
                    ).child("tareas")
                myRef2.setValue(completadas)
            }
            recargarLista()
        }
    }

    override fun deshacer(item: Tareas) {
        var tareaBorrarComp : Tareas? = null
        for (tarea in completadas) {
            if (tarea.titulo.equals(item.titulo))
                tareaBorrarComp = tarea
        }
        if(tareaBorrarComp != null) {
            completadas.remove(tareaBorrarComp)

            //Elimina la nota de las completadas si esta ahí
            val database: FirebaseDatabase =
                FirebaseDatabase.getInstance("https://taskit-e835d-default-rtdb.europe-west1.firebasedatabase.app/")
            val c: Calendar = Calendar.getInstance()
            val myRef2 = database.getReference(FirebaseAuth.getInstance().currentUser?.uid + "")
                .child("completadas")
                .child(
                    "" + c.get(Calendar.DAY_OF_MONTH) + "" + c.getDisplayName(
                        Calendar.MONTH,
                        Calendar.LONG,
                        Locale.getDefault()
                    ) + "" + c.get(Calendar.YEAR)
                ).child("tareas")
            myRef2.setValue(completadas)
        }
        recargarLista()
    }

    override fun avisoBorrar() {
        Toast.makeText(context,"Manten pulsado para borrar la tarea", Toast.LENGTH_SHORT).show()
    }

}