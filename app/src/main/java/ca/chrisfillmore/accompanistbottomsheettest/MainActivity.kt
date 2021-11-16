package ca.chrisfillmore.accompanistbottomsheettest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
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
                  navController.navigate(Screen.BottomSheetImageAndText.name)
                },
              ) {
                Text(
                  fontSize = 4.em,
                  text = "Open bottom sheet Image with fixed size",
                )
              }
            }
          }

          bottomSheet(route = Screen.BottomSheetImage.name) {
            // I could not reproduce just by using an ImageBitmap
//            Image(
//              bitmap = ImageBitmap.imageResource(id = R.drawable.good_news),
//              contentDescription = null,
//            )

            // I can reproduce by using Coil's rememberImagePainter and loading a Drawable,
            // but it can take several (10-20) attempts.
//            Image(
//              painter = rememberImagePainter(
//                data = ResourcesCompat.getDrawable(resources, R.drawable.good_news, null),
//              ),
//              contentDescription = null,
//            )

            // I can easily reproduce by loading a url. This reproduces the bug more frequently
            // than not.
            Image(
              painter = rememberImagePainter(
                data = "https://cdn.shopify.com/s/files/1/0463/6753/9356/products/IMG_20190711_134921.jpg?v=1631815297",
              ),
              contentDescription = null,
            )
          }

          bottomSheet(
            route = Screen.BottomSheetImageAndText.name,
          ) {
            Column {
              Image(
                painter = rememberImagePainter(
                  data = "https://cdn.shopify.com/s/files/1/0463/6753/9356/products/IMG_20190711_134921.jpg?v=1631815297",
                ),
                contentDescription = null,
                modifier = Modifier.height(400.dp)
              )
            }
          }
        }
      }
    }
  }
}

enum class Screen {
  StartDestination,
  BottomSheetImage,
  BottomSheetImageAndText,
}
