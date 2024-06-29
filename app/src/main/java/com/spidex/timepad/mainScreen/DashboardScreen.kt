package com.spidex.timepad.mainScreen

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.exyte.animatednavbar.utils.noRippleClickable
import com.spidex.timepad.R
import com.spidex.timepad.SoundHelper
import com.spidex.timepad.viewModel.TaskViewModel
import com.spidex.timepad.data.AppDatabase
import com.spidex.timepad.data.TaskRepository
import com.spidex.timepad.ui.theme.background
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(viewModel: TaskViewModel, onNavigateToAllTaskScreen : () -> Unit){

    val todayProductivity by viewModel.todayProductivity.collectAsState()
    val lastWeekProductivity by viewModel.lastWeekProductivity.collectAsState()
    val graphData by viewModel.graphData.collectAsState()

    val selected by viewModel.selectedPeriod.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = background),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier.padding(top = 16.dp)
        ){
            Text(
                text = "My Productivity",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ){
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(160.dp)
                    .padding(start = 4.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(2.dp)
            ){
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {

                    val (image,text,des) = createRefs()

                    Image(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier
                            .width(42.dp)
                            .height(48.dp)
                            .constrainAs(image) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            }
                    )

                    Column(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .constrainAs(text) {
                                start.linkTo(image.end, margin = 8.dp)
                            }
                    ){
                        Text(
                            text = "Task",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier,
                            fontFamily = FontFamily(Font(R.font.font2))
                        )
                        Text(
                            text = "Completed",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily(Font(R.font.font2)),
                            modifier = Modifier
                        )
                    }

                    Text(
                        text = if(selected == "day") todayProductivity.first.toString()
                            else    lastWeekProductivity.first.toString(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.font2)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp)
                            .horizontalScroll(rememberScrollState())
                            .constrainAs(des) {
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom, margin = 8.dp)
                            }
                    )

                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(end = 4.dp, top = 8.dp, start = 8.dp, bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(2.dp)
            ){
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {

                    val (image,text,des) = createRefs()

                    Image(
                        painter = painterResource(id = R.drawable.ic_time),
                        contentDescription = null,
                        modifier = Modifier
                            .width(42.dp)
                            .height(48.dp)
                            .constrainAs(image) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            }
                    )

                    Column(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .constrainAs(text) {
                                start.linkTo(image.end, margin = 8.dp)
                            }
                    ){
                        Text(
                            text = "Time",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily(Font(R.font.font2)),
                            modifier = Modifier
                        )
                        Text(
                            text = "Duration",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily(Font(R.font.font2)),
                            modifier = Modifier
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .constrainAs(des) {
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom, margin = 8.dp)
                            },
                        verticalAlignment = Alignment.Bottom
                    ) {


                        var totalSec = todayProductivity.second / 1000
                        if(selected == "week")
                        {
                            totalSec = lastWeekProductivity.second / 1000
                        }
                        val totalMin = totalSec / 60
                        val hour = totalMin / 60
                        val min = totalMin % 60

                        Text(
                            text = hour.toString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily(Font(R.font.font2)),
                            modifier = Modifier
                        )
                        Text(
                            text = "h",
                            fontSize = 24.sp,
                            color = Color.Gray,
                            fontFamily = FontFamily(Font(R.font.font2)),
                            modifier = Modifier
                                .offset(y = (-4).dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = min.toString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily(Font(R.font.font2)),
                            modifier = Modifier
                        )
                        Text(
                            text = "m",
                            fontSize = 24.sp,
                            color = Color.Gray,
                            fontFamily = FontFamily(Font(R.font.font2)),
                            modifier = Modifier
                                .offset(y = (-4).dp)
                                .padding(end = 4.dp)
                        )
                    }

                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 24.dp, top = 48.dp, bottom = 48.dp)
                .background(color = Color(0xFFe9e9fd), RoundedCornerShape(20))
                .noRippleClickable {
                    viewModel.setSelectedPeriod(
                        when (selected) {
                            "day" -> "week"
                            else -> "day"
                        }
                    )
                },
        ){
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(4.dp),
                shape = RoundedCornerShape(20),
                colors = CardDefaults.cardColors(
                    containerColor = when(selected){
                        "day" -> Color.White
                        else -> Color(0xFFe9e9fd)
                    }
                )
            ){
                Text(
                    text = "Day",
                    color = when(selected){
                        "day" -> Color.Black
                        else -> Color.Gray
                    },
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.font2)),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                shape = RoundedCornerShape(20),
                colors = CardDefaults.cardColors(
                    containerColor = when(selected){
                        "week" -> Color.White
                        else -> Color(0xFFe9e9fd)
                    }
                ),

            ){
                Text(
                    text = "Week",
                    color = when(selected){
                        "week" -> Color.Black
                        else -> Color.Gray
                    },
                    fontFamily = FontFamily(Font(R.font.font2)),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        onNavigateToAllTaskScreen()
                    }
                ),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(2.dp)
        ) {

            LineChart(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
                    .aspectRatio(1f),
                data = listOf(
                    Line(
                        label = "Time Spent",
                        values = graphData,
                        color = SolidColor(Color(0xFF23af92)),
                        firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                        secondGradientFillColor = Color.Transparent,
                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                        gradientAnimationDelay = 1000,
                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                    )
                ),
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                curvedEdges = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview(){
    val soundHelper = SoundHelper(LocalContext.current)
    val viewModel = TaskViewModel(taskRepository = TaskRepository(
        AppDatabase.getDatabase(
        LocalContext.current).taskDao()),
        soundHelper
    )
    DashboardScreen(viewModel){}
}