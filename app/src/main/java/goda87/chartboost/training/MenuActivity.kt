package goda87.chartboost.training

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import goda87.chartboost.training.theme.TrainingTheme

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}

@Composable
fun MenuView() {
    Column {
        Greeting(name = "Pepito")
        NewGameButton()
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun NewGameButton() {
    val context = LocalContext.current
    Button(onClick = {
        Toast.makeText(context, "This opens a new game", Toast.LENGTH_SHORT).show()
    }) {
        Text(text = "New game")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TrainingTheme {
        MenuView()
    }
}