package moe.lyniko.replacecursor.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.getPreferenceFlow
import me.zhanghai.compose.preference.switchPreference
import moe.lyniko.replacecursor.R
import moe.lyniko.replacecursor.utils.PreferenceUtils
import moe.lyniko.replacecursor.utils.PreferenceUtils.Companion.ConfigKeys


@Composable
fun SettingsView() {
    val context = LocalContext.current
    val managerPref = PreferenceUtils.getInstance(context).managerPref
    ProvidePreferenceLocals(
        flow = managerPref.getPreferenceFlow()
    ) {
        Text(text = "Currently empty.")
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            switchPreference(
//                key=ConfigKeys.HideNoActivityPackages.key,
//                defaultValue = ConfigKeys.HideNoActivityPackages.default,
//                title = { Text(context.getString(R.string.hide_no_activity_packages)) },
//                summary = { Text(context.getString(R.string.hide_no_activity_packages_summary)) },
//            )
//        }
    }
}