package moe.lyniko.replacecursor

// https://stackoverflow.com/a/63877349
// https://stackoverflow.com/a/1109108
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import moe.lyniko.replacecursor.ui.AppNavHost
import moe.lyniko.replacecursor.ui.BottomNavigation
import moe.lyniko.replacecursor.ui.theme.MyApplicationTheme
import moe.lyniko.replacecursor.utils.PreferenceUtils


class MainActivity : ComponentActivity() {
    private var snackbarHostState = SnackbarHostState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            PreferenceUtils.getInstance(this)
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.not_activated), Toast.LENGTH_LONG).show()
            finish()
            return
        }
        setContent {
            MyApplicationTheme {

                val scope = rememberCoroutineScope()
                val snackbarHostStateRemember = remember { snackbarHostState }
                val navController = rememberNavController()

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostStateRemember)
                    },
                    bottomBar = {
                        BottomNavigation(navController = navController)
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

}
