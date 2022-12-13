package goda87.chartboost.training

import android.content.Context
import android.content.Intent
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

class MenuActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SDK", "SDK on create")
        banner = createBanner(this)
        setContent {
            TrainingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MenuView()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("SDK", "SDK on resume")
        initSDK(applicationContext)
    }

    override fun onStart() {
        super.onStart()
        Log.d("SDK", "SDK on start")
    }

    override fun onPause() {
        super.onPause()
        Log.d("SDK", "SDK on pause")
        banner.detach()
    }
}

private lateinit var banner: Banner

private fun initSDK(context: Context) {

    // Needs to be set before SDK init
    Chartboost.addDataUseConsent(context, GDPR(GDPR.GDPR_CONSENT.BEHAVIORAL))
    Chartboost.addDataUseConsent(context, CCPA(CCPA.CCPA_CONSENT.OPT_IN_SALE))
    Chartboost.addDataUseConsent(context, COPPA(true))

    val startCallback: StartCallback = StartCallback { error ->
        if (error == null) {
            Log.d("SDK", "SDK is initialized")
            banner.cache()
            rewarded = createRewardedAd(context)
            rewarded.cache()
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
        appId = "",
        appSignature = "",
        onStarted = startCallback,
    )
}

@Composable
fun MenuView() {
    Column {
        Greeting(name = "Pepito")
        NewGameButton()
        MoreEnergyButton()
        ChartBoostBannerWrapper()
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

private lateinit var rewarded : Rewarded
private lateinit var energyLevel: MutableState<Int>

fun createRewardedAd(context: Context): Rewarded {
    return Rewarded("get_energy", object: RewardedCallback {
        override fun onAdClicked(event: ClickEvent, error: ClickError?) {
            Log.d("Rewarded", "SDK Rewarded onAdClicked $event")
        }

        override fun onAdDismiss(event: DismissEvent) {
            Log.d("Rewarded", "SDK Rewarded onAdDismiss $event")
        }

        override fun onAdLoaded(event: CacheEvent, error: CacheError?) {
            Log.d("Rewarded", "SDK Rewarded onAdLoaded $event")
        }

        override fun onAdRequestedToShow(event: ShowEvent) {
            Log.d("Rewarded", "SDK Rewarded onAdRequestedToShow $event")
        }

        override fun onAdShown(event: ShowEvent, error: ShowError?) {
            Log.d("Rewarded", "SDK Rewarded onAdShown $event")
            Log.d("Rewarded", "SDK Rewarded error ${error?.code?.name}")
            if (error != null) {
                Toast.makeText(context, "No add available", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onImpressionRecorded(event: ImpressionEvent) {
            Log.d("Rewarded", "SDK Rewarded onImpressionRecorded $event")
        }

        override fun onRewardEarned(event: RewardEvent) {
            Log.d("Rewarded", "SDK Rewarded onRewardEarned $event")
            energyLevel.value += 1
        }

    })
}

@Composable
fun NewGameButton() {
    val context = LocalContext.current
    Row {
        Button(onClick = {
            context.startActivity(Intent(context, GameActivity::class.java))
        }) {
            Text(text = "New game")
        }
    }
}

@Composable
fun MoreEnergyButton() {
    energyLevel = remember {
        mutableStateOf(0)
    }

    Row {
        Button(onClick = {
            rewarded.show()
        }) {
            Text(text = "Energy Level ${energyLevel.value}")
        }
    }
}

@Composable
fun ChartBoostBannerWrapper() {
//    val banner = remember { mutableStateOf(0) }

    // Adds view to Compose
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red, RectangleShape), // Occupy the max size in the Compose UI tree
        factory = { context ->
            // Creates view
            Log.d("SDK", "SDK banner create")
            banner
        },
        update = { view ->
            // View's been inflated or state read in this block has been updated
            // Add logic here if necessary

            // As selectedItem is read here, AndroidView will recompose
            // whenever the state changes
            // Example of Compose -> View communication
            Log.d("SDK", "SDK banner.show()")
//            view.show()
        }
    )
}

private fun createBanner(context: Context): Banner {
     return Banner(
        context = context,
        location = "menu",
        size = Banner.BannerSize.STANDARD,
        callback = object: BannerCallback {
            override fun onAdClicked(event: ClickEvent, error: ClickError?) {
                Log.d("BANNER", "SDK banner onAdClicked $event")
            }

            override fun onAdLoaded(event: CacheEvent, error: CacheError?) {
                Log.d("BANNER", "SDK banner onAdLoaded $event")
                banner.show()
            }

            override fun onAdRequestedToShow(event: ShowEvent) {
                Log.d("BANNER", "SDK banner onAdRequestedToShow $event")
            }

            override fun onAdShown(event: ShowEvent, error: ShowError?) {
                Log.d("BANNER", "SDK banner onAdShown $event")
            }

            override fun onImpressionRecorded(event: ImpressionEvent) {
                Log.d("BANNER", "SDK banner onImpressionRecorded $event")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TrainingTheme {
        MenuView()
    }
}