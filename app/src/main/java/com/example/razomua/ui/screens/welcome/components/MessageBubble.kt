//package com.example.razomua.ui.screens.welcome.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import com.example.razomua.model.Message
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//@Composable
//fun MessageBubble(message: Message) {
//    val alignment = if (message.isSentByUser) Alignment.TopEnd else Alignment.TopStart
//    val backgroundColor = if (message.isSentByUser) Color(0xFFDCF8C6) else Color(0xFFFFFFFF)
//    val shape = RoundedCornerShape(
//        topStart = 8.dp,
//        topEnd = 8.dp,
//        bottomStart = if (message.isSentByUser) 8.dp else 0.dp,
//        bottomEnd = if (message.isSentByUser) 0.dp else 8.dp
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 4.dp),
//        contentAlignment = alignment
//    ) {
//        Column(
//            modifier = Modifier
//                .background(backgroundColor, shape)
//                .padding(12.dp)
//        ) {
//            Text(
//                text = message.text,
//                style = MaterialTheme.typography.bodyMedium
//            )
//            Text(
//                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
//                style = MaterialTheme.typography.labelSmall,
//                color = Color.Gray,
//                modifier = Modifier.align(Alignment.End)
//            )
//        }
//    }
//}

package com.example.razomua.ui.screens.welcome.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.razomua.model.Message
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageBubble(message: Message) {
    val alignment = if (message.isCurrentUser) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isCurrentUser)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.isCurrentUser) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = backgroundColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!message.isCurrentUser && message.senderName.isNotEmpty()) {
                    Text(
                        text = message.senderName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.text,
                    color = textColor,
                    fontSize = 16.sp
                )

                if (message.timestamp > 0) {
                    Text(
                        text = formatTimestamp(message.timestamp),
                        fontSize = 10.sp,
                        color = textColor.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}