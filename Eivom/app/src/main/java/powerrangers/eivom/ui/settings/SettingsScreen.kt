package powerrangers.eivom.ui.settings

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch
import powerrangers.eivom.R
import powerrangers.eivom.ui.component.DrawerBody
import powerrangers.eivom.ui.component.DrawerHeader
import powerrangers.eivom.ui.component.TopBar

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateToMenuItem: (String) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                title = stringResource(id = R.string.app_name),
                onMenuIconClick = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerContent = {
            DrawerHeader()
            DrawerBody(onItemClick = navigateToMenuItem)
        }
    ) { innerPadding ->
        SettingsBody(modifier = modifier.padding(innerPadding))
    }
}

@Composable
fun SettingsBody(
    modifier: Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userPreferences by remember { viewModel.userPreferences }
    val producerState by remember { viewModel.producerState }
    val dateFormat by remember { viewModel.dateFormat }

    val coroutineScope = rememberCoroutineScope()

    val colorPickerFunction by remember { viewModel.colorPickerFunction }

    if (colorPickerFunction != null) {
        ColorPicker(
            onDismiss = {
                viewModel.clearColorPickerFunction()
            },
            onColorChanged = {
                (colorPickerFunction as (Long) -> Unit)(it)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Color Mode
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.color_mode_title)
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SelectButton(
                    text = stringResource(id = R.string.default_color_mode_option),
                    isSelected = !userPreferences.colorMode,
                    onSelect = {
                        viewModel.updateColorMode(false)
                    }
                )
                SelectButton(
                    text = stringResource(id = R.string.custom_color_mode_option),
                    isSelected = userPreferences.colorMode,
                    onSelect = {
                        viewModel.updateColorMode(true)
                    }
                )
            }
        }
        // Custom Color
        if (userPreferences.colorMode) {
            Text(text = stringResource(id = R.string.custom_color_option))
            // Topbar Background Color
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = userPreferences.topbarBackgroundColor,
                    contentColor = userPreferences.topbarTextColor
                ),
                onClick = {
                    viewModel.updateTopbarBackground()
                }
            ) {
                Text(text = stringResource(id = R.string.topbar_background_color))
            }
            // Sidebar Background Color
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = userPreferences.sidebarBackgroundColor,
                    contentColor = userPreferences.sidebarTextColor
                ),
                onClick = {
                    viewModel.updateSidebarBackground()
                }
            ) {
                Text(text = stringResource(id = R.string.sidebar_background_color))
            }
            // Screen Background Color
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = userPreferences.screenBackgroundColor,
                    contentColor = userPreferences.screenTextColor
                ),
                onClick = {
                    viewModel.updateScreenBackground()
                }
            ) {
                Text(text = stringResource(id = R.string.screen_background_color))
            }
            // Movie/Note Background Color
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = userPreferences.movieNoteBackgroundColor,
                    contentColor = userPreferences.movieNoteTextColor
                ),
                onClick = {
                    viewModel.updateMovieNoteBackground()
                }
            ) {
                Text(text = stringResource(id = R.string.movienote_background_color))
            }
            // Dialog Background Color
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = userPreferences.dialogBackgroundColor,
                    contentColor = userPreferences.dialogTextColor
                ),
                onClick = {
                    viewModel.updateDialogBackground()
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_background_color))
            }
            // Topbar Text Color
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = userPreferences.topbarBackgroundColor,
                    contentColor = userPreferences.topbarTextColor
                ),
                onClick = {
                    viewModel.updateTopbarText()
                }
            ) {
                Text(text = stringResource(id = R.string.topbar_text_color))
            }
            // Sidebar Text Color
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = userPreferences.sidebarBackgroundColor,
                    contentColor = userPreferences.sidebarTextColor
                ),
                onClick = {
                    viewModel.updateSidebarText()
                }
            ) {
                Text(text = stringResource(id = R.string.sidebar_text_color))
            }
            // Screen Text Color
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = userPreferences.screenBackgroundColor,
                    contentColor = userPreferences.screenTextColor
                ),
                onClick = {
                    viewModel.updateScreenText()
                }
            ) {
                Text(text = stringResource(id = R.string.screen_text_color))
            }
            // Movie/Note Text Color
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = userPreferences.movieNoteBackgroundColor,
                    contentColor = userPreferences.movieNoteTextColor
                ),
                onClick = {
                    viewModel.updateMovieNoteText()
                }
            ) {
                Text(text = stringResource(id = R.string.movienote_text_color))
            }
            // Dialog Text Color
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = userPreferences.dialogBackgroundColor,
                    contentColor = userPreferences.dialogTextColor
                ),
                onClick = {
                    viewModel.updateDialogText()
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_text_color))
            }
        }
        // Original Title Display
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.original_title_display_title)
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SelectButton(
                    text = stringResource(id = R.string.original_title_display_option),
                    isSelected = userPreferences.originalTitleDisplay,
                    onSelect = {
                        viewModel.updateOriginalTitleDisplay(true)
                    }
                )
                SelectButton(
                    text = stringResource(id = R.string.original_title_not_display_option),
                    isSelected = !userPreferences.originalTitleDisplay,
                    onSelect = {
                        viewModel.updateOriginalTitleDisplay(false)
                    }
                )
            }
        }
        // Date Format
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = dateFormat,
            onValueChange = {
                viewModel.updateDateFormat(it)
            },
            label = {
                Text(text = stringResource(id = R.string.date_format_title))
            },
            placeholder = {
                Text(text = stringResource(id = R.string.date_format_placeholder))
            },
            maxLines = 1
        )
        // Notification Before Month
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.notification_before_month_title)
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SelectButton(
                    text = stringResource(id = R.string.original_title_display_option),
                    isSelected = userPreferences.notificationBeforeMonth,
                    onSelect = {
                        viewModel.updateNotificationBeforeMonth(true)
                    }
                )
                SelectButton(
                    text = stringResource(id = R.string.original_title_not_display_option),
                    isSelected = !userPreferences.notificationBeforeMonth,
                    onSelect = {
                        viewModel.updateNotificationBeforeMonth(false)
                    }
                )
            }
        }
        // Notification Before Week
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.notification_before_week_title)
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SelectButton(
                    text = stringResource(id = R.string.original_title_display_option),
                    isSelected = userPreferences.notificationBeforeWeek,
                    onSelect = {
                        viewModel.updateNotificationBeforeWeek(true)
                    }
                )
                SelectButton(
                    text = stringResource(id = R.string.original_title_not_display_option),
                    isSelected = !userPreferences.notificationBeforeWeek,
                    onSelect = {
                        viewModel.updateNotificationBeforeWeek(false)
                    }
                )
            }
        }
        // Notification Before Day
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.notification_before_day_title)
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SelectButton(
                    text = stringResource(id = R.string.original_title_display_option),
                    isSelected = userPreferences.notificationBeforeDay,
                    onSelect = {
                        viewModel.updateNotificationBeforeDay(true)
                    }
                )
                SelectButton(
                    text = stringResource(id = R.string.original_title_not_display_option),
                    isSelected = !userPreferences.notificationBeforeDay,
                    onSelect = {
                        viewModel.updateNotificationBeforeDay(false)
                    }
                )
            }
        }
        // Notification On Date
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.notification_on_date_title)
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SelectButton(
                    text = stringResource(id = R.string.original_title_display_option),
                    isSelected = userPreferences.notificationOnDate,
                    onSelect = {
                        viewModel.updateNotificationOnDate(true)
                    }
                )
                SelectButton(
                    text = stringResource(id = R.string.original_title_not_display_option),
                    isSelected = !userPreferences.notificationOnDate,
                    onSelect = {
                        viewModel.updateNotificationOnDate(false)
                    }
                )
            }
        }
        // Sign In
        ProducerSignInSection(
            modifier = modifier,
            producerState = producerState,
            onSignInClick = {
                viewModel.onSignInClick(it)
            },
            handleIntent = {
                viewModel.handleIntent(it)
            },
            onSignOutClick = {
                viewModel.onSignOutClick()
            },
            deleteErrorMessage = {
                viewModel.deleteErrorMessage()
            }
        )
        // Confirm
        val context = LocalContext.current
        SelectButton(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(id = R.string.confirm_button),
            isSelected = true,
            onSelect = {
                coroutineScope.launch {
                    if (viewModel.saveSettings()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.save_settings_success),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.save_settings_fail),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )
    }
}

