package com.example.comecan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comecan.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ログイン状態を3パターンで定義
sealed interface LoginCheckState {
    object Loading : LoginCheckState     // ① チェック中...
    object LoggedIn : LoginCheckState    // ② ログイン済み
    object LoggedOut : LoginCheckState   // ③ 未ログイン
}

class MainViewModel(
    // (DIを使うと、ここにAuthRepositoryが自動で入る)
    private val authRepository: AuthRepository
) : ViewModel() {

    // UIに公開するログイン状態
    private val _loginState = MutableStateFlow<LoginCheckState>(LoginCheckState.Loading)
    val loginState: StateFlow<LoginCheckState> = _loginState.asStateFlow()

    init {
        // ViewModelが起動したら、すぐにログイン状態をチェック
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            // TODO: AuthRepositoryに isLoggeIn() 関数を作る
            // (中身は TokenManager.getToken() != null みたいな簡単なチェック)

            if (authRepository.isLoggedIn()) {
                _loginState.value = LoginCheckState.LoggedIn
            } else {
                _loginState.value = LoginCheckState.LoggedOut
            }
        }
    }
}