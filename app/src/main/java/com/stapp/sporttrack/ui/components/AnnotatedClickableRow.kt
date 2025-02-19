package com.stapp.sporttrack.ui.components

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.stapp.sporttrack.ui.theme.BlueBlack

@Composable
fun AnnotatedClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    onClick: (String) -> Unit
) {

    BasicText(
        text = text,
        modifier = modifier.clickable {
            text.getStringAnnotations(tag = "URL", start = 0, end = text.length)
                .firstOrNull()?.let { annotation ->
                    onClick(annotation.item)
                }
        },
        style = style
    )
}

@Composable
fun AnnotatedClickableRow(
    context: Context,
    questionText: String,
    actionText: String,
    targetActivity: Class<*>,
    extraIntentConfig: Intent.() -> Unit = {}
) {
    val annotatedString = buildAnnotatedString {
        append(questionText)

        pushStringAnnotation(tag = "URL", annotation = actionText)
        withStyle(style = SpanStyle(color = BlueBlack, textDecoration = TextDecoration.Underline)) {
            append(actionText)
        }
        pop()
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
    ) {
        BasicText(
            text = annotatedString,
            modifier = Modifier.clickable {
                annotatedString.getStringAnnotations(
                    tag = "URL",
                    start = 0,
                    end = annotatedString.length
                )
                    .firstOrNull()?.let { annotation ->
                        if (annotation.item == actionText) {
                            val intent = Intent(context, targetActivity).apply {
                                extraIntentConfig()
                            }
                            context.startActivity(intent)
                            (context as ComponentActivity).finish()
                        }
                    }
            }
        )
    }
}