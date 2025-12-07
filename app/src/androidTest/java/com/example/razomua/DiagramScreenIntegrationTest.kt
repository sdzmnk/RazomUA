package com.example.razomua

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.razomua.ui.screens.welcome.DiagramScreen
import org.junit.Rule
import org.junit.Test

class DiagramScreenIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun diagramScreen_fullIntegrationTest() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "diagram") {
                composable("diagram") { DiagramScreen(navController) }
                composable("chats") { androidx.compose.material3.Text("Chats Screen") }
                composable("swipe") { androidx.compose.material3.Text("Swipe Screen") }
                composable("profile") { androidx.compose.material3.Text("Profile Screen") }
            }
        }

        composeTestRule.onNodeWithText("Статистика").assertIsDisplayed()
        composeTestRule.onNodeWithText("Показники активності користувача").assertIsDisplayed()

        composeTestRule.onNodeWithText("Лайки — 70%").assertIsDisplayed()
        composeTestRule.onNodeWithText("Матчі — 20%").assertIsDisplayed()
        composeTestRule.onNodeWithText("Повідомлення — 10%").assertIsDisplayed()

        val canvasNode = composeTestRule.onNodeWithTag("UserStatsDiagram")
        canvasNode.assertExists()

        composeTestRule.mainClock.advanceTimeBy(500)
        canvasNode.assertExists()
        composeTestRule.mainClock.advanceTimeBy(1500)
        canvasNode.assertExists()

    }
}
