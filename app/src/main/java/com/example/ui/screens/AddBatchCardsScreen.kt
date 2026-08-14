package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NetworkType
import com.example.ui.components.SmartTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBatchCardsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentType by viewModel.currentNetworkType.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    val templates by viewModel.templates.collectAsState()
    val filteredProfiles = profiles.filter { it.type == currentType }

    var totalCount by remember { mutableStateOf("50") }
    var selectedProfile by remember {
        mutableStateOf(filteredProfiles.firstOrNull()?.name ?: "3_Hours_1GB")
    }
    var userOnlyDigits by remember { mutableStateOf(true) }
    var passOnlyDigits by remember { mutableStateOf(true) }
    var userLength by remember { mutableStateOf("6") }
    var passLength by remember { mutableStateOf("4") }
    var prefix by remember { mutableStateOf("") }
    var suffix by remember { mutableStateOf("") }
    var validityDays by remember { mutableStateOf("2") }
    var allowedHours by remember { mutableStateOf("3") }
    var price by remember { mutableStateOf("200") }
    var downloadMb by remember { mutableStateOf("1024") }
    var selectedServer by remember { mutableStateOf("all") }
    var addComment by remember { mutableStateOf(true) }
    var bindFirstDevice by remember { mutableStateOf(false) }

    val isUserManager = currentType == NetworkType.USER_MANAGER
    val screenTitle = if (isUserManager) "توليد دفعة كروت يوزرمانجر" else "توليد مجموعة كروت هوتسبوت"

    // Sync profile values when selected profile changes
    LaunchedEffect(selectedProfile) {
        val prof = profiles.find { it.name == selectedProfile }
        prof?.let {
            validityDays = it.validityDays.toString()
            allowedHours = it.allowedTimeHours.toString()
            price = it.price.toInt().toString()
            downloadMb = it.downloadLimitMb.toString()
        }
    }

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = screenTitle,
                showBack = true,
                onBackClick = {
                    val back = if (isUserManager) AppScreen.USER_MANAGER_MENU else AppScreen.HOTSPOT_MENU
                    viewModel.navigateTo(back)
                }
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
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "إعدادات الدفعة والتوليد",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SmartCyanLight
                    )

                    // Row 1: Total cards count & Profile
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = totalCount,
                            onValueChange = { totalCount = it },
                            label = { Text("إجمالي الكروت", color = TextSecondaryDark) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("batch_count_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Profile Dropdown
                        var profExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = profExpanded,
                            onExpandedChange = { profExpanded = !profExpanded },
                            modifier = Modifier.weight(1.4f)
                        ) {
                            OutlinedTextField(
                                value = selectedProfile,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("الباقة", color = TextSecondaryDark) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = profExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SmartCyan,
                                    unfocusedBorderColor = DarkNavyBorder,
                                    focusedContainerColor = DarkNavyInput,
                                    unfocusedContainerColor = DarkNavyInput,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = profExpanded,
                                onDismissRequest = { profExpanded = false }
                            ) {
                                (if (filteredProfiles.isNotEmpty()) filteredProfiles else profiles).forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text("${p.name} (${p.price} ريال)") },
                                        onClick = {
                                            selectedProfile = p.name
                                            profExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Row 2: User Pattern & Pass Pattern toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = userOnlyDigits,
                            onClick = { userOnlyDigits = !userOnlyDigits },
                            label = { Text(if (userOnlyDigits) "الاسم: أرقام فقط" else "الاسم: أرقام وحروف", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SmartCyanDark,
                                selectedLabelColor = Color.White,
                                containerColor = DarkNavyInput,
                                labelColor = TextSecondaryDark
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = passOnlyDigits,
                            onClick = { passOnlyDigits = !passOnlyDigits },
                            label = { Text(if (passOnlyDigits) "السر: أرقام فقط" else "السر: أرقام وحروف", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SmartCyanDark,
                                selectedLabelColor = Color.White,
                                containerColor = DarkNavyInput,
                                labelColor = TextSecondaryDark
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 3: Username Length & Password Length
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = userLength,
                            onValueChange = { userLength = it },
                            label = { Text("طول اسم المستخدم", color = TextSecondaryDark) },
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
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = passLength,
                            onValueChange = { passLength = it },
                            label = { Text("طول كلمة السر", color = TextSecondaryDark) },
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
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Row 4: Prefix & Suffix
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = prefix,
                            onValueChange = { prefix = it },
                            label = { Text("البادئة (Prefix)", color = TextSecondaryDark) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = suffix,
                            onValueChange = { suffix = it },
                            label = { Text("لاحقة (Suffix)", color = TextSecondaryDark) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Row 5: Validity, Allowed Hours, Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = validityDays,
                            onValueChange = { validityDays = it },
                            label = { Text("الصلاحية (أيام)", color = TextSecondaryDark, fontSize = 11.sp) },
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
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = allowedHours,
                            onValueChange = { allowedHours = it },
                            label = { Text("الوقت (ساعة)", color = TextSecondaryDark, fontSize = 11.sp) },
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
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("السعر (ريال)", color = TextSecondaryDark, fontSize = 11.sp) },
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
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Row 6: Download limit & Server
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = downloadMb,
                            onValueChange = { downloadMb = it },
                            label = { Text("التحميل (MiB)", color = TextSecondaryDark) },
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
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = selectedServer,
                            onValueChange = { selectedServer = it },
                            label = { Text("السيرفر", color = TextSecondaryDark) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Checkboxes: Validity comment & Bind device
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = addComment,
                            onCheckedChange = { addComment = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = SmartCyan,
                                uncheckedColor = DarkNavyBorder
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "إضافة كومنت الصلاحية والوقت تلقائياً",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = bindFirstDevice,
                            onCheckedChange = { bindFirstDevice = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = SmartCyan,
                                uncheckedColor = DarkNavyBorder
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ربط الكرت بأول جهاز يستخدمه (MAC Lock)",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Dual Action Buttons: إنشاء وطباعة / معاينة الطباعة
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val count = totalCount.toIntOrNull() ?: 10
                        viewModel.generateBatchVouchers(
                            count = count,
                            profileName = selectedProfile,
                            userPatternDigits = userOnlyDigits,
                            passPatternDigits = passOnlyDigits,
                            userLen = userLength.toIntOrNull() ?: 6,
                            passLen = passLength.toIntOrNull() ?: 4,
                            prefix = prefix.trim(),
                            suffix = suffix.trim(),
                            price = price.toDoubleOrNull() ?: 200.0,
                            validityDays = validityDays.toIntOrNull() ?: 1,
                            allowedHours = allowedHours.toIntOrNull() ?: 24,
                            downloadLimitMb = downloadMb.toLongOrNull() ?: 1024L,
                            server = selectedServer.trim(),
                            addValidityComment = addComment,
                            bindFirstDevice = bindFirstDevice,
                            andPrint = true
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SmartBlue),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(52.dp)
                        .testTag("create_and_print_batch")
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إنشاء وطباعة", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                FilledTonalButton(
                    onClick = {
                        val count = totalCount.toIntOrNull() ?: 10
                        viewModel.generateBatchVouchers(
                            count = count,
                            profileName = selectedProfile,
                            userPatternDigits = userOnlyDigits,
                            passPatternDigits = passOnlyDigits,
                            userLen = userLength.toIntOrNull() ?: 6,
                            passLen = passLength.toIntOrNull() ?: 4,
                            prefix = prefix.trim(),
                            suffix = suffix.trim(),
                            price = price.toDoubleOrNull() ?: 200.0,
                            validityDays = validityDays.toIntOrNull() ?: 1,
                            allowedHours = allowedHours.toIntOrNull() ?: 24,
                            downloadLimitMb = downloadMb.toLongOrNull() ?: 1024L,
                            server = selectedServer.trim(),
                            addValidityComment = addComment,
                            bindFirstDevice = bindFirstDevice,
                            andPrint = false
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = SmartCyanDark),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("create_batch_only")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إنشاء الكروت", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
