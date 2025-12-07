package com.example.razomua

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.razomua.ui.screens.login.LoginScreen
import com.example.razomua.viewmodel.LoginViewModel
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_uiElementsDisplayed() {
        composeTestRule.setContent {
            LoginScreen(navController = androidx.navigation.compose.rememberNavController())
        }

        composeTestRule.onNodeWithTag("emailField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
    }

    @Test
    fun loginScreen_canInputText() {
        val viewModel = LoginViewModel()

        composeTestRule.setContent {
            LoginScreen(
                navController = androidx.navigation.compose.rememberNavController(),
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag("emailField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("123456")

        assert(viewModel.email.value == "test@example.com")
        assert(viewModel.password.value == "123456")
    }
}
