package com.example.comecan.util

import android.content.Context
import android.content.SharedPreferences

// (Context はあとでDIやApplicationクラスから受け取る)
class TokenManager(context: Context) {

    // "PREFS_NAME" という名前で設定ファイルをロック
    private val prefs: SharedPreferences =
        context.getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)

    // "JWT_TOKEN" という名前で金庫に鍵を保存する
    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString("JWT_TOKEN", token)
        editor.apply()
    }

    // 金庫から鍵を取り出す
    fun getToken(): String? {
        return prefs.getString("JWT_TOKEN", null)
    }

    // (ログアウト時に鍵を捨てる)
    fun clearToken() {
        val editor = prefs.edit()
        editor.remove("JWT_TOKEN")
        editor.apply()
    }
}