package com.cristhian.miprimeraapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.cristhian.miprimeraapp.R
import com.cristhian.miprimeraapp.model.database.entities.UserEntity

class UsuarioAdapter(context: Context, usuarios: List<UserEntity>):
    ArrayAdapter<UserEntity>(context, 0, usuarios) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_usuario, parent, false
        )

        val usuario = getItem(position)

        val nombreTextView = view.findViewById<TextView>(R.id.textViewNombre)
        val apellidoTextView = view.findViewById<TextView>(R.id.textViewApellido)

        nombreTextView.text = "Nombre: ${usuario?.nombre}"
        apellidoTextView.text = "Apellido: ${usuario?.apellido}"

        return view
    }
}