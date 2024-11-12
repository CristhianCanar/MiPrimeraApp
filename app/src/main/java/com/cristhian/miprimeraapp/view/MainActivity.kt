package com.cristhian.miprimeraapp.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cristhian.miprimeraapp.databinding.ActivityMainBinding
import com.cristhian.miprimeraapp.model.database.dao.UserDao
import com.cristhian.miprimeraapp.model.database.entities.UserEntity
import com.cristhian.miprimeraapp.model.database.providers.UsuarioDatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var usuarioAdapter: UsuarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = UsuarioDatabaseProvider.getDatabase(binding.root.context)

        // Obtener todos los usuarios y mostrarlos
        val usuarioDao = db.getUserDao()

        //eliminarUsuarios(usuarioDao)

        obtenerUsuarios(usuarioDao)

        val listaUsers:List<UserEntity> = listOf(
            UserEntity(nombre = "Luis", apellido = "Perez"),
            UserEntity(nombre = "Maria", apellido = "Rodriguez"),
            UserEntity(nombre = "Margot", apellido = "Perales"),
        )

        CoroutineScope(Dispatchers.IO).launch {
            val usuarios = db.getUserDao().insertAllUsers(listaUsers)
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            val usuarios = db.getUserDao().getAllUsers()
//            if (usuarios.isNotEmpty())
//                usuarios.forEach {
//                    println("Usuario: ${it.nombre}, Apellido: ${it.apellido}")
//                }
//        }

        // Inicializa el adaptador con una lista vacía por ahora
        usuarioAdapter = UsuarioAdapter(this, mutableListOf())
        binding.lvUsers.adapter = usuarioAdapter

        // Cargar los usuarios desde la base de datos
        cargarUsuarios()

    }
    private fun obtenerUsuarios(usuarioDao: UserDao) {
        lifecycleScope.launch(Dispatchers.IO) {
            val usuarios = usuarioDao.getAllUsers()
            // Si deseas actualizar la UI, usa withContext para cambiar al hilo principal
            withContext(Dispatchers.Main) {
                usuarios.forEach { usuario ->
                    println("Usuario: ${usuario.nombre}, Apellido: ${usuario.apellido}")
                }
            }
        }
    }

    private fun cargarUsuarios() {
        val usuarioDao = UsuarioDatabaseProvider.getDatabase(applicationContext).getUserDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val usuarios = usuarioDao.getAllUsers()
            // Actualizar la lista en el hilo principal
            withContext(Dispatchers.Main) {
                usuarioAdapter.clear()
                usuarioAdapter.addAll(usuarios)
                usuarioAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun eliminarUsuarios(usuarioDao: UserDao) {
        lifecycleScope.launch(Dispatchers.IO) {
            usuarioDao.eliminarTodos()
        }
    }

}
