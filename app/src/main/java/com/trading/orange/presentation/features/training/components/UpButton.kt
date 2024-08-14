package com.trading.orange.presentation.features.training.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trading.orange.R
import com.trading.orange.presentation.common.theme.ColorGreen
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.TradingOrangeTheme

@Composable
fun UpButton(
    modifier: Modifier = Modifier,
    onCallClicked: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onCallClicked,
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContentColor = Color.Transparent
        ),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (enabled) {
                        Modifier.background(color = ColorGreen)
                    } else {
                        Modifier.background(color = Color(217, 217, 217))
                    }
                )
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(id = R.string.up),
                style = DefaultTextStyle.copy(
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Preview
@Composable
private fun UpButtonEnabledPreview() {
    TradingOrangeTheme {
        UpButton(
            onCallClicked = {},
            enabled = true
        )
    }
}

@Preview
@Composable
private fun UpButtonDisabledPreview() {
    TradingOrangeTheme {
        UpButton(
            onCallClicked = {},
            enabled = false
        )
    }
}