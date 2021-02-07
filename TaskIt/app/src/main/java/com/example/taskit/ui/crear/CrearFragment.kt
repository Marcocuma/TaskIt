package com.example.taskit.ui.crear

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.taskit.MainActivity
import com.example.taskit.R
import com.example.taskit.model.Tareas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_crear.*

class CrearFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_crear, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_createTask.setOnClickListener {
            val nombre = editTextTextTaskName.text.toString()
            val descripcion = editTextTextDescription.text.toString()
            if(editTextTextScore.text.toString().isNotEmpty()) {
                val puntuacion: Long = editTextTextScore.text.toString().toLong()
                if (nombre.isNotEmpty() && descripcion.isNotEmpty() && puntuacion > 0){
                    if(!(activity as MainActivity?)?.compruebaTarea(nombre)!!) {
                        val database: FirebaseDatabase =
                            FirebaseDatabase.getInstance("https://taskit-e835d-default-rtdb.europe-west1.firebasedatabase.app/")
                        println(FirebaseAuth.getInstance().currentUser?.uid + "")
                        val myRef =
                            database.getReference(FirebaseAuth.getInstance().currentUser?.uid + "")
                                .child("tareas")
                        (activity as MainActivity?)?.listaTareas?.add(
                            Tareas(
                                nombre,
                                descripcion,
                                puntuacion
                            )
                        )
                        myRef.setValue((activity as MainActivity?)?.listaTareas)
                        limpiarCampos()
                    }
                }
            }
        }
    }
    fun limpiarCampos(){
        editTextTextTaskName.text = Editable.Factory.getInstance().newEditable("")
        editTextTextDescription.text = Editable.Factory.getInstance().newEditable("")
        editTextTextScore.text = Editable.Factory.getInstance().newEditable("")
    }
}