package com.example.week3

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class GameState { INITIAL, COUNTDOWN, RUNNING, GAME_OVER }
enum class Mode { COLOR, TEXT }
enum class Choice { INK, WORD }
enum class ColorName(val label: String, val color: Color) {
    RED("RED", Color(0xFFE53935)),
    BLUE("BLUE", Color(0xFF2196F3)),
    GREEN("GREEN", Color(0xFF4CAF50)),
    ORANGE("ORANGE", Color(0xFFFF9800))
}

data class Question(val word: ColorName, val ink: ColorName, val mode: Mode, val inkOnLeft: Boolean) : java.io.Serializable

private const val TIME_LIMIT_MS = 5000L
private const val MAX_STRIKES = 3

private fun newQuestion(): Question {
    val word = ColorName.values().random()
    val ink = ColorName.values().filter { it != word }.random()
    return Question(word, ink, Mode.values().random(), Random.nextBoolean())
}

@Composable
fun ColorWordScreen() {
    var state by rememberSaveable { mutableStateOf(GameState.INITIAL) }
    var countdownText by rememberSaveable { mutableStateOf("3") }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var strikes by rememberSaveable { mutableIntStateOf(0) }
    var best by rememberSaveable { mutableIntStateOf(0) }
    var question by rememberSaveable { mutableStateOf(newQuestion()) }
    var questionId by rememberSaveable { mutableIntStateOf(0) }
    var timeLeft by rememberSaveable { mutableLongStateOf(TIME_LIMIT_MS) }

    fun nextQuestion() { question = newQuestion(); questionId++ }

    fun registerStrike() {
        strikes++
        if (strikes >= MAX_STRIKES) {
            if (score > best) best = score
            state = GameState.GAME_OVER
        } else nextQuestion()
    }

    fun onAnswer(choice: Choice) {
        val correct = if (question.mode == Mode.COLOR) Choice.INK else Choice.WORD
        if (choice == correct) { score++; nextQuestion() } else registerStrike()
    }

    LaunchedEffect(state) {
        if (state == GameState.COUNTDOWN) {
            score = 0; strikes = 0
            for (n in 3 downTo 1) { countdownText = n.toString(); delay(1000) }
            countdownText = "Start!"; delay(700)
            nextQuestion()
            state = GameState.RUNNING
        }
    }

    LaunchedEffect(questionId, state) {
        if (state == GameState.RUNNING) {
            timeLeft = TIME_LIMIT_MS
            val start = SystemClock.elapsedRealtime()
            while (timeLeft > 0) {
                delay(50)
                timeLeft = (TIME_LIMIT_MS - (SystemClock.elapsedRealtime() - start)).coerceAtLeast(0)
            }
            registerStrike()
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFFFAFAFA)).systemBarsPadding().padding(24.dp)) {
        when (state) {
            GameState.INITIAL -> Column(
                Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Welcome\nto\nColor Word Matching", fontSize = 24.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))
                FilledTonalButton(onClick = { state = GameState.COUNTDOWN }) { Text("Start Game") }
            }

            GameState.COUNTDOWN -> Text(countdownText, fontSize = 32.sp, modifier = Modifier.align(Alignment.Center))

            GameState.RUNNING -> {
                val q = question
                val seconds = (timeLeft + 999) / 1000
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Mode: ${q.mode.name}", fontSize = 14.sp)
                        Text("✅ $score  ❌ $strikes/$MAX_STRIKES", fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    Surface(shape = MaterialTheme.shapes.medium, color = Color(0xFFE3EDF2)) {
                        Text(
                            if (q.mode == Mode.COLOR) "Pilih WARNA TINTA" else "Pilih NAMA KATA",
                            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    LinearProgressIndicator(progress = { timeLeft / TIME_LIMIT_MS.toFloat() }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Text("$seconds s", fontSize = 20.sp)
                    Spacer(Modifier.height(24.dp))
                    Text(q.word.label, color = q.ink.color, fontSize = 48.sp)
                    Spacer(Modifier.weight(1f))
                    val left = if (q.inkOnLeft) Choice.INK else Choice.WORD
                    val right = if (q.inkOnLeft) Choice.WORD else Choice.INK
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                        listOf(left, right).forEach { c ->
                            FilledTonalButton(onClick = { onAnswer(c) }) {
                                Text(if (c == Choice.INK) q.ink.label else q.word.label)
                            }
                        }
                    }
                    Spacer(Modifier.height(80.dp))
                }
            }

            GameState.GAME_OVER -> Column(
                Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Game Over!", fontSize = 30.sp)
                Spacer(Modifier.height(24.dp))
                Text("Your Score", fontSize = 18.sp)
                Text("$score", fontSize = 28.sp)
                Spacer(Modifier.height(8.dp))
                Text("Best Score", fontSize = 14.sp)
                Text("$best", fontSize = 18.sp)
                Spacer(Modifier.height(24.dp))
                FilledTonalButton(onClick = { state = GameState.COUNTDOWN }) { Text("Restart Game") }
                FilledTonalButton(onClick = { state = GameState.INITIAL }) { Text("Exit") }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ColorWordScreenPreview() { ColorWordScreen() }