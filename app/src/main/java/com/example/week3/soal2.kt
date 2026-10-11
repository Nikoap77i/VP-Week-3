package com.example.week3

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun ErvanClickerScreen() {
    var coins by rememberSaveable { mutableLongStateOf(0L) }
    var value by rememberSaveable { mutableDoubleStateOf(1.0) }
    var cost by rememberSaveable { mutableLongStateOf(10L) }
    var tapCount by remember { mutableIntStateOf(0) }
    var eat by remember { mutableStateOf(false) }

    val perTap = value.roundToInt().coerceAtLeast(1)
    val nextPerTap = (value * 1.5).roundToInt()

    LaunchedEffect(tapCount) {
        if (tapCount > 0) {
            eat = true
            delay(60)
            eat = false
        }
    }

    Box(Modifier.fillMaxSize()) {
        Image(
            painterResource(R.drawable.ervan_background), contentDescription = null,
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
        )
        Column(
            Modifier.fillMaxSize().systemBarsPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.35f))
            ) {
                Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("$coins", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 40.sp)
                    Text("$perTap coins per tap", color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.weight(1f))
            Text("Tap the Ervan!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(if (eat) R.drawable.ervan_gigit_pisang else R.drawable.ervan_megang_pisang),
                contentDescription = "Ervan",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(220.dp).clip(RoundedCornerShape(20.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        coins += perTap
                        tapCount++
                    }
            )
            Spacer(Modifier.height(6.dp))
            Text(if (eat) "YUMMMYY!" else "I'M HUNGRYYYY", color = Color.White, fontSize = 16.sp)
            Spacer(Modifier.weight(1f))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f))
            ) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Gimme Your Coinsss", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF424242))
                    Spacer(Modifier.height(6.dp))
                    Text("Next upgrade: +$nextPerTap coins per tap", fontSize = 14.sp, color = Color(0xFF757575))
                    Spacer(Modifier.height(12.dp))
                    val canBuy = coins >= cost
                    Button(
                        onClick = {
                            if (coins >= cost) {
                                coins -= cost
                                cost *= 2
                                value *= 1.5
                            }
                        },
                        enabled = canBuy,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            disabledContainerColor = Color(0xFFBDBDBD),
                            disabledContentColor = Color.White
                        )
                    ) {
                        Text(if (canBuy) "Pay for $cost coins" else "Find ${cost - coins} more coins", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun preview() {
    MaterialTheme {
        ErvanClickerScreen()
    }
}
