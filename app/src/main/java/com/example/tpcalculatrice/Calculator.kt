package com.example.tpcalculatrice

import android.os.Bundle

class Calculator {

    private var displayText = "" //   le texte affiché dans le TextView
    private var lastOperation: Char? = null //  la dernière opération effectuée
    private var currentNumber = "" //  le nombre actuellement en cours de saisie
    private var result = 0  //  le résultat du calcul
    private var isError = false // si une erreur est survenue

    // Les Constantes
    companion object {
        const val ADD = '+'       // Addition
        const val SUBTRACT = '-'  // Soustraction
        const val MULTIPLY = '*'  // Multiplication
        const val DIVIDE = '/'    // Division
        const val MODULO = '%'    // Modulo
    }

    // Renvoie le texte à afficher, ou "Erreur" si une erreur s'est produite
    fun getDisplayText(): String = if (isError) "Erreur" else displayText

    // Sauvegarde l'état actuel dans le Bundle pour la restauration après un changement de configuration
    fun saveState(outState: Bundle) {
        outState.putString("displayText", displayText)
        outState.putString("currentNumber", currentNumber)
        outState.putChar("lastOperation", lastOperation ?: ' ')
        outState.putInt("result", result)
        outState.putBoolean("isError", isError)
    }

    // Restaure l'état depuis le Bundle pour restaurer l'interface
    fun restoreState(savedInstanceState: Bundle) {
        displayText = savedInstanceState.getString("displayText", "")
        currentNumber = savedInstanceState.getString("currentNumber", "")
        lastOperation = savedInstanceState.getChar("lastOperation").takeIf { it != ' ' }  // Si opération non vide
        result = savedInstanceState.getInt("result", 0)
        isError = savedInstanceState.getBoolean("isError", false)
    }


    fun addNumber(number: Char) {
        if (isError) reset()  // Si une erreur est survenue, on réinitialise

        // Limite la saisie du nombre à 8 chiffres
        if (currentNumber.length < 8) {
            currentNumber += number
            displayText += number
        }
    }

    // Ajoute une opération à la calculatrice
    fun addOperation(operation: Char) {
        if (isError) reset()

        if (currentNumber.isNotEmpty()) {
            if (lastOperation != null) {
                calculate()
                displayText = "$result $operation "
            } else {
                result = currentNumber.toInt()
                displayText = "$currentNumber $operation "
            }
            lastOperation = operation
            currentNumber = ""
        } else if (lastOperation != null) {
            displayText = displayText.dropLast(3) + " $operation " // Supprime la dernière opération et ajoute la nouvelle
            lastOperation = operation
        } else if (displayText.isNotEmpty()) {
            lastOperation = operation
            displayText += " $operation "
        }

        val parts = displayText.split(" ")
        if (parts.size >= 3) {
            val left = parts[0].length
            val right = parts[2].length
            if (left > 8 || right > 8) {
                reset()
                displayText = "Erreur: limite 8 chiffres"
            }
        }
    }

    // Calcule le résultat final en utilisant l'opération précédente
    fun calculateResult() {
        if (isError) return
        if (lastOperation != null && currentNumber.isNotEmpty()) {
            calculate()
            lastOperation = null
            currentNumber = result.toString()
            displayText = result.toString()
        }
    }

    // Change le signe du dernier nombre saisi
    fun negateLastNumber() {
        if (isError) reset()
        if (currentNumber.isNotEmpty()) {
            currentNumber = if (currentNumber.startsWith("-")) {
                currentNumber.drop(1)
            } else {
                "-$currentNumber"
            }
            displayText = displayText.dropLastWhile { it != ' ' } + currentNumber
        }
    }

    // Supprime le dernier caractère saisi (nombre ou opération)
    fun removeLastCharacter() {
        if (isError) return

        when {
            currentNumber.isNotEmpty() -> {
                // Cas où le nombre est négatif avec un seul chiffre
                if (currentNumber.length == 2 && currentNumber.startsWith("-")) {
                    currentNumber = ""
                    displayText = displayText.dropLast(2) // Supprime le nombre et le signe "-"
                } else {
                    currentNumber = currentNumber.dropLast(1)
                    displayText = displayText.dropLast(1)
                }
            }
            lastOperation != null -> {
                // Cas où on supprime une opération
                displayText = displayText.dropLast(3) // Supprime l'opération et les espaces
                lastOperation = null
            }
            else -> {
                // Cas général
                displayText = displayText.dropLast(1)
            }
        }
    }

    // Effectue le calcul de l'opération en cours
    private fun calculate() {
        if (currentNumber.isEmpty() || isError) return

        try {
            val currentValue = currentNumber.toInt()

            when (lastOperation) {
                ADD -> result += currentValue
                SUBTRACT -> result -= currentValue
                MULTIPLY -> result *= currentValue
                DIVIDE -> {
                    if (currentValue != 0) {
                        result /= currentValue
                    } else {
                        triggerError("Erreur: div/0") // Division par zéro
                        return
                    }
                }
                MODULO -> {
                    if (currentValue != 0) {
                        result %= currentValue
                    } else {
                        triggerError("Erreur: mod/0") // Modulo par zéro
                        return
                    }
                }
            }

            lastOperation = null
            currentNumber = ""
        } catch (e: Exception) {
            triggerError("Erreur: format invalide")
        }
    }

    // Gère les erreurs en réinitialisant l'état et en affichant un message d'erreur
    private fun triggerError(message: String) {
        displayText = message
        currentNumber = ""
        lastOperation = null
        result = 0
        isError = true

    }


    // Réinitialise toute la calculatrice à son état initial
    fun reset() {
        displayText = ""
        currentNumber = ""
        lastOperation = null
        result = 0
        isError = false
    }
}
