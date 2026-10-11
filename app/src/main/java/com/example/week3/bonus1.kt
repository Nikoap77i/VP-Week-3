package com.example.week3

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val CoffeeBg = Color(0xFFF8F3F0)
private val Brown = Color(0xFF4A1C0C)
private val Orange = Color(0xFFD9622B)
private val Peach = Color(0xFFF6D6BE)
private val Cream = Color(0xFFFBF0E4)

private fun rupiah(n: Int) = "Rp " + String.format(Locale.US, "%,d", n).replace(',', '.')

private fun Modifier.dashedBorder(color: Color, radius: Dp = 12.dp) = drawBehind {
    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(radius.toPx()),
        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f)))
    )
}

@Composable
fun CoffeeOrderScreen() {
    val context = LocalContext.current
    var quantity by rememberSaveable { mutableIntStateOf(1) }
    var large by rememberSaveable { mutableStateOf(false) }
    val unitPrice = 25_000 + if (large) 6_000 else 0
    val subtotal = unitPrice * quantity
    val tax = subtotal * 10 / 100
    val total = subtotal + tax

    Column(
        Modifier.fillMaxSize().background(CoffeeBg).systemBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("Ngopi Sek Bolo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Brown)
            Icon(Icons.Filled.Coffee, contentDescription = null, tint = Brown)
        }
        Spacer(Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Peach)
        ) {
            Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF6B3318), Color(0xFF8E3B1C)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Coffee, null, tint = Peach, modifier = Modifier.size(64.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text("Caramel Latte", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Brown)
                Text(
                    "Espresso shot, steamed milk & caramel syrup",
                    fontSize = 12.sp, color = Color(0xFF8E3B1C), textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text("Rp 25.000 / cup", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Orange)
            }
        }

        Spacer(Modifier.height(14.dp))
        Text("PILIHAN UKURAN:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8E3B1C))
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SizeChip("Regular (+0)", !large, Modifier.weight(1f)) { large = false }
            SizeChip("Large (+6k)", large, Modifier.weight(1f)) { large = true }
        }

        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Peach)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                Arrangement.SpaceBetween, Alignment.CenterVertically
            ) {
                Text("Jumlah Pesanan:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Brown)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    QtyButton("-", enabled = quantity > 1, filled = false) {
                        if (quantity > 1) quantity--
                    }
                    Text("$quantity", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Brown)
                    QtyButton("+", enabled = true, filled = true) { quantity++ }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Column(
            Modifier.fillMaxWidth().dashedBorder(Color(0xFFE8B78F))
                .clip(RoundedCornerShape(12.dp)).background(Cream).padding(14.dp)
        ) {
            SummaryRow("Subtotal:", rupiah(subtotal))
            Spacer(Modifier.height(4.dp))
            SummaryRow("Pajak Resto (10%):", rupiah(tax))
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Peach)
            SummaryRow("Total Pembayaran:", rupiah(total), bold = true)
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                val size = if (large) "Large" else "Regular"
                Toast.makeText(
                    context,
                    "$quantity x Caramel Latte ($size) ditambahkan ke keranjang. Total ${rupiah(total)}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange)
        ) { Text("TAMBAH KE KERANJANG", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun SizeChip(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier.clip(shape).background(if (selected) Color.White else Cream)
            .border(1.5.dp, if (selected) Orange else Peach, shape)
            .clickable(onClick = onClick).padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label, fontSize = 13.sp, color = if (selected) Orange else Brown,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun QtyButton(symbol: String, enabled: Boolean, filled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        Modifier.size(36.dp).clip(shape)
            .background(if (filled) Peach.copy(alpha = 0.6f) else Color.White)
            .border(1.dp, Color(0xFFD9DCE3), shape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            symbol, fontSize = 18.sp, fontWeight = FontWeight.Bold,
            color = when {
                !enabled -> Color(0xFFB0B0B0)
                filled -> Orange
                else -> Brown
            }
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label, fontSize = if (bold) 14.sp else 12.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, color = Brown)
        Text(value, fontSize = if (bold) 14.sp else 12.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, color = if (bold) Orange else Brown)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CoffeeOrderScreenPreview() { CoffeeOrderScreen() }