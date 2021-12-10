package me.mking.currencywatch.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.ui.theme.CurrencyWatchTheme
import java.lang.Integer.max
import java.text.DecimalFormat

@FlowPreview
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LatestExchangeRatesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyWatchTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainActivityScreen(viewModel.state.collectAsState(), {
                        viewModel.setBaseAmount(it)
                    }, { viewModel.setBaseCurrency(it.currencyName) })
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
    onBaseAmountChanged: (String) -> Unit,
    onExchangeRateClick: (ExchangeRateClickEvent) -> Unit
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
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(horizontal = 16.dp)) {
                        val textState =
                            remember {
                                mutableStateOf(
                                    TextFieldValue(
                                        currentState.data.baseAmount
                                    )
                                )
                            }
                        BasicTextField(
                            value = textState.value,
                            onValueChange = {
                                textState.value = when {
                                    it.text.isBlank() -> {
                                        onBaseAmountChanged.invoke("0.00")
                                        it
                                    }
                                    it.text.toDoubleOrNull() == null -> textState.value
                                    else -> {
                                        val newValue = it.text.trim()
                                            .replace("[^0-9.]".toRegex(), "")
                                            .take(13)
                                            .replace(
                                                "\\.([0-9]{1,3}).*?$".toRegex(), ".$1"
                                            )
                                        onBaseAmountChanged.invoke(newValue)
                                        it.copy(newValue)
                                    }
                                }
                            },
                            textStyle = TextStyle.Default.copy(
                                fontSize = 24.sp
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(9f),
                            decorationBox = { innerTextField ->
                                Row(
                                    modifier = Modifier
                                        .border(
                                            2.dp,
                                            color = Color.LightGray,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = currentState.data.baseCurrencySymbol,
                                        fontSize = 24.sp,
                                        color = Color.LightGray,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )
                                    innerTextField()
                                }
                            },
                            visualTransformation = CurrencyTransformation()
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(currentState.data.rates) { message ->
                            ExchangeRateRow(message) { onExchangeRateClick.invoke(message.clickEvent) }
                        }
                    }
                }
            }
        }
    }
}

class CurrencyTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (text.text.isBlank()) {
            return TransformedText(text, OffsetMapping.Identity)
        }
        val split = text.text.split(".")
        val prefix = split.first()
        val suffix = split.getOrElse(1) { "" }
        val containsDecimal = text.text.contains(".")

        val output = DecimalFormat("#,###").format(prefix.toDouble())
        val difference = (output.length - text.length) + when {
            suffix.isNotBlank() -> suffix.length + 1
            containsDecimal -> 1
            else -> 0
        }
        return TransformedText(
            AnnotatedString(
                output + when {
                    suffix.isNotBlank() -> ".$suffix"
                    containsDecimal -> "."
                    else -> ""
                }
            ),
            CurrencyOffsetMapping(difference)
        )
    }

    private class CurrencyOffsetMapping(private val offsetDifference: Int) : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return max(0, offset + offsetDifference)
        }

        override fun transformedToOriginal(offset: Int): Int {
            return max(0, offset - offsetDifference)
        }
    }
}

@Composable
fun ExchangeRateRow(rate: LatestExchangeRatesViewData.ExchangeRate, onClick: (String) -> Unit) {
    Row(modifier = Modifier.clickable { onClick.invoke(rate.name) }) {
        Text(
            text = rate.name,
            modifier = Modifier
                .weight(.12f)
                .align(Alignment.CenterVertically),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
        Text(
            text = rate.rate,
            modifier = Modifier
                .weight(.2f)
                .align(Alignment.CenterVertically),
            color = Color.Gray,
        )
        Text(
            "${rate.symbol} ${rate.value}",
            modifier = Modifier
                .fillMaxWidth()
                .weight(.6f)
                .align(Alignment.CenterVertically),
            fontSize = 24.sp,
            color = Color.DarkGray,
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
                        baseCurrency = CurrencyEntity("GBP", "GBP", true),
                        rates = listOf(
                            LatestExchangeRatesViewData.ExchangeRate("USD", "1.28", "$", "1.28", ExchangeRateClickEvent("USD"))
                        ),
                        baseAmount = "0.00",
                        baseCurrencySymbol = "£"
                    )
                ),
                producer = {}
            ),
            onBaseAmountChanged = {},
            onExchangeRateClick = {}
        )
    }
}