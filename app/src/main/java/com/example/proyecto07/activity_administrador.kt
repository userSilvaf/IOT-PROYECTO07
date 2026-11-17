package com.example.proyecto07

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.Toast

class AdministradorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administrador)

        val btnGestionarUsuarios =
            findViewById<Button>(R.id.btnGestionarUsuarios)

        val btnVerRegistros =
            findViewById<Button>(R.id.btnVerRegistros)

        val btnCerrarSesionAdmin =
            findViewById<Button>(R.id.btnCerrarSesionAdmin)

        btnGestionarUsuarios.setOnClickListener {
            Toast.makeText(
                this,
                "Gestión de usuarios pendiente de implementar",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnVerRegistros.setOnClickListener {
            Toast.makeText(
                this,
                "Registros del sistema pendientes de implementar",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnCerrarSesionAdmin.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cerrarSesion() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}