@Composable
fun ProducerSignInSection(
    modifier: Modifier,
    producerState: ProducerState,
    onSignInClick: (ActivityResultLauncher<IntentSenderRequest>) -> Unit,
    handleIntent: (Intent?) -> Unit,
    onSignOutClick: () -> Unit,
    deleteErrorMessage: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleIntent(result.data)
            }
        }
    )
    val context = LocalContext.current

    if (producerState.errorMessage != null) {
        Toast.makeText(
            context,
            producerState.errorMessage,
            Toast.LENGTH_LONG
        ).show()
        deleteErrorMessage()
    }

    LaunchedEffect(key1 = producerState.isSignInSuccess) {
        if (producerState.isSignInSuccess) {
            Toast.makeText(
                context,
                context.getString(R.string.sign_in_success),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    LaunchedEffect(key1 = producerState.isSignOutSuccess) {
        if (producerState.isSignOutSuccess) {
            Toast.makeText(
                context,
                context.getString(R.string.sign_out_success),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(text = stringResource(id = R.string.producer_sign_in_title))
            if (!producerState.isSignInSuccess) {
                Text(
                    modifier = modifier.clickable { onSignInClick(launcher) },
                    text = stringResource(id = R.string.sign_in_text)
                )
            } else {
                Row {
                    Text(text = producerState.userName ?: stringResource(id = R.string.unknown))
                    Spacer(modifier = modifier.width(8.dp))
                    Text(
                        modifier = modifier.clickable { onSignOutClick() },
                        text = stringResource(id = R.string.sign_out_text)
                    )
                }
            }
        }
    }
}

@Composable
fun SelectButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onSelect,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
            contentColor = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
        )
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ColorPicker(
    onDismiss: () -> Unit,
    onColorChanged: (Long) -> Unit
) {
    // on below line we are creating a variable for controller
    val controller = rememberColorPickerController()
    var selectedColor = Color.White.toArgb().toLong()
    Dialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Card {
            // on below line we are creating a column,
            Column(
                // on below line we are adding a modifier to it,
                modifier = Modifier
                    // on below line we are adding a padding.
                    .padding(all = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // on below line we are adding a row.
                Row(
                    // on below line we are adding a modifier
                    // on below line we are adding horizontal
                    // and vertical alignment.
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // on below line we are adding a alpha tile.
                    AlphaTile(
                        // on below line we are
                        // adding modifier to it
                        modifier = Modifier
                            // on below line
                            // we are adding a height.
                            .height(60.dp)
                            // on below line we are adding clip.
                            .clip(RoundedCornerShape(6.dp)),
                        // on below line we are adding controller.
                        controller = controller
                    )
                }
                // on below line we are
                // adding horizontal color picker.
                HsvColorPicker(
                    // on below line we are
                    // adding a modifier to it
                    modifier = Modifier
                        .height(300.dp)
                        .padding(10.dp),
                    // on below line we are
                    // adding a controller
                    controller = controller,
                    // on below line we are
                    // adding on color changed.
                    onColorChanged = {
                        selectedColor = it.color.toArgb().toLong()
                    }
                )
                // on below line we are adding a alpha slider.
                AlphaSlider(
                    // on below line we
                    // are adding a modifier to it.
                    modifier = Modifier
                        .padding(10.dp)
                        .height(35.dp),
                    // on below line we are
                    // adding a controller.
                    controller = controller,
                    // on below line we are
                    // adding odd and even color.
                    tileOddColor = Color.White,
                    tileEvenColor = Color.Black
                )
                // on below line we are
                // adding a brightness slider.
                BrightnessSlider(
                    // on below line we
                    // are adding a modifier to it.
                    modifier = Modifier
                        .padding(10.dp)
                        .height(35.dp),
                    // on below line we are
                    // adding a controller.
                    controller = controller,
                )
                SelectButton(
                    text = stringResource(id = R.string.confirm_button),
                    isSelected = true,
                    onSelect = {
                        onColorChanged(selectedColor)
                        onDismiss()
                    }
                )

            }
        }
    }
}