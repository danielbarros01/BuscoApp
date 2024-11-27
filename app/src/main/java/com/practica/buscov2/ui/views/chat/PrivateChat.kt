package com.practica.buscov2.ui.views.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.Message
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.components.ArrowBack
import com.practica.buscov2.ui.components.InsertCircleProfileImage
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.chat.ChatViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.util.AppUtils.Companion.formatHours
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatPrivateScreen(
    toUserId: Int,
    viewModel: ChatViewModel,
    vmUser: UserViewModel,
    navController: NavHostController
) {
    val user by vmUser.user.collectAsState()
    val token by viewModel.token.collectAsState()

    //El otro usuario
    var toUser: User? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(Unit) {
        token?.let {
            vmUser.getMyProfile(it.token, onError = {
                navController.navigate("Login")
            }, onSuccess = {})
        }

        vmUser.getProfile(
            userId = toUserId,
            onError = { navController.popBackStack() },
            onSuccess = { toUser = it })
        viewModel.getMessagesWithUser(toUserId)
    }

    if (user != null && toUser != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            ChatP(
                Modifier.align(Alignment.Center),
                viewModel,
                user!!,
                toUser!!,
                navController
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatP(
    modifier: Modifier,
    viewModel: ChatViewModel,
    user: User,
    toUser: User,
    navController: NavHostController
) {
    Scaffold(modifier = Modifier
        .fillMaxSize(),
        bottomBar = {
            //Send message
            SectionSendMessage(viewModel, toUser.id!!)
        },
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp)
                    .clip(RoundedCornerShape(20.dp)),
                title = {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier) {
                            InsertCircleProfileImage(
                                image = toUser.image ?: "",
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(start = 10.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                modifier = Modifier.clickable {
                                    navController.navigate("Profile/${toUser.id}")
                                },
                                text = "${toUser.name} ${toUser.lastname}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(modifier = Modifier.padding(5.dp),
                        onClick = {
                            navController.popBackStack()
                        }) {
                        ArrowBack()
                    }
                },
                colors = TopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = GrayText,
                    navigationIconContentColor = Color.Black,
                    scrolledContainerColor = Color.Red,
                    actionIconContentColor = Color.Cyan
                ),
            )
        })
    {
        val listState = rememberLazyListState() // Recordar el estado de la lista
        val coroutineScope = rememberCoroutineScope()
        val chats by viewModel.messages.collectAsState()

        //Scroll hasta abajo
        if (chats.isNotEmpty()) {
            LaunchedEffect(chats.size) {
                coroutineScope.launch {
                    listState.scrollToItem(chats.size - 1)
                }
            }
        }


        LazyColumn(
            state = listState,
            modifier = modifier
                .padding(it)
                .padding(15.dp)
                .fillMaxSize()
        ) {
            items(chats.size) { index ->
                val msg = chats[index]
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (msg.userIdSender == user.id) Alignment.End else Alignment.Start
                ) {
                    MessageView(
                        sending = msg.userIdSender == user.id,
                        message = msg
                    )
                }
            }
        }
    }
}

@Composable
fun SectionSendMessage(vm: ChatViewModel, toUserId: Int) {
    var message by remember {
        mutableStateOf("")
    }

    BottomAppBar(
        containerColor = Color.White,
        modifier = Modifier.clip(RoundedCornerShape(20.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row {
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text(text = "Escribe tu mensaje ...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.LightGray.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.LightGray.copy(alpha = 0.2f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 15.dp)
                )

                //Space(size = 4.dp)
                IconButton(
                    modifier = Modifier
                        .width(70.dp)
                        .size(60.dp), onClick = {
                        vm.sendMessage(toUserId, message)
                        message = ""
                    }, enabled = message.isNotEmpty()
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.send2),
                        contentDescription = "",
                        tint = OrangePrincipal
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessageView(modifier: Modifier = Modifier, sending: Boolean, message: Message) {
    Box(
        modifier = modifier
            .background(
                if (sending) OrangePrincipal
                else Color.LightGray.copy(alpha = 0.5f),
                shape = if (sending) RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 0.dp
                )
                else
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
            )

            .padding(12.dp)

    ) {
        Text(
            text = message.text,
            fontSize = 16.sp,
            color = if (sending) Color.White else GrayText
        )
    }
    Text(
        text = formatHours(message.dateAndTime ?: ""),
        modifier = Modifier.padding(start = 8.dp),
        fontSize = 12.sp
    )
}


