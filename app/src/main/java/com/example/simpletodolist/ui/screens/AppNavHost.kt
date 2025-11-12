package com.example.simpletodolist.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.simpletodolist.PermissionDelegate
import com.example.simpletodolist.ui.viewmodel.TodoViewModel
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private enum class OnboardingStep {
    WELCOME,
    PERMISSION_PROMPT
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    isOnboardingCompleted: Boolean,
    onOnboardingFinished: () -> Unit,
    permissionDelegate: PermissionDelegate,
    todoViewModel: TodoViewModel
) {
    Scaffold { innerPadding ->

        if (isOnboardingCompleted) {
            TodoScreen(viewModel = todoViewModel)
        } else {
            var currentStep by rememberSaveable { mutableStateOf(OnboardingStep.WELCOME) }

            when (currentStep) {
                OnboardingStep.WELCOME -> {
                    WelcomeScreen(
                        innerPadding = innerPadding,
                        onGetStartedClicked = {
                            currentStep = OnboardingStep.PERMISSION_PROMPT
                        }
                    )
                }

                OnboardingStep.PERMISSION_PROMPT -> {
                    PermissionPromptScreen(
                        innerPadding = innerPadding,
                        onGrantPermission = {
                            permissionDelegate.requestExactAlarm()
                            permissionDelegate.requestNotification()
                            onOnboardingFinished()
                        },
                        onDecline = {
                            onOnboardingFinished()
                        }
                    )
                }
            }
        }
    }
}