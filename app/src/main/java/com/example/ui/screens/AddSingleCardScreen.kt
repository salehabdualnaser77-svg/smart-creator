package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NetworkType
import com.example.ui.components.SmartTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSingleCardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentType by viewModel.currentNetworkType.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    val filteredProfiles = profiles.filter { it.type == currentType }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedServer by remember { mutableStateOf("all") }
    var selectedProfile by remember {
        mutableStateOf(filteredProfiles.firstOrNull()?.name ?: "1_Hour_500MB")
    }
    var customerComment by remember { mutableStateOf("") }
    var bindFirstDevice by remember { mutableStateOf(false) }

    val isUserManager = currentType == NetworkType.USER_MANAGER
    val screenTitle = if (isUserManager) "إضافة مستخدم يوزرمانجر (VIP)" else "إضافة كرت هوتسبوت فردي"

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = screenTitle,
                showBack = true,
                onBackClick = {
                    val backScreen = if (isUserManager) AppScreen.USER_MANAGER_MENU else AppScreen.HOTSPOT_MENU
                    viewModel.navigateTo(backScreen)
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
                        text = "بيانات المستخدم",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SmartCyanLight
                    )

                    // Username Input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("اسم المستخدم:-", color = TextSecondaryDark) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SmartCyan) },
                        trailingIcon = {
                            IconButton(onClick = {
                                username = "user_" + Random.nextInt(1000, 9999)
                            }) {
                                Icon(Icons.Default.Shuffle, contentDescription = "توليد عشوائي", tint = SmartYellow)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("single_username_input"),
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

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("كلمة المرور:-", color = TextSecondaryDark) },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = SmartCyan) },
                        trailingIcon = {
                            IconButton(onClick = {
                                password = Random.nextInt(1000, 9999).toString()
                            }) {
                                Icon(Icons.Default.Casino, contentDescription = "توليد باسوورد", tint = SmartYellow)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("single_password_input"),
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

                    // Server Selection
                    var serverExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = serverExpanded,
                        onExpandedChange = { serverExpanded = !serverExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedServer,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("السيرفر", color = TextSecondaryDark) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serverExpanded) },
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
                            expanded = serverExpanded,
                            onDismissRequest = { serverExpanded = false }
                        ) {
                            listOf("all", "hotspot1", "hotspot2", "default").forEach { srv ->
                                DropdownMenuItem(
                                    text = { Text(srv) },
                                    onClick = {
                                        selectedServer = srv
                                        serverExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Profile Selection
                    var profileExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = profileExpanded,
                        onExpandedChange = { profileExpanded = !profileExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedProfile,
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(
                                    if (isUserManager) "باقات اليوزرمانجر" else "باقة Hotspot",
                                    color = TextSecondaryDark
                                )
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = profileExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("profile_selector"),
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
                            expanded = profileExpanded,
                            onDismissRequest = { profileExpanded = false }
                        ) {
                            (if (filteredProfiles.isNotEmpty()) filteredProfiles else profiles).forEach { prof ->
                                DropdownMenuItem(
                                    text = { Text("${prof.name} - ${prof.price} ريال (${prof.validityDays} يوم)") },
                                    onClick = {
                                        selectedProfile = prof.name
                                        profileExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Customer / Comment
                    OutlinedTextField(
                        value = customerComment,
                        onValueChange = { customerComment = it },
                        label = { Text("العميل / الملاحظات", color = TextSecondaryDark) },
                        leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = SmartCyan) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
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

                    // Bind to first MAC Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = bindFirstDevice,
                            onCheckedChange = { bindFirstDevice = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = SmartCyan,
                                uncheckedColor = DarkNavyBorder
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ربط بأول جهاز يستخدمه (قفل الماك)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (username.isBlank()) {
                        viewModel.showNotification("يرجى إدخال اسم المستخدم", isError = true)
                        return@Button
                    }
                    viewModel.addSingleVoucher(
                        username = username.trim(),
                        pass = if (password.isBlank()) username.trim() else password.trim(),
                        profileName = selectedProfile,
                        server = selectedServer,
                        comment = customerComment,
                        boundMac = bindFirstDevice
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SmartBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_add_single_card")
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إضافة الكرت",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}
