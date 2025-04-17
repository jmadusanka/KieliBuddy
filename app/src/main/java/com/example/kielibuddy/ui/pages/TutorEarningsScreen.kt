package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.ui.theme.Purple40
import com.example.kielibuddy.model.PaymentSession


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorEarningsScreen(
    navController: NavController,
    totalEarnings: Double = 450.0,
    totalStudents: Int = 15,
    totalHours: Double = 120.5,
    paymentHistory: List<PaymentSession> = listOf(
        PaymentSession("Student A", 25.0, "Apr 16, 2025", 1.5),
        PaymentSession("Student B", 30.0, "Apr 14, 2025", 2.0),
        PaymentSession("Student C", 20.0, "Apr 12, 2025", 1.0)
    )
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Earnings", color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp)) // Same width as IconButton to balance the layout
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple40
                )
            )

        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color(0xFFF9F7FF)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Earnings Summary", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))
                SummaryItem("Total Earnings", "€$totalEarnings")
                SummaryItem("Total Students", "$totalStudents")
                SummaryItem("Total Hours", "${totalHours}h")
                Spacer(modifier = Modifier.height(20.dp))
                Text("Payment History", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(paymentHistory) { session ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(session.studentName, fontWeight = FontWeight.Medium)
                            Text("${session.date} • ${session.hours}h", fontSize = 12.sp, color = Color.Gray)
                        }
                        Text("€${session.amount}", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 16.sp)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TutorEarningsScreenPreview() {
    val navController = rememberNavController()
    TutorEarningsScreen(
        navController = navController,
        paymentHistory = listOf(
            PaymentSession("Student A", 25.0, "Apr 16, 2025", 1.5),
            PaymentSession("Student B", 30.0, "Apr 14, 2025", 2.0),
            PaymentSession("Student C", 20.0, "Apr 12, 2025", 1.0)
        )
    )
}
