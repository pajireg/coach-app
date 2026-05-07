package com.suminchoi.coachapp.design.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.rcColors

@Composable
fun RcTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val colors = rcColors
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { { Text(it, style = RcTypography.bodySmall) } },
        placeholder = placeholder?.let { { Text(it, style = RcTypography.bodySmall, color = colors.textMuted) } },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        textStyle = RcTypography.bodyMedium,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.borderStrong,
            unfocusedBorderColor = colors.border,
            focusedContainerColor = colors.bgElev,
            unfocusedContainerColor = colors.bgElev,
            cursorColor = colors.text,
            focusedTextColor = colors.text,
            unfocusedTextColor = colors.text,
            focusedLabelColor = colors.textDim,
            unfocusedLabelColor = colors.textFaint,
        ),
    )
}
