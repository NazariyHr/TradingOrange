package com.trading.orange.presentation.features.contact_us

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trading.orange.R
import com.trading.orange.presentation.common.components.MainScreensLayout
import com.trading.orange.presentation.common.theme.ColorOrange
import com.trading.orange.presentation.common.theme.DefaultTextStyle
import com.trading.orange.presentation.common.theme.FontFamilyAvenirBlack
import com.trading.orange.presentation.common.theme.FontFamilyAvenirHeavy
import com.trading.orange.presentation.common.theme.FontFamilyAvenirRegular
import com.trading.orange.presentation.common.theme.TradingOrangeTheme

@Composable
fun ContactUsScreenRoot(
    navController: NavController,
    viewModel: ContactUsViewModel =
        hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    ContactUsScreen(
        state = state,
        onWriteMailClicked = {
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("mailto:${ContextCompat.getString(context, R.string.email)}")
                )
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"))
                context.startActivity(browserIntent)
            }
        }
    )
}

@Composable
private fun ContactUsScreen(
    state: ContactUsScreenState,
    onWriteMailClicked: () -> Unit
) {
    MainScreensLayout {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.contact_us),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = "Contact us!",
                    style = DefaultTextStyle
                        .copy(
                            fontSize = 24.sp,
                            color = Color.White,
                            fontFamily = FontFamilyAvenirBlack
                        ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 24.dp)
                )

                Text(
                    text = "В случае вопросов обращайтесь по имейл test@gmail.com",
                    style = DefaultTextStyle
                        .copy(
                            fontSize = 16.sp,
                            color = Color(255, 246, 246),
                            fontFamily = FontFamilyAvenirRegular
                        ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(bottom = 16.dp, top = 16.dp)
                )

                Button(
                    onClick = {
                        onWriteMailClicked()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 1.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = ColorOrange
                    )
                ) {
                    Text(
                        text = "Написать нам на почту",
                        style = DefaultTextStyle.copy(
                            fontSize = 16.sp,
                            color = Color.White,
                            fontFamily = FontFamilyAvenirHeavy
                        ),
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ContactUsScreenPreview() {
    TradingOrangeTheme {
        ContactUsScreen(
            state = ContactUsScreenState(),
            onWriteMailClicked = {}
        )
    }
}