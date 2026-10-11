package com.example.week3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class Rps(val emoji: String, val label: String) {
    ROCK("✊", "Rock"), PAPER("✋", "Paper"), SCISSOR("✌️", "Scissor");

    fun beats(other: Rps) =
        (this == ROCK && other == SCISSOR) || (this == PAPER && other == ROCK) || (this == SCISSOR && other == PAPER)
}

enum class RpsState { INITIAL, PICK, REVEAL, FINISHED }
enum class RpsResult(val text: String) { WIN("You Win!"), LOSE("You Lose!"), DRAW("Draw") }

@Composable
fun RpsScreen() {
    var bestOf by rememberSaveable { mutableIntStateOf(5) }
    var state by rememberSaveable { mutableStateOf(RpsState.INITIAL) }
    var yourScore by rememberSaveable { mutableIntStateOf(0) }
    var cpuScore by rememberSaveable { mutableIntStateOf(0) }
    var best by rememberSaveable { mutableStateOf<Int?>(null) }
    var playerPick by rememberSaveable { mutableStateOf<Rps?>(null) }
    var cpuPick by rememberSaveable { mutableStateOf<Rps?>(null) }
    var result by rememberSaveable { mutableStateOf<RpsResult?>(null) }
    var orderSeed by rememberSaveable { mutableIntStateOf(Random.nextInt()) }

    val target = bestOf / 2 + 1
    val order = remember(orderSeed) { Rps.values().toList().shuffled(Random(orderSeed)) }

    fun pick(p: Rps) {
        val c = Rps.values().random()
        val r = when {
            p == c -> RpsResult.DRAW
            p.beats(c) -> RpsResult.WIN
            else -> RpsResult.LOSE
        }
        playerPick = p; cpuPick = c; result = r
        if (r == RpsResult.WIN) yourScore++ else if (r == RpsResult.LOSE) cpuScore++
        state = RpsState.REVEAL
    }

    fun resetMatch(next: RpsState) {
        yourScore = 0; cpuScore = 0
        playerPick = null; cpuPick = null; result = null
        orderSeed = Random.nextInt()
        state = next
    }

    LaunchedEffect(state) {
        if (state == RpsState.REVEAL) {
            delay(1000)
            if (yourScore >= target || cpuScore >= target) {
                val diff = yourScore - cpuScore
                val current = best
                if (current == null || diff > current) best = diff
                state = RpsState.FINISHED
            } else {
                playerPick = null; cpuPick = null; result = null
                orderSeed = Random.nextInt()
                state = RpsState.PICK
            }
        }
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFFAFAFA)).systemBarsPadding().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("🧑 $yourScore — $cpuScore 🤖", fontSize = 16.sp,
                modifier = Modifier.semantics { contentDescription = "Skor kamu $yourScore, skor komputer $cpuScore" })
            Text("Best of $bestOf", fontSize = 12.sp)
        }

        Column(Modifier.weight(1f).fillMaxWidth(), Arrangement.Center, Alignment.CenterHorizontally) {
            when (state) {
                RpsState.INITIAL -> {
                    Text("Rock ✊ Paper ✋ Scissors ✌️", fontSize = 22.sp)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(3, 5, 7).forEach { n ->
                            if (bestOf == n) Button(onClick = { bestOf = n }) { Text("Best of $n") }
                            else OutlinedButton(onClick = { bestOf = n }) { Text("Best of $n") }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    FilledTonalButton(onClick = { resetMatch(RpsState.PICK) }) { Text("Start") }
                }

                RpsState.PICK -> {
                    Text("Pick your move!", fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("❔ VS ❔", fontSize = 36.sp)
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        order.forEach { r ->
                            FilledTonalButton(
                                onClick = { pick(r) },
                                modifier = Modifier.weight(1f).semantics { contentDescription = "Pilih ${r.label}" },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp)
                            ) { Text("${r.emoji} ${r.label}", fontSize = 13.sp, maxLines = 1) }
                        }
                    }
                }

                RpsState.REVEAL -> {
                    Text(
                        "${playerPick?.emoji} VS ${cpuPick?.emoji}", fontSize = 36.sp,
                        modifier = Modifier.semantics {
                            contentDescription = "Kamu ${playerPick?.label} melawan komputer ${cpuPick?.label}"
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(result?.text ?: "", fontSize = 18.sp,
                        modifier = Modifier.semantics { contentDescription = "Hasil ronde: ${result?.text}" })
                }

                RpsState.FINISHED -> {
                    Text(if (yourScore > cpuScore) "You Won the Match!" else "You Lost the Match", fontSize = 22.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Best Score: ${best ?: "-"}", fontSize = 12.sp)
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        FilledTonalButton(onClick = { resetMatch(RpsState.PICK) }) { Text("Restart") }
                        FilledTonalButton(onClick = { resetMatch(RpsState.INITIAL) }) { Text("Exit") }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RpsScreenPreview() { RpsScreen() }