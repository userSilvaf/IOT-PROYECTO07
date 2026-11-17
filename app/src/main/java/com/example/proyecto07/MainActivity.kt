package com.example.proyecto07

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var txtModo: TextView
    private lateinit var txtEstadoLampara: TextView
    private lateinit var txtUmbral: TextView
    private lateinit var sliderUmbral: SeekBar

    private var modoActual = "AUTO"

    private val executor = Executors.newSingleThreadExecutor()

    private val apiUrl =
        "https://10.0.2.2:5000/api/configuracion"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtModo = findViewById(R.id.txtModo)
        txtEstadoLampara = findViewById(R.id.txtEstadoLampara)
        txtUmbral = findViewById(R.id.txtUmbral)
        sliderUmbral = findViewById(R.id.sliderUmbral)

        val btnAuto = findViewById<Button>(R.id.btnAuto)
        val btnOn = findViewById<Button>(R.id.btnOn)
        val btnOff = findViewById<Button>(R.id.btnOff)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        btnAuto.setOnClickListener {
            modoActual = "AUTO"
            txtModo.text = "Modo seleccionado: Auto"
            enviarConfiguracion()
        }

        btnOn.setOnClickListener {
            modoActual = "ON"
            txtModo.text = "Modo seleccionado: Forzado On"
            enviarConfiguracion()
        }

        btnOff.setOnClickListener {
            modoActual = "OFF"
            txtModo.text = "Modo seleccionado: Forzado Off"
            enviarConfiguracion()
        }

        sliderUmbral.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    txtUmbral.text =
                        "Umbral configurado: $progress"
                }

                override fun onStartTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }

                override fun onStopTrackingTouch(
                    seekBar: SeekBar?
                ) {
                    enviarConfiguracion()
                }
            }
        )

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun enviarConfiguracion() {

        val umbral = sliderUmbral.progress

        val datos = JSONObject().apply {
            put("umbral", umbral)
            put("modo", modoActual)
        }

        executor.execute {

            var conexion: HttpURLConnection? = null

            try {
                val url = URL(apiUrl)
                conexion = url.openConnection() as HttpURLConnection

                conexion.requestMethod = "POST"
                conexion.connectTimeout = 5000
                conexion.readTimeout = 5000
                conexion.doOutput = true

                conexion.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                conexion.outputStream.use { salida ->
                    salida.write(datos.toString().toByteArray(Charsets.UTF_8))
                }

                val codigo = conexion.responseCode

                if (codigo in 200..299) {
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Configuración guardada en MySQL",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Error al enviar configuración: $codigo",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {

                runOnUiThread {
                    Toast.makeText(
                        this,
                        "No se pudo conectar con Flask: ${e.message}",
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
