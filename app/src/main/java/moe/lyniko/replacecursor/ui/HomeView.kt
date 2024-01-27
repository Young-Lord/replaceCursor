package moe.lyniko.replacecursor.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import moe.lyniko.replacecursor.BuildConfig
import moe.lyniko.replacecursor.R
import moe.lyniko.replacecursor.ui.theme.MyApplicationTheme
import moe.lyniko.replacecursor.utils.PreferenceUtils
import moe.lyniko.replacecursor.utils.ResourceHookEntry
import java.lang.NullPointerException

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

fun isResourceIdValid(resourceId: String): Boolean {
    return resourceId.matches(Regex("^[a-z0-9_]+$"))
}

private lateinit var preferenceUtils: PreferenceUtils
private var snackbarHostState = SnackbarHostState()

@Composable
fun HomeView() {
    val context = LocalContext.current
    val recomposeEntries: MutableState<Int> = remember { mutableIntStateOf(0) }
    preferenceUtils = PreferenceUtils.getInstance(context)
    MyApplicationTheme {
        val snackbarHostStateRemember = remember { snackbarHostState }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostStateRemember)
            },
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                key(recomposeEntries.value){
                    MainEntries(recomposeEntries)
                }
                MainSettings(recomposeEntries)
            }
        }
    }
}

@SuppressLint("SetWorldReadable", "SdCardPath")
@Composable
private fun MainSettings(recomposeEntries: MutableState<Int>) {
    val context = LocalContext.current
    val activity = context.getActivity()!!
    var resultFile by remember { mutableStateOf("") }
    var resourceId by remember { mutableStateOf("") }
    @Suppress("BlockingMethodInNonBlockingContext") val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    activity.contentResolver.takePersistableUriPermission(
                        uri,
                        takeFlags
                    )
                    // copy to data dir
                    val inputStream = activity.contentResolver.openInputStream(uri)!!
                    if(resultFile.isNotEmpty()) preferenceUtils.removeImageBinary(resultFile)
                    resultFile = "${resourceId}_${System.currentTimeMillis()}"
                    preferenceUtils.setImageBinary(resultFile, inputStream.readBytes())
                    /*
                    // random hardcoded for xpsoed
                    val resultFileObject = File("/data/data/${BuildConfig.APPLICATION_ID}/files/").resolve(resultFile)
                    val outputStream = resultFileObject.outputStream()
                    inputStream!!.copyTo(outputStream)
                    outputStream.close()
                    resultFileObject.setReadable(true, false)
                    resultFile = resultFileObject.absolutePath
                    */
                    inputStream.close()
                    // Log.e("HomeView", "resultFilename: $resultFile")
                }
            }
        }
    // a card with resource id input and image file input

    //1. display a card
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        //2. display text field for resource id
        TextField(
            value = resourceId,
            onValueChange = { resourceId = it },
            label = { Text(text = LocalContext.current.getString(R.string.resource_id)) },
            modifier = Modifier.fillMaxWidth()
        )
        //3. display Button for image file, use SAF to select
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                enabled = isResourceIdValid(resourceId),
                onClick = {
                // open saf to get image
                val intent = activity.let {
                    Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                    }
                }
                startForResult.launch(intent)
            }) {
                Text(text = LocalContext.current.getString(R.string.select_image))
            }
            //4. display Button for add
            Button(
                enabled = isResourceIdValid(resourceId) && resultFile.isNotEmpty(),
                onClick = {
                    // add to preferenceUtils
                    preferenceUtils.addResourceHook(
                        ResourceHookEntry(
                            resourceId,
                            resultFile,
                            true,
                            0
                        )
                    )
                    // refresh
                    resourceId = ""
                    resultFile = ""
                    recomposeEntries.value += 1
                }) {
                Text(text = LocalContext.current.getString(R.string.add))
            }
        }
    }
}

@Composable
private fun MainEntries(recomposeEntries: MutableState<Int>) {
    val hooks by remember { mutableStateOf(preferenceUtils.resourceHooks) }
    hooks.forEach {
        SingleEntry(it, recomposeEntries)
    }
}

@Composable
private fun SingleEntry(res: ResourceHookEntry, recomposeEntries: MutableState<Int>) {
    // val activity = LocalContext.current.getActivity()!!

    // get resource_name, image from preferenceUtils, display them along with a switch
    // when switch is toggled, update preferenceUtils

    val imageBinary: ByteArray
    try{
        imageBinary = preferenceUtils.getImageBinary(res.imageFile)
        // Log.e("HomeView", "imageBinary: ${imageBinary.size}")
    }
    catch (e: NullPointerException) {
        e.printStackTrace()
        Log.w(BuildConfig.APPLICATION_ID, "image ${res.imageFile} not found, removed")
        preferenceUtils.removeResourceHook(res)
        return
    }

    //1. display a card
    Row(modifier=Modifier.fillMaxWidth()){
        //2. display image
        // Log.e("HomeView", "res.imageFile: ${res.imageFile}")
        Image(
            painter = rememberDrawablePainter(
                drawable = Drawable.createFromStream(
                    imageBinary.inputStream(),
                    null
                )
            ),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        //3. display resource id
        Column(
            modifier = Modifier.align(Alignment.CenterVertically),
        ) {
            Text(text = res.resourceId, style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.weight(1f))
        //4. display switch
        var checked by remember { mutableStateOf(res.enabled) }
        Switch(checked = checked, onCheckedChange = {
            res.enabled = it
            preferenceUtils.updateResourceHook(res)
            checked = it
        })
        //5. delete button
        IconButton(onClick = {
            preferenceUtils.removeResourceHook(res)
            recomposeEntries.value += 1
        }) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )
        }
        /*
        Test Content Provider
        val resolver  = activity.contentResolver
        val uri = Uri.parse("content://moe.lyniko.replacecursor.ImageProvider/${File(res.imageFile).name}")
        val cursor = resolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val imageBinary = cursor.getBlob(0)
            cursor.close()
            Log.e("HomeView", "imageBinary: ${imageBinary.size}")
            Text(text = "imageBinary: ${imageBinary.size}")
        }
        else {
            Log.e("HomeView", "cursor is null")
            Text(text = "cursor is null")
        }
         */
    }
}