package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvDisplay: TextView
    private lateinit var etName: TextInputEditText
    private lateinit var btnConfirmName: Button
    private lateinit var btnLength: Button
    private lateinit var btnWeight: Button
    private lateinit var btnTemp: Button
    private lateinit var btnReset: Button

    private lateinit var btnNumbers: Array<Button>
    private lateinit var btnDecimal: Button

    private var currentInput = ""
    private var lastConversionType = ""
    private var isNameConfirmed = false
    private val df = DecimalFormat("#.####")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupNumericButtons()
        setupFunctionalButtons()
    }

    private fun initializeViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        tvDisplay = findViewById(R.id.tvDisplay)
        etName = findViewById(R.id.etName)
        btnConfirmName = findViewById(R.id.btnConfirmName)
        btnLength = findViewById(R.id.btnLength)
        btnWeight = findViewById(R.id.btnWeight)
        btnTemp = findViewById(R.id.btnTemp)
        btnReset = findViewById(R.id.btnReset)
        btnDecimal = findViewById(R.id.btnDecimal)

        btnNumbers = Array(10) { index ->
            findViewById(resources.getIdentifier("btn$index", "id", packageName))
        }
    }

    private fun setupNumericButtons() {
        btnNumbers.forEach { button ->
            button.setOnClickListener {
                appendToInput(button.text.toString())
            }
        }

        btnDecimal.setOnClickListener {
            if (!currentInput.contains(".")) {
                if (currentInput.isEmpty()) {
                    currentInput = "0"
                }
                appendToInput(".")
            }
        }
    }

    private fun setupFunctionalButtons() {
        btnConfirmName.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isNotEmpty()) {
                tvWelcome.text = "Bienvenido, $name, a la App de Conversión"
                isNameConfirmed = true

                btnLength.isEnabled = true
                btnWeight.isEnabled = true
                btnTemp.isEnabled = true

                etName.isEnabled = false
                btnConfirmName.isEnabled = false

                Toast.makeText(this, "¡Nombre confirmado! Ya puede usar la calculadora.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, ingrese su nombre para continuar.", Toast.LENGTH_SHORT).show()
            }
        }

        btnReset.setOnClickListener {
            currentInput = ""
            lastConversionType = ""
            tvDisplay.text = ""
        }

        setupConversionButton(btnLength, "length")
        setupConversionButton(btnWeight, "weight")
        setupConversionButton(btnTemp, "temp")
    }

    private fun setupConversionButton(button: Button, conversionType: String) {
        button.setOnClickListener {
            if (!isNameConfirmed) {
                Toast.makeText(this, "Por favor, confirma tu nombre primero.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentInput.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese un valor primero.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val inputValue = currentInput.toDouble()
                var result = 0.0
                var resultText = ""

                when (conversionType) {
                    "length" -> {
                        if (lastConversionType == "length_m_to_ft") {
                            result = inputValue * 0.3048
                            resultText = "$inputValue pies = ${df.format(result)} metros"
                            lastConversionType = "length_ft_to_m"
                        } else {
                            result = inputValue * 3.28084
                            resultText = "$inputValue metros = ${df.format(result)} pies"
                            lastConversionType = "length_m_to_ft"
                        }
                    }
                    "weight" -> {
                        if (lastConversionType == "weight_kg_to_lb") {
                            result = inputValue * 0.453592
                            resultText = "$inputValue libras = ${df.format(result)} kilogramos"
                            lastConversionType = "weight_lb_to_kg"
                        } else {
                            result = inputValue * 2.20462
                            resultText = "$inputValue kilogramos = ${df.format(result)} libras"
                            lastConversionType = "weight_kg_to_lb"
                        }
                    }
                    "temp" -> {
                        if (lastConversionType == "temp_c_to_f") {
                            result = (inputValue - 32) * 5 / 9
                            resultText = "$inputValue °F = ${df.format(result)} °C"
                            lastConversionType = "temp_f_to_c"
                        } else {
                            result = (inputValue * 9 / 5) + 32
                            resultText = "$inputValue °C = ${df.format(result)} °F"
                            lastConversionType = "temp_c_to_f"
                        }
                    }
                }

                tvDisplay.text = resultText
                currentInput = result.toString()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Valor no válido.", Toast.LENGTH_SHORT).show()
                currentInput = ""
                tvDisplay.text = ""
            }
        }
    }

    private fun appendToInput(digit: String) {
        if (!isNameConfirmed) {
            Toast.makeText(this, "Por favor, confirma tu nombre primero.", Toast.LENGTH_SHORT).show()
            return
        }

        if (lastConversionType.isNotEmpty()) {
            currentInput = digit
            lastConversionType = ""
        } else {
            currentInput += digit
        }

        tvDisplay.text = currentInput
    }
}

