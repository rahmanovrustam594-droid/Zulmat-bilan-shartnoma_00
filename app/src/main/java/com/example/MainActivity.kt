package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.GameScreen
import com.example.ui.GameViewModel
import com.example.ui.screens.CodexOracleScreen
import com.example.ui.screens.DayCampScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.NightDefenseScreen
import com.example.ui.theme.ZulmatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZulmatTheme {
                val viewModel: GameViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()
                val gameProfile by viewModel.gameProfile.collectAsState()

                // System BackHandler for non-main screens
                if (currentScreen != GameScreen.MAIN_MENU) {
                    BackHandler {
                        when (currentScreen) {
                            GameScreen.NIGHT_DEFENSE -> {
                                // Prevent accidental exit mid-battle or return to camp
                                viewModel.returnToCamp()
                            }
                            GameScreen.CODEX_ORACLE -> {
                                viewModel.navigateTo(GameScreen.MAIN_MENU)
                            }
                            GameScreen.DAY_CAMP -> {
                                viewModel.navigateTo(GameScreen.MAIN_MENU)
                            }
                            else -> {
                                viewModel.navigateTo(GameScreen.MAIN_MENU)
                            }
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    ZulmatGameApp(
                        viewModel = viewModel,
                        currentScreen = currentScreen,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ZulmatGameApp(
    viewModel: GameViewModel,
    currentScreen: GameScreen,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.gameProfile.collectAsState()

    when (currentScreen) {
        GameScreen.MAIN_MENU -> {
            MainMenuScreen(
                viewModel = viewModel,
                profile = profile,
                modifier = modifier
            )
        }
        GameScreen.DAY_CAMP -> {
            if (profile != null) {
                DayCampScreen(
                    viewModel = viewModel,
                    profile = profile!!,
                    modifier = modifier
                )
            } else {
                MainMenuScreen(
                    viewModel = viewModel,
                    profile = null,
                    modifier = modifier
                )
            }
        }
        GameScreen.NIGHT_DEFENSE -> {
            if (profile != null) {
                NightDefenseScreen(
                    viewModel = viewModel,
                    profile = profile!!,
                    modifier = modifier
                )
            } else {
                MainMenuScreen(
                    viewModel = viewModel,
                    profile = null,
                    modifier = modifier
                )
            }
        }
        GameScreen.CODEX_ORACLE -> {
            CodexOracleScreen(
                viewModel = viewModel,
                modifier = modifier
            )
        }
        GameScreen.SETTINGS -> {
            MainMenuScreen(
                viewModel = viewModel,
                profile = profile,
                modifier = modifier
            )
        }
    }
}
