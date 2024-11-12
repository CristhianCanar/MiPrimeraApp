package com.cristhian.miprimeraapp.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cristhian.miprimeraapp.model.database.dao.UserDao
import com.cristhian.miprimeraapp.model.database.entities.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class UsuarioDatabase: RoomDatabase() {
    abstract fun getUserDao():UserDao
}