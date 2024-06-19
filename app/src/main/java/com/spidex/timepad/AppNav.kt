package com.spidex.timepad

import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.exyte.animatednavbar.utils.noRippleClickable
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.spidex.timepad.ui.theme.background
import com.spidex.timepad.ui.theme.card
import java.time.LocalDate

@Composable
fun AppNavigation(){

    val viewModel = TaskViewModel(taskRepository = TaskRepository(AppDatabase.getDatabase(
        LocalContext.current).taskDao())
    )

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val navViewModel = viewModel<NavViewModel>()
    val showBottomNav by navViewModel.showBottomNav.collectAsState()


    val navigationItems = listOf(
        NavigationItem(0,"Home",NavigationRoute.Home.route,R.drawable.clock_false, R.drawable.clock_true),
        NavigationItem(1,"Task",NavigationRoute.Task.route,R.drawable.task_false,R.drawable.task_true),
        NavigationItem(2,"DashBoard",NavigationRoute.Dashboard.route,R.drawable.dashboard_false,R.drawable.dashboard_true),
    )

    val systemUiController = rememberSystemUiController()
    val view = LocalView.current
    SideEffect {
        systemUiController.isNavigationBarVisible = false
        systemUiController.isStatusBarVisible = false
        view.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            navViewModel.setShowBottomNav(
                destination.route in listOf(
                    NavigationRoute.Home.route,
                    NavigationRoute.Dashboard.route,
                    NavigationRoute.Task.route
                )
            )
        }
    }

    Scaffold(
        containerColor = background,
        bottomBar = {
            if (showBottomNav) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ){
                    Row (
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(Color.White),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Image(
                            painter = if(currentRoute == NavigationRoute.Home.route) painterResource(id = R.drawable.clock_true) else painterResource(id = R.drawable.clock_false),
                            contentDescription = null,
                            modifier = Modifier
                                .width(32.dp)
                                .height(32.dp)
                                .noRippleClickable {
                                    navController.navigate(NavigationRoute.Home.route)
                                }
                        )
                        Image(
                            painter = if(currentRoute == NavigationRoute.Task.route) painterResource(id = R.drawable.task_true) else painterResource(id = R.drawable.task_false),
                            contentDescription = null,
                            modifier = Modifier
                                .width(32.dp)
                                .height(32.dp)
                                .noRippleClickable {
                                    navController.navigate(NavigationRoute.Task.route)
                                }
                        )
                        Image(
                            painter = if(currentRoute == NavigationRoute.Dashboard.route) painterResource(id = R.drawable.dashboard_true) else painterResource(id = R.drawable.dashboard_false),
                            contentDescription = null,
                            modifier = Modifier
                                .width(32.dp)
                                .height(32.dp)
                                .noRippleClickable {
                                    navController.navigate(NavigationRoute.Dashboard.route)
                                }
                        )

                    }
                }
            }
        },
    ){
        NavHost(
            navController = navController,
            startDestination = NavigationRoute.Home.route,
            modifier = Modifier.padding(it)
        ){
            composable(NavigationRoute.Home.route){
                HomeScreen(viewModel) {
                    navController.navigate(NavigationRoute.Clock.route)
                }
            }
            composable(NavigationRoute.Task.route){
                TaskScreen(viewModel){
                    navController.navigate(NavigationRoute.Clock.route)
                }
            }
            composable(NavigationRoute.Dashboard.route){
                DashboardScreen(viewModel)
            }
            composable(NavigationRoute.Clock.route){
                TimeScreen(navController,viewModel){task->
                    viewModel.markTaskCompleted(task)
                }
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun Preview(){
    AppNavigation()
}
