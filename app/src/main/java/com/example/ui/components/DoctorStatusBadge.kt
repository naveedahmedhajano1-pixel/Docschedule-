package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusBreak
import com.example.ui.theme.StatusBreakBg
import com.example.ui.theme.StatusBreakText
import com.example.ui.theme.StatusOnLeave
import com.example.ui.theme.StatusOnLeaveBg
import com.example.ui.theme.StatusOnLeaveText
import com.example.ui.theme.StatusSittingIn
import com.example.ui.theme.StatusSittingInBg
import com.example.ui.theme.StatusSittingInText
import com.example.ui.theme.StatusSittingOut
import com.example.ui.theme.StatusSittingOutBg
import com.example.ui.theme.StatusSittingOutText

/**
 * Status badge displaying whether doctor is Sitting In, Sitting Out, On Leave, or on Break.
 */
@Composable
fun DoctorStatusBadge(
    status: String,
    isOnLeave: Boolean,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val effectiveStatus = if (isOnLeave) "ON_LEAVE" else status

    val (bgColor, textColor, borderColor, label, icon) = when (effectiveStatus) {
        "ON_LEAVE" -> StatusConfig(
            bg = StatusOnLeaveBg,
            text = StatusOnLeaveText,
            border = StatusOnLeave.copy(alpha = 0.3f),
            label = "ON LEAVE",
            icon = Icons.Default.Block
        )
        "SITTING_IN" -> StatusConfig(
            bg = StatusSittingInBg,
            text = StatusSittingInText,
            border = StatusSittingIn.copy(alpha = 0.4f),
            label = "SITTING IN",
            icon = Icons.Default.CheckCircle
        )
        "SITTING_OUT" -> StatusConfig(
            bg = StatusSittingOutBg,
            text = StatusSittingOutText,
            border = StatusSittingOut.copy(alpha = 0.4f),
            label = "SITTING OUT",
            icon = Icons.Default.Schedule
        )
        "BREAK" -> StatusConfig(
            bg = StatusBreakBg,
            text = StatusBreakText,
            border = StatusBreak.copy(alpha = 0.4f),
            label = "ON BREAK",
            icon = Icons.Default.HourglassTop
        )
        else -> StatusConfig(
            bg = StatusSittingInBg,
            text = StatusSittingInText,
            border = StatusSittingIn.copy(alpha = 0.4f),
            label = "SITTING IN",
            icon = Icons.Default.CheckCircle
        )
    }

    Row(
        modifier = modifier
            .testTag("doctor_status_badge_${label.lowercase().replace(' ', '_')}")
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = if (compact) 8.dp else 10.dp, vertical = if (compact) 3.dp else 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (effectiveStatus == "SITTING_IN") {
            // Pulsing live indicator dot
            Box(
                modifier = Modifier
                    .size(if (compact) 7.dp else 9.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(StatusSittingIn)
            )
            Spacer(modifier = Modifier.width(6.dp))
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(if (compact) 12.dp else 14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = label,
            color = textColor,
            fontSize = if (compact) 11.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

private data class StatusConfig(
    val bg: Color,
    val text: Color,
    val border: Color,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
