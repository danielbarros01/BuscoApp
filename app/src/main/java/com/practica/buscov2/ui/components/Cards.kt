package com.practica.buscov2.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.Application
import com.practica.buscov2.model.busco.Chat
import com.practica.buscov2.model.busco.Notification
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.Qualification
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.Worker
import com.practica.buscov2.ui.theme.GrayField
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.GreenBusco
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.theme.RedBusco
import com.practica.buscov2.ui.theme.YellowGold
import com.practica.buscov2.ui.theme.YellowStar
import com.practica.buscov2.util.AppUtils
import com.practica.buscov2.util.AppUtils.Companion.daysAgo
import com.practica.buscov2.util.AppUtils.Companion.formatNumber
import com.practica.buscov2.util.AppUtils.Companion.starsToPercentage

@Composable
fun CardProposal(
    image: String,
    title: String,
    price: String,
    date: String,
    modifier: Modifier? = null,
    onClick: () -> Unit
) {
    val modifierDefault = Modifier
        .padding(vertical = 8.dp)
        .fillMaxWidth()
        .height(150.dp)
        .shadow(10.dp, RoundedCornerShape(10.dp))
        .clip(RoundedCornerShape(10.dp))
        .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
        .clickable { onClick() }

    Row(
        modifier = modifier ?: modifierDefault,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(110.dp)
                .fillMaxHeight()
        ) {
            InsertAsyncImage(
                image = image,
                R.drawable.camerapicnull,
                modifier = Modifier.fillMaxSize()
            ) {}
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = GrayText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = price, fontSize = 18.sp, modifier = Modifier.weight(1f))
                    Text(text = date, fontSize = 18.sp, color = OrangePrincipal)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CardProposalWithButton(
    proposal: Proposal,
    onClickProposal: () -> Unit,
    onSend: (MutableState<String>, MutableState<Boolean>) -> Unit
) {
    val textButton = remember { mutableStateOf("Enviar") }
    val enabledButton = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
    ) {
        CardProposal(
            image = proposal.image ?: "",
            title = proposal.title ?: "",
            price = "$${proposal.minBudget.toString()} a $${proposal.maxBudget.toString()}",
            date = AppUtils.formatDateCard("${proposal.date}"),
            onClick = onClickProposal,
            modifier = Modifier
                .height(125.dp)
                .clickable { onClickProposal() }
        )
        ButtonPrincipal(
            text = textButton.value,
            enabled = enabledButton.value,
            shape = RectangleShape
        ) {
            onSend(textButton, enabledButton)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CardJob(
    proposal: Proposal,
    user: User,
    worker: Worker,
    onClickProposal: () -> Unit,
    onClickName: () -> Unit,
    onClickChat: () -> Unit
) {
    Column(
        modifier = Modifier
            .height(250.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
    ) {
        CardProposal(
            modifier = Modifier
                .height(125.dp)
                .clickable { onClickProposal() },
            image = proposal.image ?: "",
            title = proposal.title ?: "",
            price = "$${proposal.minBudget.toString()} a $${proposal.maxBudget.toString()}",
            date = AppUtils.formatDateCard("${proposal.date}"),
            onClick = onClickProposal
        )
        HorizontalDivider()
        //InfoUser and chat
        Row(
            modifier = Modifier
                .height(125.dp)
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(GrayPlaceholder)
                    .width(110.dp),
                contentAlignment = Alignment.Center
            ) {
                InsertCircleProfileImage(
                    image = user.image ?: "",
                    modifier = Modifier.size(80.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "${user.name} ${user.lastname}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    modifier = Modifier.clickable {
                        onClickName()
                    }
                )
                Text(
                    text = worker.professions?.firstOrNull()?.name ?: "",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { onClickChat() }, modifier = Modifier.size(50.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.message_fill),
                        contentDescription = "Enviar mensaje",
                        tint = OrangePrincipal,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun CardWorkerRecommendation(
    modifier: Modifier = Modifier,
    worker: Worker,
    qualification: Int,
    onClick: () -> Unit
) {
    val colorStops = arrayOf(
        0.0f to Color.White,
        0.3f to OrangePrincipal
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colorStops = colorStops
                )
            )
            .border(1.dp, OrangePrincipal, RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InsertCircleProfileImage(
                    image = worker.user?.image ?: "",
                    modifier = Modifier.size(100.dp),
                    onClick = onClick
                )
                Box {
                    Text(
                        text = "${starsToPercentage(qualification)}% calificación",
                        fontSize = 15.sp,
                        style = TextStyle.Default.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "${starsToPercentage(qualification)}% calificación",
                        fontSize = 15.sp,
                        color = YellowGold,
                        style = TextStyle.Default.copy(
                            drawStyle = Stroke(width = 3f, join = StrokeJoin.Round),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

            }
            Column(
                modifier = Modifier
                    .padding(start = 1.dp)
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = worker.professions?.first()?.name ?: "",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${worker.user?.name} ${worker.user?.lastname}",
                        color = Color.White
                    )
                }
                Text(
                    text = "${worker.yearsExperience} años de experiencia",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun CardProposalRecommendation(
    modifier: Modifier = Modifier,
    proposal: Proposal,
    onClick: () -> Unit
) {
    val colorStops = arrayOf(
        0.0f to Color.White,
        0.3f to OrangePrincipal
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colorStops = colorStops
                )
            )
            .border(1.dp, OrangePrincipal, RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InsertCircleProfileImage(
                    image = proposal.user?.image ?: "",
                    modifier = Modifier.size(100.dp),
                    onClick = onClick
                )
                Text(
                    text = proposal.user?.name ?: "",
                    fontSize = 20.sp,
                    style = TextStyle.Default.copy(
                        color = GrayText,
                        fontWeight = FontWeight.Bold,
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 1.dp)
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Busca",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${proposal.profession?.name} para ${proposal.title}",
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = " $${formatNumber(proposal.minBudget.toString())} a $${
                        formatNumber(
                            proposal.maxBudget.toString()
                        )
                    }",
                    color = Color.White,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 15.dp)
                )
            }
        }
    }
}

@Composable
fun CardApplicant(
    item: Application,
    rating: Qualification,
    onDecline: () -> Unit,
    onChoose: () -> Unit,
    onClickName: () -> Unit
) {
    CardWorker(
        modifier = if (item.status == true) Modifier
            .height(140.dp)
            .border(2.dp, YellowStar, RoundedCornerShape(10.dp)) else Modifier.height(180.dp),
        item.worker ?: Worker(),
        rating,
        onClickName
    ) {
        //BUTTONS
        Row(modifier = Modifier.offset(y = 4.dp)) {

            if (item.status == null) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenBusco
                    ),
                    onClick = { onChoose() }) {
                    Text(
                        text = "Elegir",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Button(modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedBusco
                    ),
                    onClick = { onDecline() }) {
                    Text(
                        text = "Rechazar",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
            if (item.status == false) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .background(GrayField),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Rechazado",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun CardWorker(
    modifier: Modifier = Modifier,
    worker: Worker,
    rating: Qualification,
    onClickName: () -> Unit = {},
    onClick: () -> Unit = {},
    buttons: @Composable () -> Unit = {}
) {
    val nameComplete = "${worker.user?.name} ${worker.user?.lastname}"
    val yearsExperience = worker.yearsExperience ?: 0
    val image = worker.user?.image ?: ""
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Column {

            //Data
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                //FOTO
                InsertCircleProfileImage(
                    image = image,
                    modifier = Modifier
                        .width(90.dp)
                        .height(90.dp),
                    onClick = onClick
                )


                //DATA
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxSize()
                ) {
                    //NAME
                    Text(
                        text = nameComplete,
                        fontSize = 26.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onClickName()
                        })
                    //YEARS
                    Text(
                        text = "$yearsExperience años de experiencia",
                        fontSize = 16.sp,
                        color = GrayText,
                        modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
                    )

                    //EXPERIENCE
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StarRatingBar(5, rating.score ?: 0f) {

                        }
                        Text(
                            text = "${rating.score} (${rating.quantity ?: ""})",
                            fontSize = 12.sp,
                            color = GrayPlaceholder,
                            fontWeight = FontWeight.Medium
                        )
                    }

                }
            }

            buttons()
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CardQualificationOfUser(user: User, qualification: Qualification) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        HorizontalDivider()
        Space(size = 4.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            InsertCircleProfileImage(
                image = user.image ?: "",
                modifier = Modifier.size(40.dp),
            )
            Space(size = 8.dp)
            Text(
                text = "${user.name} ${user.lastname}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (daysAgo(qualification.date!!) < 1) {
                    "Hoy"
                } else "Hace ${daysAgo(qualification.date)} días",
                color = GrayText,
                fontSize = 16.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                StarRatingBar(5, qualification.score ?: 0f, sizeStar = 28.dp) {}
                Space(size = 4.dp)
                Text(text = "${qualification.score}/5", color = GrayText, fontSize = 16.sp)
            }
        }
        Space(size = 2.dp)
        Text(text = qualification.commentary ?: "", fontSize = 18.sp, color = GrayText)
    }
}

@Composable
fun CardJobCompleted(proposal: Proposal, onClick: () -> Unit = {}) {
    Column(modifier = Modifier.padding(vertical = 14.dp)) {
        HorizontalDivider()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Space(size = 4.dp)
            Title(text = proposal.title ?: "", modifier = Modifier.clickable { onClick() })
            InsertAsyncImage(
                image = proposal.image ?: "",
                defaultImg = R.drawable.camerapicnull,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
            Text(text = proposal.description ?: "", fontSize = 16.sp, color = GrayText)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CardChatUser(chat: Chat, onClick: (Chat) -> Unit) {
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .height(100.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
            .shadow(
                elevation = 8.dp,
                spotColor = Color.Black,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable { onClick(chat) }
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier) {
                InsertCircleProfileImage(
                    image = chat.user?.image ?: "",
                    modifier = Modifier.size(70.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 10.dp, top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = (if (!chat.user?.name.isNullOrEmpty()) {
                        "${chat.user?.name} ${chat.user?.lastname}"
                    } else chat.user?.username).toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = chat.lastMessage?.text ?: "",
                    color = GrayText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
                Text(
                    text = AppUtils.formatHours(chat.lastMessage?.dateAndTime.toString()),
                    color = GrayText
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CardNotification(notification: Notification, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.padding(vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier) {
                InsertCircleProfileImage(
                    image = notification.userSender?.image ?: "",
                    modifier = Modifier.size(70.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 10.dp, top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Notificación",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = AppUtils.formatHours(notification.dateAndTime.toString()),
                        color = GrayText
                    )
                }

                Text(
                    text = notification.text ?: "",
                    color = GrayText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }


        }
    }
}