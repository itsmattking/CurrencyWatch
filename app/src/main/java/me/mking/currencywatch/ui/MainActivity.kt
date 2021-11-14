package me.mking.currencywatch.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.ui.theme.CurrencyWatchTheme

@FlowPreview
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LatestExchangeRatesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyWatchTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainActivityScreen(viewModel.state.collectAsState()) {
                        viewModel.setOtherBase(CurrencyEntity(it, it, true))
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.load()
    }
}

@Composable
fun ContainerScreen(
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(backgroundColor = Color.White) {
        Column(content = content)
    }
}

@Composable
fun MainActivityScreen(
    state: State<ViewState<LatestExchangeRatesViewData>>,
    onClick: (String) -> Unit
) {
    ContainerScreen {
        Row(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
        ) {
            if (state.value is ViewState.Ready) {
                if ((state.value as ViewState.Ready).data.isReloading) {
                    Text(text = "Loading...", modifier = Modifier.weight(.8f))
                }
            }
            Text(text = "Currency Watch", modifier = Modifier.weight(.2f))
        }
        when (val currentState = state.value) {
            is ViewState.Error -> Text(currentState.throwable.message.toString())
            is ViewState.Idle -> Text("hi")
            is ViewState.Loading -> Text("Loading...")
            is ViewState.Ready -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(currentState.data.rates) { message ->
                        ExchangeRateRow(message, onClick)
                    }
                }
            }
        }

    }
}

@Composable
fun ExchangeRateRow(rate: LatestExchangeRatesViewData.ExchangeRate, onClick: (String) -> Unit) {
    Row {
        ClickableText(
            text = AnnotatedString(rate.name),
            modifier = Modifier.weight(.2f),
            onClick = {
                onClick.invoke(rate.name)
            })
        Text(
            rate.value.toString(), modifier = Modifier
                .fillMaxWidth()
                .weight(.8f),
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CurrencyWatchTheme {
        MainActivityScreen(
            state = produceState(
                initialValue = ViewState.Ready(
                    data = LatestExchangeRatesViewData(
                        base = CurrencyEntity("USD", "USD", true),
                        rates = listOf(
                            LatestExchangeRatesViewData.ExchangeRate("GBP", 0.85)
                        )
                    )
                ),
                producer = {}
            ),
            onClick = {}
        )
    }
}