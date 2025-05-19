package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    // UI Elements
    private lateinit var tvWelcome: TextView
    private lateinit var tvDisplay: TextView
    private lateinit var etName: TextInputEditText
    private lateinit var btnConfirmName: Button
    private lateinit var btnLength: Button
    private lateinit var btnWeight: Button
    private lateinit var btnTemp: Button
    private lateinit var btnReset: Button

    // Number buttons
    private lateinit var btnNumbers: Array<Button>
    private lateinit var btnDecimal: Button

    // State variables
    private var currentInput = ""
    private var lastConversionType = ""
    private var isNameConfirmed = false
    private val df = DecimalFormat("#.####") // Format for conversion results

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
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

        // Create array for numeric buttons
        btnNumbers = Array(10) { index ->
            findViewById(resources.getIdentifier("btn$index", "id", packageName))
        }
    }

    private fun setupNumericButtons() {
        // Setup numeric buttons (0-9)
        btnNumbers.forEach { button ->
            button.setOnClickListener {
                appendToInput(button.text.toString())
            }
        }

        // Setup decimal button
        btnDecimal.setOnClickListener {
            // Only add decimal if there isn't one already
            if (!currentInput.contains(".")) {
                // If input is empty, add a leading zero
                if (currentInput.isEmpty()) {
                    currentInput = "0"
                }
                appendToInput(".")
            }
        }
    }

    private fun setupFunctionalButtons() {
        // Name confirmation button
        btnConfirmName.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isNotEmpty()) {
                tvWelcome.text = "Bienvenido, $name, a la App de Conversión"
                isNameConfirmed = true

                // Enable conversion buttons
                btnLength.isEnabled = true
                btnWeight.isEnabled = true
                btnTemp.isEnabled = true

                // Make name field read-only after confirmation
                etName.isEnabled = false
                btnConfirmName.isEnabled = false

                Toast.makeText(this, "¡Nombre confirmado! Ya puede usar la calculadora.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, ingrese su nombre para continuar.", Toast.LENGTH_SHORT).show()
            }
        }

        // Reset button
        btnReset.setOnClickListener {
            currentInput = ""
            lastConversionType = ""
            tvDisplay.text = ""
        }

        // Conversion buttons
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
                            // Convert from feet to meters
                            result = inputValue * 0.3048
                            resultText = "$inputValue pies = ${df.format(result)} metros"
                            lastConversionType = "length_ft_to_m"
                        } else {
                            // Convert from meters to feet
                            result = inputValue * 3.28084
                            resultText = "$inputValue metros = ${df.format(result)} pies"
                            lastConversionType = "length_m_to_ft"
                        }
                    }
                    "weight" -> {
                        if (lastConversionType == "weight_kg_to_lb") {
                            // Convert from pounds to kilograms
                            result = inputValue * 0.453592
                            resultText = "$inputValue libras = ${df.format(result)} kilogramos"
                            lastConversionType = "weight_lb_to_kg"
                        } else {
                            // Convert from kilograms to pounds
                            result = inputValue * 2.20462
                            resultText = "$inputValue kilogramos = ${df.format(result)} libras"
                            lastConversionType = "weight_kg_to_lb"
                        }
                    }
                    "temp" -> {
                        if (lastConversionType == "temp_c_to_f") {
                            // Convert from Fahrenheit to Celsius
                            result = (inputValue - 32) * 5 / 9
                            resultText = "$inputValue °F = ${df.format(result)} °C"
                            lastConversionType = "temp_f_to_c"
                        } else {
                            // Convert from Celsius to Fahrenheit
                            result = (inputValue * 9 / 5) + 32
                            resultText = "$inputValue °C = ${df.format(result)} °F"
                            lastConversionType = "temp_c_to_f"
                        }
                    }
                }

                tvDisplay.text = resultText
                currentInput = result.toString() // Update current input for chained conversions
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

        // If we just did a conversion, replace the value instead of appending to it
        if (lastConversionType.isNotEmpty()) {
            currentInput = digit
            lastConversionType = ""
        } else {
            currentInput += digit
        }

        tvDisplay.text = currentInput
    }
}

