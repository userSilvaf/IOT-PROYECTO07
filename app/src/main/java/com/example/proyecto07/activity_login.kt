package com.example.proyecto07

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection
import java.net.Proxy

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsuario: EditText
    private lateinit var edtContrasena: EditText

    private val executor = Executors.newSingleThreadExecutor()

    private  val API_URL = "https://10.0.2.2:5000/api/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtUsuario = findViewById(R.id.edtUsuario)
        edtContrasena = findViewById(R.id.edtContrasena)

        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val usuario = edtUsuario.text.toString().trim()
            val contrasena = edtContrasena.text.toString()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(
                    this,
                    "Complete todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                btnLogin.isEnabled = false
                validarAcceso(usuario, contrasena, btnLogin)
            }
        }
    }

    private fun validarAcceso(
        usuario: String,
        contrasena: String,
        btnLogin: Button
    ) {
        executor.execute {
            var conexion: HttpURLConnection? = null

            try {
                conexion = URL(API_URL).openConnection() as HttpURLConnection

                conexion.requestMethod = "POST"
                conexion.connectTimeout = 10000
                conexion.readTimeout = 10000
                conexion.doOutput = true

                conexion.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                val datos = JSONObject().apply {
                    put("usuario", usuario)
                    put("contrasena", contrasena)
                }

                conexion.outputStream.bufferedWriter().use {
                    it.write(datos.toString())
                }

                val codigo = conexion.responseCode

                val respuesta = if (codigo in 200..299) {
                    conexion.inputStream.bufferedReader().use {
                        it.readText()
                    }
                } else {
                    conexion.errorStream?.bufferedReader()?.use {
                        it.readText()
                    } ?: ""
                }

                val resultado = JSONObject(respuesta)

                runOnUiThread {
                    btnLogin.isEnabled = true

                    if (codigo == 200) {
                        val token = resultado.getString("token")
                        val rol = resultado.getString("rol")

                        val intent = if (rol == "admin") {
                            Intent(this, AdministradorActivity::class.java)
                        } else {
                            Intent(this, MainActivity::class.java)
                        }

                        intent.putExtra("TOKEN", token)
                        intent.putExtra("ROL", rol)

                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(
                            this,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (error: Exception) {
            android.util.Log.e(
                "ERROR_LOGIN",
                "Error al conectar con Flask",
                error
            )

            runOnUiThread {
                btnLogin.isEnabled = true

                Toast.makeText(
                    this,
                    "Error: ${error.javaClass.simpleName}: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            } finally {
                conexion?.disconnect()
            }
        }
    }
    private fun probarConexion(btnLogin: Button) {
        executor.execute {
            var conexion: HttpsURLConnection? = null

            try {
                val url = URL("https://10.0.2.2:5000/")
                conexion = url.openConnection(Proxy.NO_PROXY) as HttpsURLConnection

                conexion.connectTimeout = 15000
                conexion.readTimeout = 15000
                conexion.requestMethod = "GET"

                val codigo = conexion.responseCode

                val respuesta = conexion.inputStream.bufferedReader().use {
                    it.readText()
                }

                android.util.Log.d(
                    "PRUEBA_HTTPS",
                    "Código: $codigo | Respuesta: $respuesta"
                )

                runOnUiThread {
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "Conexión correcta: HTTP $codigo",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (error: Exception) {
                android.util.Log.e(
                    "PRUEBA_HTTPS",
                    "Falló la conexión HTTPS",
                    error
                )

                runOnUiThread {
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        "Error: ${error.javaClass.simpleName}: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } finally {
                conexion?.disconnect()
            }
        }
    }

    override fun onDestroy() {
        executor.shutdown()
        super.onDestroy()
    }
}

