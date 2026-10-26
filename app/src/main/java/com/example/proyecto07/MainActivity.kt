package com.example.proyecto07

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var txtModo: TextView
    private lateinit var txtEstadoLampara: TextView
    private lateinit var txtUmbral: TextView
    private lateinit var sliderUmbral: SeekBar

    private var modoActual = "Auto"

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

        btnAuto.setOnClickListener {
            modoActual = "Auto"
            txtModo.text = "Modo seleccionado: Auto"
            txtEstadoLampara.text =
                "Lámpara: esperando datos del Arduino"
        }

        btnOn.setOnClickListener {
            modoActual = "Forzado On"
            txtModo.text = "Modo seleccionado: Forzado On"
            txtEstadoLampara.text =
                "Lámpara: encendido solicitado"
        }

        btnOff.setOnClickListener {
            modoActual = "Forzado Off"
            txtModo.text = "Modo seleccionado: Forzado Off"
            txtEstadoLampara.text =
                "Lámpara: apagado solicitado"
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
                }
            }
        )
    }
}
