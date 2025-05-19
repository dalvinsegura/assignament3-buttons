package com.example.myapplication

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvDisplay: TextView
    private lateinit var etName: TextInputEditText
    private lateinit var btnConfirmName: MaterialButton
    private lateinit var btnLength: MaterialButton
    private lateinit var btnWeight: MaterialButton
    private lateinit var btnTemp: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var cardDisplay: CardView
    private lateinit var cardNumpad: CardView
    private lateinit var cardConversion: CardView

    private lateinit var btnNumbers: Array<MaterialButton>
    private lateinit var btnDecimal: MaterialButton

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
        applyAnimations()
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
        cardDisplay = findViewById(R.id.cardDisplay)
        cardNumpad = findViewById(R.id.cardNumpad)
        cardConversion = findViewById(R.id.cardConversion)

        // Inicializar los botones numéricos usando MaterialButton
        btnNumbers = Array(10) { index ->
            findViewById<MaterialButton>(resources.getIdentifier("btn$index", "id", packageName))
        }
    }

    private fun setupNumericButtons() {
        btnNumbers.forEach { button ->
            button.setOnClickListener {
                button.animateClick()
                appendToInput(button.text.toString())
            }
        }

        btnDecimal.setOnClickListener {
            btnDecimal.animateClick()
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
                btnConfirmName.animateClick()
                tvWelcome.text = "Bienvenido, $name, a la App de Conversión"
                isNameConfirmed = true

                // Habilitar los botones de conversión con animación
                enableConversionButtons(true)

                // Deshabilitar la edición del nombre
                etName.isEnabled = false
                btnConfirmName.isEnabled = false

                // Mostrar mensaje y animar las tarjetas
                Toast.makeText(this, "¡Nombre confirmado! Ya puede usar la calculadora.", Toast.LENGTH_SHORT).show()
                animateCardsAfterConfirmation()
            } else {
                shakeView(etName)
                Toast.makeText(this, "Por favor, ingrese su nombre para continuar.", Toast.LENGTH_SHORT).show()
            }
        }

        btnReset.setOnClickListener {
            btnReset.animateClick()
            animateDisplayClear()
            currentInput = ""
            lastConversionType = ""
            tvDisplay.text = ""
        }

        setupConversionButton(btnLength, "length", R.color.lengthColor)
        setupConversionButton(btnWeight, "weight", R.color.weightColor)
        setupConversionButton(btnTemp, "temp", R.color.tempColor)
    }

    private fun setupConversionButton(button: MaterialButton, conversionType: String, highlightColor: Int) {
        button.setOnClickListener {
            if (!isNameConfirmed) {
                shakeView(button)
                Toast.makeText(this, "Por favor, confirma tu nombre primero.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentInput.isEmpty()) {
                shakeView(cardDisplay)
                Toast.makeText(this, "Por favor, ingrese un valor primero.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            button.animateClick()

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

                animateDisplayChange(resultText)
                currentInput = result.toString()
                flashCardBackground(cardDisplay, highlightColor)
            } catch (e: NumberFormatException) {
                shakeView(cardDisplay)
                Toast.makeText(this, "Valor no válido.", Toast.LENGTH_SHORT).show()
                currentInput = ""
                tvDisplay.text = ""
            }
        }
    }

    private fun appendToInput(digit: String) {
        if (!isNameConfirmed) {
            shakeView(etName)
            Toast.makeText(this, "Por favor, confirma tu nombre primero.", Toast.LENGTH_SHORT).show()
            return
        }

        if (lastConversionType.isNotEmpty()) {
            currentInput = digit
            lastConversionType = ""
        } else {
            currentInput += digit
        }

        animateDisplayChange(currentInput)
    }

    // Métodos de animación
    private fun animateDisplayChange(text: String) {
        // Animar cambio de texto con fade out/in
        val fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeOut.duration = 150
        fadeIn.duration = 150

        fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                tvDisplay.text = text
                tvDisplay.startAnimation(fadeIn)
            }
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })

        tvDisplay.startAnimation(fadeOut)
    }

    private fun animateDisplayClear() {
        val slideOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)
        slideOut.duration = 200

        slideOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                tvDisplay.text = ""
                tvDisplay.alpha = 0f
                tvDisplay.animate().alpha(1f).duration = 200
            }
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })

        tvDisplay.startAnimation(slideOut)
    }

    private fun applyAnimations() {
        // Aplicar animaciones iniciales a las tarjetas
        cardDisplay.apply {
            alpha = 0f
            translationY = 100f
            animate().alpha(1f).translationY(0f).setDuration(500).startDelay = 300
        }

        cardConversion.apply {
            alpha = 0f
            translationY = 100f
            animate().alpha(1f).translationY(0f).setDuration(500).startDelay = 500
        }

        cardNumpad.apply {
            alpha = 0f
            translationY = 100f
            animate().alpha(1f).translationY(0f).setDuration(500).startDelay = 700
        }
    }

    private fun animateCardsAfterConfirmation() {
        val shake = AnimationUtils.loadAnimation(this, android.R.anim.shake)
        cardDisplay.startAnimation(shake)
        cardNumpad.startAnimation(shake)

        // Mostrar que están activos ahora con un pulso
        flashCardBackground(cardDisplay, R.color.colorAccent)
        flashCardBackground(cardNumpad, R.color.colorAccent)
    }

    private fun enableConversionButtons(enable: Boolean) {
        btnLength.isEnabled = enable
        btnWeight.isEnabled = enable
        btnTemp.isEnabled = enable

        if (enable) {
            val slideIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
            slideIn.duration = 500
            btnLength.startAnimation(slideIn)
            btnWeight.startAnimation(slideIn)
            btnTemp.startAnimation(slideIn)
        }
    }

    private fun MaterialButton.animateClick() {
        this.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
            .withEndAction {
                this.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }
    }

    private fun shakeView(view: View) {
        val shake = AnimationUtils.loadAnimation(this, android.R.anim.shake)
        view.startAnimation(shake)
    }

    private fun flashCardBackground(card: CardView, colorResId: Int) {
        val colorFrom = card.cardBackgroundColor.defaultColor
        val colorTo = ContextCompat.getColor(this, colorResId)

        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 250

        colorAnimation.addUpdateListener { animator ->
            card.setCardBackgroundColor(animator.animatedValue as Int)
        }

        colorAnimation.start()

        // Volver al color original después de un breve intervalo
        card.postDelayed({
            val reverseAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
            reverseAnimation.duration = 500

            reverseAnimation.addUpdateListener { animator ->
                card.setCardBackgroundColor(animator.animatedValue as Int)
            }

            reverseAnimation.start()
        }, 250)
    }
}
