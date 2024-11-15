package com.example.tpcalculatrice

import com.example.tpcalculatrice.Calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var calculator: Calculator
    private lateinit var textViewDisplay: TextView

    // Initialisation de l'activité, restauration de l'état et configuration des boutons
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calculator = Calculator()
        textViewDisplay = findViewById(R.id.textViewResult)

        if (savedInstanceState != null) {
            calculator.restoreState(savedInstanceState)
            updateDisplay()
        }

        setupButtons()
    }

    // Sauvegarde de l'état du calculateur lors de la destruction de l'activité
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        calculator.saveState(outState)
    }

    // Configuration des boutons et gestion des actions associées
    private fun setupButtons() {
        val buttonIds = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9
        )

        buttonIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                val number = (it as Button).text[0]
                calculator.addNumber(number)
                updateDisplay()
            }
        }

        val operationButtons = mapOf(
            R.id.buttonAdd to '+',
            R.id.buttonSubtract to '-',
            R.id.buttonMultiply to '*',
            R.id.buttonDivide to '/',
            R.id.buttonModulo to '%'
        )

        operationButtons.forEach { (id, operation) ->
            findViewById<Button>(id).setOnClickListener {
                calculator.addOperation(operation)
                updateDisplay()
            }
        }

        findViewById<Button>(R.id.buttonEquals).setOnClickListener {
            calculator.calculateResult()
            updateDisplay()
            copyResultToClipboard(textViewDisplay.text.toString())
        }

        findViewById<Button>(R.id.buttonPlusMinus).setOnClickListener {
            calculator.negateLastNumber()
            updateDisplay()
        }

        findViewById<Button>(R.id.buttonC).setOnClickListener {
            calculator.removeLastCharacter()
            updateDisplay()
        }

        findViewById<Button>(R.id.buttonAC).setOnClickListener {
            calculator.reset()
            updateDisplay()
        }
    }

    // Met à jour l'affichage du TextView avec le texte limité à 20 caractères
    private fun updateDisplay() {
        val limitedText = calculator.getDisplayText().take(20)
        textViewDisplay.text = limitedText
    }

    // Copie le résultat final dans le presse-papiers
    private fun copyResultToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Result", text)
        clipboard.setPrimaryClip(clip)
    }
}
