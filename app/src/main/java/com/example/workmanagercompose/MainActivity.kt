package com.example.workmanagercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmanagercompose.ui.theme.WorkManagerComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .build()

        val colorFilterRequest = OneTimeWorkRequestBuilder<ColorFilterWorker>()
            .build()

        val workManager = WorkManager.getInstance(applicationContext)

        setContent {
            WorkManagerComposeTheme {
                val workInfos = workManager
                    .getWorkInfosForUniqueWorkLiveData("download")
                    .observeAsState()
                    .value

                val downloadInfo = remember(key1 = workInfos){
                    workInfos?.find { it.id == downloadRequest.id}
                }

                val filterInfo = remember(key1 = workInfos){
                    workInfos?.find { it.id == colorFilterRequest.id}
                }

                val imageUri by derivedStateOf {
                    val downloadUri = downloadInfo
                        ?.outputData
                        ?.getString(WorkerKeys.IMAGE_URI)
                        ?.toUri()

                    val filterUri = filterInfo
                        ?.outputData
                        ?.getString(WorkerKeys.FILTER_URI)
                        ?.toUri()

                    filterUri ?: downloadUri
                }


            }
        }
    }
}
