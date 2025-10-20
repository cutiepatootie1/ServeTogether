package com.main.servetogether.ui.forgotpass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.main.servetogether.ui.theme.White

@Composable
fun ForgotPass(navController: NavController){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(White),
        contentAlignment = Alignment.TopCenter){

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth() ) {


        }
    }
}