package com.example.week3
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkBg = Color(0xFF0F1426)
private val CardBg = Color(0xFF1B2236)
private val CardBorder = Color(0xFF2C3650)
private val Accent = Color(0xFF5BB5F0)
private val LabelGrey = Color(0xFF9AA6C2)
private const val DEFAULT_SPOT = "Menara Eiffel Kearifan Lokal"
private val EXPECTATION_OPTIONS = listOf("Kurang", "Sesuai", "Melebihi ★")

@Composable
fun TravelJournalScreen() {
    val context = LocalContext.current

    var spot by rememberSaveable { mutableStateOf(DEFAULT_SPOT) }
    var enjoyed by rememberSaveable { mutableStateOf("") }
    var goBack by rememberSaveable { mutableStateOf("") }
    var expectation by rememberSaveable { mutableIntStateOf(2) }
    var saved by rememberSaveable { mutableStateOf(true) }

    Column(
        Modifier.fillMaxSize().background(DarkBg).systemBarsPadding().imePadding().padding(16.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(170.dp).clip(RoundedCornerShape(16.dp))) {
            Image(
                painter = painterResource(R.drawable.eifel_probolinggo),
                contentDescription = DEFAULT_SPOT,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier.fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xE60F1426))))
            )
            Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                Text("NGLENCER SEK LE!", color = Color(0xFF9EC5FF), fontSize = 11.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(DEFAULT_SPOT, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Kota Probolinggo, Jawa Timur  •  ★★★★★ 5.0", color = Color(0xFFD6E4FF), fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(14.dp))

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            JournalField(
                label = "SPOT WISATA FAVORIT", value = spot,
                onChange = { spot = it; saved = false },
                placeholder = "Nama spot wisata", singleLine = true
            )
            JournalField(
                label = "APA YANG PALING KAMU NIKMATI?", value = enjoyed,
                onChange = { enjoyed = it; saved = false },
                placeholder = "Ketik pengalamanmu di sini...", minLines = 3
            )
            JournalField(
                label = "WOULD YOU GO BACK HERE? / CATATAN TAMBAHAN", value = goBack,
                onChange = { goBack = it; saved = false },
                placeholder = "Ya / tidak, kenapa? Tambahkan catatan di sini...", minLines = 2
            )

            Text("APAKAH MEMENUHI EKSPEKTASIMU?", color = LabelGrey, fontSize = 11.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EXPECTATION_OPTIONS.forEachIndexed { i, text ->
                    val selected = expectation == i
                    val shape = RoundedCornerShape(50)
                    Box(
                        Modifier.clip(shape)
                            .background(if (selected) Accent else Color.Transparent)
                            .border(1.dp, if (selected) Accent else CardBorder, shape)
                            .clickable { expectation = i; saved = false }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text, fontSize = 13.sp,
                            color = if (selected) DarkBg else LabelGrey,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Row(Modifier.fillMaxWidth().padding(top = 8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Row {
                Text("Status: ", color = LabelGrey, fontSize = 13.sp)
                Text(
                    if (saved) "Draft Tersimpan" else "Belum Disimpan",
                    color = Accent, fontSize = 13.sp, fontWeight = FontWeight.Bold
                )
            }
            FloatingActionButton(
                onClick = {
                    saved = true
                    val name = spot.ifBlank { DEFAULT_SPOT }
                    Toast.makeText(
                        context,
                        "Jurnal \"$name\" berhasil disimpan! (${EXPECTATION_OPTIONS[expectation]})",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                containerColor = Accent,
                contentColor = DarkBg,
                shape = RoundedCornerShape(16.dp)
            ) { Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun JournalField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = false,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else minLines,
        shape = RoundedCornerShape(12.dp),
        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) },
        placeholder = { Text(placeholder, color = Color(0xFF5E6A87), fontSize = 14.sp) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CardBg,
            unfocusedContainerColor = CardBg,
            focusedBorderColor = Accent,
            unfocusedBorderColor = CardBorder,
            focusedLabelColor = Accent,
            unfocusedLabelColor = LabelGrey,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Accent
        )
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun previewTravel(){
    TravelJournalScreen()
}