package com.example.comecan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.comecan.ui.screens.HomeScreen // ⭐️ TODO: HomeScreen.kt を作る
import com.example.comecan.ui.screens.LoginScreen
import com.example.comecan.ui.viewmodel.LoginCheckState
import com.example.comecan.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    // ⭐️ ViewModel をセット
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                // ⭐️ ViewModel の状態を監視する
                val loginState by viewModel.loginState.collectAsState()

                // ⭐️ 状態に応じて、表示する画面を切り替える
                when (loginState) {

                    // ① チェック中...
                    is LoginCheckState.Loading -> {
                        // くるくる（スプラッシュ画面）を表示
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    // ② ログイン済み
                    is LoginCheckState.LoggedIn -> {
                        // ⭐️ ホーム画面を表示
                        HomeScreen()
                    }

                    // ③ 未ログイン
                    is LoginCheckState.LoggedOut -> {
                        // ⭐️ ログイン画面を表示
                        LoginScreen()
                    }
                }
            }
        }
    }
}