package goda87.chartboost.training

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ads.Interstitial;
import com.chartboost.sdk.ads.Rewarded;
import com.chartboost.sdk.ads.Banner;
import com.chartboost.sdk.LoggingLevel;
import com.chartboost.sdk.Analytics;
import com.chartboost.sdk.callbacks.BannerCallback
import com.chartboost.sdk.callbacks.InterstitialCallback
import com.chartboost.sdk.callbacks.RewardedCallback
import com.chartboost.sdk.callbacks.StartCallback
import com.chartboost.sdk.events.*
import com.chartboost.sdk.privacy.model.CCPA
import com.chartboost.sdk.privacy.model.COPPA
import com.chartboost.sdk.privacy.model.GDPR
import goda87.chartboost.training.theme.TrainingTheme

class GameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SDK", "SDK on create GAme")
        add = createIntersticialAd(this)
        setContent {
            TrainingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GameView()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("SDK", "SDK on resume")
        add.cache()
//        initSDK(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SDK", "game on destroy")
    }
}

private lateinit var add: Interstitial

private fun initSDK(context: Context) {

    // Needs to be set before SDK init
    Chartboost.addDataUseConsent(context, GDPR(GDPR.GDPR_CONSENT.BEHAVIORAL))
    Chartboost.addDataUseConsent(context, CCPA(CCPA.CCPA_CONSENT.OPT_IN_SALE))
    Chartboost.addDataUseConsent(context, COPPA(true))

    val startCallback: StartCallback = StartCallback { error ->
        if (error == null) {
            Log.d("SDK", "SDK is initialized")
            Toast.makeText(context, "SDK is initialized", Toast.LENGTH_SHORT).show()
            add.cache()
        } else {
            Log.d("SDK", "SDK initialization error: ${error.code.name} ${error.code.errorCode}")
            error.exception?.printStackTrace()
            Toast.makeText(
                context,
                "SDK initialization error: ${error.code.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Chartboost.startWithAppId(
        context = context,
        appId = "6385d6b664ee65cebcc2791e",
        appSignature = "2171dba20bebb14f3cc4d3b3ba3e828c98f2b49d",
        onStarted = startCallback,
    )
}

@Composable
fun GameView() {
    Row {
        FinishLevelButton()
        CloseButton()
    }
}

@Composable
fun FinishLevelButton() {
    val context = LocalContext.current
    Log.d("compose", "sdk create interstitial ad")
    Row {
        Button(onClick = {
            add.show()
        }) {
            Text(text = "Finish Level")
        }
    }
}

@Composable
fun CloseButton() {
    val activity = LocalContext.current as Activity
    Button(onClick = {
        activity.finish()
    }) {
        Text(text = "Close")
    }
}

private fun createIntersticialAd(activity: Activity): Interstitial {
    return Interstitial("new_game", object: InterstitialCallback {
        override fun onAdClicked(event: ClickEvent, error: ClickError?) {
            Log.d("intersticial", "SDK intersticial onAdClicked $event")
        }

        override fun onAdDismiss(event: DismissEvent) {
            Log.d("intersticial", "SDK intersticial onAdDismiss $event")
            activity.finish()
        }

        override fun onAdLoaded(event: CacheEvent, error: CacheError?) {
            Log.d("intersticial", "SDK intersticial onAdLoaded $event")
        }

        override fun onAdRequestedToShow(event: ShowEvent) {
            Log.d("intersticial", "SDK intersticial onAdRequestedToShow $event")
        }

        override fun onAdShown(event: ShowEvent, error: ShowError?) {
            Log.d("intersticial", "SDK intersticial onAdShown ${event.ad}")
            Log.d("intersticial", "SDK intersticial onAdShown ${error?.code?.name}")
            if (error != null) {
                activity.finish()
            }
        }

        override fun onImpressionRecorded(event: ImpressionEvent) {
            Log.d("intersticial", "SDK intersticial onImpressionRecorded $event")
        }

    })
}

@Preview(showBackground = true)
@Composable
fun GamePreview() {
    TrainingTheme {
        GameView()
    }
}