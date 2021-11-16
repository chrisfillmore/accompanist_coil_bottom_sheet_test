package ca.chrisfillmore.accompanistbottomsheettest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

class MainActivity : ComponentActivity() {
  @ExperimentalMaterialApi
  @ExperimentalMaterialNavigationApi
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    /**
     * I believe a code illustrates a bug in which navigating to a bottom sheet, which only
     * contains an [Image], doesn't always open the bottom sheet. On observation. I've seen that
     * the destination is added to the back stack, but the bottom sheet does not open.
     *
     * Subsequent attempts to open the bottom sheet may succeed, and you may end up with duplicate
     * back stack entries. That is, dismissing the bottom sheet which successfully opened, pops
     * the stack and navigates you to the same bottom sheet, the one which failed to open before.
     * (This behaviour is not totally consistent. Sometimes it occurs, sometimes not.)
     */
    setContent {
      val bottomSheetNavigator = rememberBottomSheetNavigator()
      val navController = rememberNavController().apply {
        navigatorProvider.addNavigator(bottomSheetNavigator)
      }
      ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
      ) {
        NavHost(
          navController = navController,
          startDestination = Screen.StartDestination.name,
          modifier = Modifier,
        ) {
          composable(route = Screen.StartDestination.name) {
            Column(
              modifier = Modifier.padding(16.dp)
            ) {
              // Repro steps:
              // 1) Tap this button to open bottom sheet
              // 2) Dismiss the bottom sheet
              // 3) Repeat steps 1-2 until you reproduce
              Button(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxWidth(),
                onClick = {
                  navController.navigate(Screen.BottomSheetImage.name)
                },
              ) {
                Text(
                  fontSize = 4.em,
                  text = "Open bottom sheet Image",
                )
              }

              Spacer(modifier = Modifier.height(16.dp))

              Button(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxWidth(),
                onClick = {
                  navController.navigate(Screen.BottomSheetImageWithHeight.name)
                },
              ) {
                Text(
                  fontSize = 4.em,
                  text = "Open bottom sheet Image with height",
                )
              }
            }
          }

          bottomSheet(route = Screen.BottomSheetImage.name) {
            BabyImage()
          }

          bottomSheet(
            route = Screen.BottomSheetImageWithHeight.name,
          ) {
            // Applying a fixed height fixes the issue
            BabyImage(modifier = Modifier.height(400.dp))
          }
        }
      }
    }
  }
}

@Composable
fun BabyImage(modifier: Modifier = Modifier) {
  Image(
    painter = rememberImagePainter(
      data = "https://cdn.shopify.com/s/files/1/0463/6753/9356/products/IMG_20190711_134921.jpg?v=1631815297",
    ),
    contentDescription = null,
    modifier = modifier
  )
}

enum class Screen {
  StartDestination,
  BottomSheetImage,
  BottomSheetImageWithHeight,
}
