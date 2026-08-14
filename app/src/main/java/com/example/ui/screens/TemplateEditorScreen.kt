package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrintTemplate
import com.example.ui.components.FourDotsLogo
import com.example.ui.components.SmartTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateEditorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val templates by viewModel.templates.collectAsState()
    var currentTemplate by remember {
        mutableStateOf(
            templates.firstOrNull() ?: PrintTemplate()
        )
    }

    // Interactive Drag Offsets for variables inside card canvas
    var userOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var passOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var snOffset by remember { mutableStateOf(Offset(0f, 0f)) }

    var cardsPerPage by remember { mutableStateOf(currentTemplate.cardsPerPage.toString()) }
    var colsPerPage by remember { mutableStateOf(currentTemplate.columnsPerPage.toString()) }
    var userLen by remember { mutableStateOf(currentTemplate.usernameLength.toString()) }
    var passLen by remember { mutableStateOf(currentTemplate.passwordLength.toString()) }

    var cardFontSize by remember { mutableStateOf(currentTemplate.cardFontSizeSp.toString()) }
    var passFontSize by remember { mutableStateOf(currentTemplate.passwordFontSizeSp.toString()) }
    var snFontSize by remember { mutableStateOf(currentTemplate.serialFontSizeSp.toString()) }

    var showProfile by remember { mutableStateOf(currentTemplate.showProfile) }
    var showPassword by remember { mutableStateOf(currentTemplate.showPassword) }
    var showSerial by remember { mutableStateOf(currentTemplate.showSerial) }
    var showUsername by remember { mutableStateOf(currentTemplate.showUsername) }
    var showPrice by remember { mutableStateOf(currentTemplate.showPrice) }
    var showQrCode by remember { mutableStateOf(currentTemplate.showQrCode) }

    var selectedBgTheme by remember { mutableStateOf(0) } // 0: Blue/Gold, 1: Cyan/Navy, 2: Emerald/Yellow

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = "إدارة وتعديل القوالب",
                showBack = true,
                onBackClick = { viewModel.navigateTo(AppScreen.MAIN_DASHBOARD) }
            )
        },
        containerColor = DarkNavyBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Action Bar: حذف | صورة / سمة | جديد | حفظ
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            selectedBgTheme = (selectedBgTheme + 1) % 3
                            viewModel.showNotification("تم تغيير سمة الخلفية للقالب")
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = DarkNavyInput),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = SmartCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("صورة / سمة", fontSize = 12.sp, color = Color.White)
                    }

                    FilledTonalButton(
                        onClick = {
                            userOffset = Offset.Zero
                            passOffset = Offset.Zero
                            snOffset = Offset.Zero
                            viewModel.showNotification("تم إنشاء قالب جديد فارغ")
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = DarkNavyInput),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = SmartGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("جديد", fontSize = 12.sp, color = Color.White)
                    }

                    Button(
                        onClick = {
                            val updated = currentTemplate.copy(
                                showProfile = showProfile,
                                showPassword = showPassword,
                                showSerial = showSerial,
                                showUsername = showUsername,
                                showPrice = showPrice,
                                showQrCode = showQrCode,
                                cardsPerPage = cardsPerPage.toIntOrNull() ?: 10,
                                columnsPerPage = colsPerPage.toIntOrNull() ?: 2,
                                usernameLength = userLen.toIntOrNull() ?: 6,
                                passwordLength = passLen.toIntOrNull() ?: 6,
                                cardFontSizeSp = cardFontSize.toIntOrNull() ?: 13,
                                passwordFontSizeSp = passFontSize.toIntOrNull() ?: 14,
                                serialFontSizeSp = snFontSize.toIntOrNull() ?: 10
                            )
                            viewModel.saveTemplate(updated)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SmartBlue),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حفظ القالب", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Checkbox Elements Toggle Row (Matching Prompt: [ ] الباقة [ ] كلمة سر [ ] سريال [ ] اسم مستخدم [ ] السعر [ ] QR)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "خيارات إظهار عناصر الكرت",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SmartCyanLight,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = showProfile,
                                onCheckedChange = { showProfile = it },
                                colors = CheckboxDefaults.colors(checkedColor = SmartCyan)
                            )
                            Text("الباقة", color = Color.White, fontSize = 12.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = showPassword,
                                onCheckedChange = { showPassword = it },
                                colors = CheckboxDefaults.colors(checkedColor = SmartCyan)
                            )
                            Text("كلمة سر", color = Color.White, fontSize = 12.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = showSerial,
                                onCheckedChange = { showSerial = it },
                                colors = CheckboxDefaults.colors(checkedColor = SmartCyan)
                            )
                            Text("سريال (SN)", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = showUsername,
                                onCheckedChange = { showUsername = it },
                                colors = CheckboxDefaults.colors(checkedColor = SmartCyan)
                            )
                            Text("اسم مستخدم", color = Color.White, fontSize = 12.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = showPrice,
                                onCheckedChange = { showPrice = it },
                                colors = CheckboxDefaults.colors(checkedColor = SmartCyan)
                            )
                            Text("السعر", color = Color.White, fontSize = 12.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = showQrCode,
                                onCheckedChange = { showQrCode = it },
                                colors = CheckboxDefaults.colors(checkedColor = SmartCyan)
                            )
                            Text("رمز QR", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Interactive Drag & Drop Canvas Card Preview
            Text(
                text = "معاينة القالب التفاعلي (يمكن سحب النصوص لتعديل موضعها)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SmartYellowLight
            )

            val gradientBrush = when (selectedBgTheme) {
                1 -> Brush.horizontalGradient(listOf(Color(0xFF0F172A), Color(0xFF0891B2)))
                2 -> Brush.horizontalGradient(listOf(Color(0xFF064E3B), Color(0xFF059669)))
                else -> Brush.horizontalGradient(listOf(SmartBlueDark, SmartBlue))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(gradientBrush)
                    .border(2.dp, SmartYellowLight.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                // Background Watermark / Logo
                Column(
                    modifier = Modifier.align(Alignment.TopEnd),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Smart Creator WiFi",
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FourDotsLogo(size = 24)
                    }
                    if (showProfile) {
                        Text(
                            text = "باقة 3 ساعات (1GB)",
                            color = SmartYellowLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = (cardFontSize.toIntOrNull() ?: 13).sp
                        )
                    }
                    if (showPrice) {
                        Text(
                            text = "200 ريال",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }

                // QR Code Area
                if (showQrCode) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.BottomStart)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            tint = Color.Black,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Draggable Username Box
                if (showUsername) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(userOffset.x.roundToInt(), userOffset.y.roundToInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    userOffset += dragAmount
                                }
                            }
                            .align(Alignment.CenterStart)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "المستخدم: ", fontSize = 12.sp, color = TextSecondaryDark)
                            Text(
                                text = "748921",
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = (passFontSize.toIntOrNull() ?: 14).sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Draggable Password Box
                if (showPassword) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(passOffset.x.roundToInt(), passOffset.y.roundToInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    passOffset += dragAmount
                                }
                            }
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "كلمة السر: ", fontSize = 12.sp, color = TextSecondaryDark)
                            Text(
                                text = "748",
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = (passFontSize.toIntOrNull() ?: 14).sp,
                                color = SmartYellowLight
                            )
                        }
                    }
                }

                // Draggable Serial Box
                if (showSerial) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(snOffset.x.roundToInt(), snOffset.y.roundToInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    snOffset += dragAmount
                                }
                            }
                            .align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = "SN: 2026-89412",
                            fontSize = (snFontSize.toIntOrNull() ?: 10).sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Inputs Grid (Matching prompt specification)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "شبكة مدخلات الأرقام والتنسيق (Inputs Grid)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SmartCyanLight
                    )

                    // Row 1: عدد الكروت بالصفحة | عدد الأعمدة بالصفحة | طول اسم المستخدم | طول كلمة السر
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = cardsPerPage,
                            onValueChange = { cardsPerPage = it },
                            label = { Text("كروت/صفحة", fontSize = 10.sp, color = TextSecondaryDark) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = colsPerPage,
                            onValueChange = { colsPerPage = it },
                            label = { Text("أعمدة/صفحة", fontSize = 10.sp, color = TextSecondaryDark) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = userLen,
                            onValueChange = { userLen = it },
                            label = { Text("طول الاسم", fontSize = 10.sp, color = TextSecondaryDark) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = passLen,
                            onValueChange = { passLen = it },
                            label = { Text("طول السر", fontSize = 10.sp, color = TextSecondaryDark) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Row 2: حجم خط الكرت | حجم خط كلمة المرور | حجم SN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = cardFontSize,
                            onValueChange = { cardFontSize = it },
                            label = { Text("حجم خط الكرت", fontSize = 11.sp, color = TextSecondaryDark) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = passFontSize,
                            onValueChange = { passFontSize = it },
                            label = { Text("حجم خط السر", fontSize = 11.sp, color = TextSecondaryDark) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = snFontSize,
                            onValueChange = { snFontSize = it },
                            label = { Text("حجم خط SN", fontSize = 11.sp, color = TextSecondaryDark) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }
    }
}
