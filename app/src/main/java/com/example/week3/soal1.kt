package com.example.week3

import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToLong
import kotlin.random.Random

enum class ReactionState { INITIAL, WAITING, READY, TRIAL_DONE, FAIL, FINAL }

enum class ReactionCategory(val message: String, val color: Color, @DrawableRes val image: Int) {
    FAST("DANG YOU ARE SO FAST BRO!", Color(0xFF00E676), R.drawable.suangaar),
    GOOD("YOUR REFLEX IS GOOD", Color(0xFF2196F3), R.drawable.mantap),
    AVERAGE("MEH LIKE OTHER PERSON", Color(0xFFFF9800), R.drawable.kureng),
    SNAIL("YOU LIKE A SNAIL BRO", Color(0xFFFF5722), R.drawable.cupu);

    companion object {
        fun from(avgMs: Long) = when {
            avgMs < 180 -> FAST
            avgMs < 280 -> GOOD
            avgMs < 450 -> AVERAGE
            else -> SNAIL
        }
    }
}

@Composable
fun ReactionTestScreen() {
    var state by rememberSaveable { mutableStateOf(ReactionState.INITIAL) }
    var trialIndex by rememberSaveable { mutableIntStateOf(0) }
    var results by rememberSaveable { mutableStateOf(List<Long?>(3) { null }) }
    var startTime by rememberSaveable { mutableLongStateOf(0L) }
    var lastTime by rememberSaveable { mutableLongStateOf(0L) }

    LaunchedEffect(state, trialIndex) {
        if (state == ReactionState.WAITING) {
            delay(Random.nextLong(500L, 4501L))
            startTime = SystemClock.elapsedRealtime()
            state = ReactionState.READY
        }
    }

    val valid = results.filterNotNull()
    val average: Long? = if (valid.isNotEmpty()) valid.average().roundToLong() else null
    val category = average?.let { ReactionCategory.from(it) }

    fun onTap() {
        when (state) {
            ReactionState.INITIAL -> state = ReactionState.WAITING
            ReactionState.WAITING -> state = ReactionState.FAIL
            ReactionState.READY -> {
                val t = SystemClock.elapsedRealtime() - startTime
                lastTime = t
                results = results.toMutableList().also { it[trialIndex] = t }
                state = if (trialIndex >= 2) ReactionState.FINAL else ReactionState.TRIAL_DONE
            }
            ReactionState.TRIAL_DONE -> { trialIndex++; state = ReactionState.WAITING }
            ReactionState.FAIL -> {
                if (trialIndex >= 2) state = ReactionState.FINAL
                else { trialIndex++; state = ReactionState.INITIAL }
            }
            ReactionState.FINAL -> {
                results = List(3) { null }
                trialIndex = 0
                state = ReactionState.INITIAL
            }
        }
    }

    val bg = when (state) {
        ReactionState.INITIAL -> Color(0xFF6ECFE0)
        ReactionState.WAITING -> Color(0xFFD6D6D6)
        ReactionState.READY, ReactionState.TRIAL_DONE -> Color(0xFF4CAF50)
        ReactionState.FAIL -> Color(0xFFF94144)
        ReactionState.FINAL -> category?.color ?: Color(0xFFF94144)
    }

    Box(
        Modifier.fillMaxSize().background(bg)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onTap() }
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            when (state) {
                ReactionState.INITIAL -> {
                    Title("Reaction"); BigIcon(Icons.Filled.Bolt)
                    Body("Test"); Small("Click to Start")
                    if (trialIndex > 0) ResultsCard(results, null)
                }
                ReactionState.WAITING -> {
                    Title("Get Ready"); BigIcon(Icons.Filled.Warning)
                    Body("Wait for green light..."); Small("DON'T CLICK YET!")
                }
                ReactionState.READY -> {
                    Title("GO!"); BigIcon(Icons.AutoMirrored.Filled.DirectionsRun)
                    Body("CLICK NOW!"); Small("TAP AS FAST AS YOU CAN!")
                }
                ReactionState.TRIAL_DONE -> {
                    Title("Trial ${trialIndex + 1} Complete!")
                    Box(Modifier.size(96.dp).clip(CircleShape).background(Color.White), Alignment.Center) {
                        Icon(Icons.Filled.Check, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(64.dp))
                    }
                    Spacer(Modifier.height(24.dp))
                    Body("Time: ${lastTime}ms"); Small("Continue to Trial ${trialIndex + 2}")
                    ResultsCard(results, null)
                }
                ReactionState.FAIL -> {
                    Title("FAIL!"); BigIcon(Icons.Filled.ThumbDown)
                    Body("You clicked too early, TRY TO READ THE RULE BRO")
                    Small(if (trialIndex >= 2) "SEE RESULT" else "TRY AGAIN")
                    ResultsCard(results, null)
                }
                ReactionState.FINAL -> {
                    val shown = category ?: ReactionCategory.SNAIL
                    Title(if (category != null) shown.message else "NO VALID TRIAL, READ THE RULE BRO!")
                    Image(
                        painterResource(shown.image), contentDescription = shown.message,
                        modifier = Modifier.size(130.dp).clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(Modifier.height(24.dp))
                    Body(if (average != null) "Average: ${average}ms" else "Average: -")
                    Small("Click to Start New Test")
                    ResultsCard(results, average)
                }
            }
        }
    }
}

@Composable private fun Title(t: String) {
    Text(t, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    Spacer(Modifier.height(24.dp))
}

@Composable private fun BigIcon(icon: ImageVector) {
    Icon(icon, null, tint = Color.White, modifier = Modifier.size(96.dp))
    Spacer(Modifier.height(24.dp))
}

@Composable private fun Body(t: String) {
    Text(t, color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center)
    Spacer(Modifier.height(8.dp))
}

@Composable private fun Small(t: String) {
    Text(t, color = Color.White, fontSize = 13.sp, textAlign = TextAlign.Center)
    Spacer(Modifier.height(28.dp))
}

@Composable
private fun ResultsCard(results: List<Long?>, average: Long?) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Trial Results", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                results.forEachIndexed { i, v ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${i + 1}", fontWeight = FontWeight.Bold, fontSize = 14.sp,
                            color = if (v != null) Color(0xFF4CAF50) else Color.Gray)
                        Text(if (v != null) "${v}ms" else "-", fontSize = 12.sp, color = Color.Black)
                    }
                }
            }
            if (average != null) {
                Spacer(Modifier.height(10.dp))
                Text("Average Score", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${average}ms", color = Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReactionTestScreenPreview() { ReactionTestScreen() }