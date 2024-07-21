package com.practica.buscov2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.Profession
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.theme.RedBusco
import com.practica.buscov2.ui.theme.Rubik
import com.practica.buscov2.ui.theme.YellowGold

@Composable
fun CardProposal(image: String, title: String, price: String, date: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(150.dp)
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
            .clickable { onClick() },
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

@Composable
fun CardWorkerRecommendation(
    modifier: Modifier = Modifier,
    user: User,
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
                    image = user.image ?: "",
                    modifier = Modifier.size(100.dp)
                )
                Box() {
                    Text(
                        text = "$qualification% calificación",
                        fontSize = 15.sp,
                        style = TextStyle.Default.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "$qualification% calificación", fontSize = 15.sp, color = YellowGold,
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
                        text = user.worker?.workersProfessions?.first()?.profession?.name ?: "",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = "${user.name} ${user.lastname}", color = Color.White)
                }
                Text(
                    text = "${user.worker?.yearsExperience} años de experiencia",
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
                    modifier = Modifier.size(100.dp)
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
                    text = " $${proposal.minBudget} a $${proposal.maxBudget}",
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