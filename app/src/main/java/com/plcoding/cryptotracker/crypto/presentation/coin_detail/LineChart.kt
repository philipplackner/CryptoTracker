package com.plcoding.cryptotracker.crypto.presentation.coin_detail

import android.provider.ContactsContract.Data
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.cryptotracker.crypto.domain.CoinPrice
import com.plcoding.cryptotracker.ui.theme.CryptoTrackerTheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun LineChart(
    dataPoints: List<DataPoint>,
    style: ChartStyle,
    visibleDataPointsIndices: IntRange,
    unit: String,
    modifier: Modifier = Modifier,
    selectedDataPoint: DataPoint? = null,
    onSelectedDataPoint: (DataPoint) -> Unit = {},
    onXLabelWidthChange: (Float) -> Unit = {},
    showHelperLines: Boolean = true
) {
    val textStyle = LocalTextStyle.current.copy(
        fontSize = style.labelFontSize
    )

    val visibleDataPoints = remember(dataPoints, visibleDataPointsIndices) {
        dataPoints.slice(visibleDataPointsIndices)
    }

    val maxYValue = remember(visibleDataPoints) {
        visibleDataPoints.maxOfOrNull { it.y } ?: 0f
    }
    val minYValue = remember(visibleDataPoints) {
        visibleDataPoints.minOfOrNull { it.y } ?: 0f
    }

    val measurer = rememberTextMeasurer()

    var xLabelWidth by remember {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(key1 = xLabelWidth) {
        onXLabelWidthChange(xLabelWidth)
    }

    val selectedDataPointIndex = remember(selectedDataPoint) {
        dataPoints.indexOf(selectedDataPoint)
    }
    var drawPoints by remember {
        mutableStateOf(listOf<DataPoint>())
    }
    var isShowingDataPoints by remember {
        mutableStateOf(selectedDataPoint != null)
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(drawPoints, xLabelWidth) {
                detectHorizontalDragGestures { change, _ ->
                    val newSelectedDataPointIndex = getSelectedDataPointIndex(
                        touchOffsetX = change.position.x,
                        triggerWidth = xLabelWidth,
                        drawPoints = drawPoints
                    )
                    isShowingDataPoints =
                        (newSelectedDataPointIndex + visibleDataPointsIndices.first) in
                                visibleDataPointsIndices
                    if(isShowingDataPoints) {
                        onSelectedDataPoint(dataPoints[newSelectedDataPointIndex])
                    }
                }
            }
    ) {
        val minLabelSpacingYPx = style.minYLabelSpacing.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()
        val horizontalPaddingPx = style.horizontalPadding.toPx()
        val xAxisLabelSpacingPx = style.xAxisLabelSpacing.toPx()

        val xLabelTextLayoutResults = visibleDataPoints.map {
            measurer.measure(
                text = it.xLabel,
                style = textStyle.copy(textAlign = TextAlign.Center)
            )
        }
        val maxXLabelWidth = xLabelTextLayoutResults.maxOfOrNull { it.size.width } ?: 0
        val maxXLabelHeight = xLabelTextLayoutResults.maxOfOrNull { it.size.height } ?: 0
        val maxXLabelLineCount = xLabelTextLayoutResults.maxOfOrNull { it.lineCount } ?: 0
        val xLabelLineHeight = if(maxXLabelLineCount > 0) {
            maxXLabelHeight / maxXLabelLineCount
        } else 0

        val viewPortHeightPx = size.height -
                (maxXLabelHeight + 2 * verticalPaddingPx
                        + xLabelLineHeight + xAxisLabelSpacingPx)

        // Y-LABEL CALCULATION
        val labelViewPortHeightPx = viewPortHeightPx + xLabelLineHeight
        val labelCountExcludingLastLabel =
            ((labelViewPortHeightPx / (xLabelLineHeight + minLabelSpacingYPx))).toInt()

        val valueIncrement = (maxYValue - minYValue) / labelCountExcludingLastLabel

        val yLabels = (0..labelCountExcludingLastLabel).map {
            ValueLabel(
                value = maxYValue - (valueIncrement * it),
                unit = unit
            )
        }

        val yLabelTextLayoutResults = yLabels.map {
            measurer.measure(
                text = it.formatted(),
                style = textStyle
            )
        }
        val maxYLabelWidth = yLabelTextLayoutResults.maxOfOrNull { it.size.width } ?: 0

        val viewPortTopY = verticalPaddingPx + xLabelLineHeight + 10f
        val viewPortRightX = size.width
        val viewPortBottomY = viewPortTopY + viewPortHeightPx
        val viewPortLeftX = 2f * horizontalPaddingPx + maxYLabelWidth

        xLabelWidth = maxXLabelWidth + xAxisLabelSpacingPx
        xLabelTextLayoutResults.forEachIndexed { index, result ->
            val x = viewPortLeftX + xAxisLabelSpacingPx / 2f +
                    xLabelWidth * index
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x,
                    y = viewPortBottomY + xAxisLabelSpacingPx
                ),
                color = if(index == selectedDataPointIndex) {
                    style.selectedColor
                } else style.unselectedColor
            )

            if(showHelperLines) {
                drawLine(
                    color = if(selectedDataPointIndex == index) {
                        style.selectedColor
                    } else style.unselectedColor,
                    start = Offset(
                        x = x + result.size.width / 2f,
                        y = viewPortBottomY
                    ),
                    end = Offset(
                        x = x + result.size.width / 2f,
                        y = viewPortTopY
                    ),
                    strokeWidth = if(selectedDataPointIndex == index) {
                        style.helperLinesThicknessPx * 1.8f
                    } else style.helperLinesThicknessPx
                )
            }

            if(selectedDataPointIndex == index) {
                val valueLabel = ValueLabel(
                    value = visibleDataPoints[index].y,
                    unit = unit
                )
                val valueResult = measurer.measure(
                    text = valueLabel.formatted(),
                    style = textStyle.copy(
                        color = style.selectedColor
                    ),
                    maxLines = 1
                )
                val textPositionX = if(selectedDataPointIndex == visibleDataPointsIndices.last) {
                    x - valueResult.size.width
                } else {
                    x - valueResult.size.width / 2f
                } + result.size.width / 2f
                val isTextInVisibleRange =
                    (size.width - textPositionX).roundToInt() in 0..size.width.roundToInt()
                if(isTextInVisibleRange) {
                    drawText(
                        textLayoutResult = valueResult,
                        topLeft = Offset(
                            x = textPositionX,
                            y = viewPortTopY - valueResult.size.height - 10f
                        )
                    )
                }
            }
        }

        val heightRequiredForLabels = xLabelLineHeight *
                (labelCountExcludingLastLabel + 1)
        val remainingHeightForLabels = labelViewPortHeightPx - heightRequiredForLabels
        val spaceBetweenLabels = remainingHeightForLabels / labelCountExcludingLastLabel

        yLabelTextLayoutResults.forEachIndexed { index, result ->
            val x = horizontalPaddingPx + maxYLabelWidth - result.size.width.toFloat()
            val y = viewPortTopY +
                    index * (xLabelLineHeight + spaceBetweenLabels) -
                    xLabelLineHeight / 2f
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x,
                    y = y
                ),
                color = style.unselectedColor
            )

            if(showHelperLines) {
                drawLine(
                    color = style.unselectedColor,
                    start = Offset(
                        x = viewPortLeftX,
                        y = y + result.size.height.toFloat() / 2f
                    ),
                    end = Offset(
                        x = viewPortRightX,
                        y = y + result.size.height.toFloat() / 2f
                    ),
                    strokeWidth = style.helperLinesThicknessPx
                )
            }
        }

        // visibleDataPointsIndices = 5..20
        drawPoints = visibleDataPointsIndices.map {
            val x = viewPortLeftX + (it - visibleDataPointsIndices.first) *
                    xLabelWidth + xLabelWidth / 2f
            // [minYValue; maxYValue] -> [0; 1]
            val ratio = (dataPoints[it].y - minYValue) / (maxYValue - minYValue)
            val y = viewPortBottomY - (ratio * viewPortHeightPx)
            DataPoint(
                x = x,
                y = y,
                xLabel = dataPoints[it].xLabel
            )
        }

        val conPoints1 = mutableListOf<DataPoint>()
        val conPoints2 = mutableListOf<DataPoint>()
        for(i in 1 until drawPoints.size) {
            val p0 = drawPoints[i - 1]
            val p1 = drawPoints[i]

            val x = (p1.x + p0.x) / 2f
            val y1 = p0.y
            val y2 = p1.y

            conPoints1.add(DataPoint(x, y1, ""))
            conPoints2.add(DataPoint(x, y2, ""))
        }

        val linePath = Path().apply {
            if(drawPoints.isNotEmpty()) {
                moveTo(drawPoints.first().x, drawPoints.first().y)

                for(i in 1 until drawPoints.size) {
                    cubicTo(
                        x1 = conPoints1[i - 1].x,
                        y1 = conPoints1[i - 1].y,
                        x2 = conPoints2[i - 1].x,
                        y2 = conPoints2[i - 1].y,
                        x3 = drawPoints[i].x,
                        y3 = drawPoints[i].y
                    )
                }
            }
        }
        drawPath(
            path = linePath,
            color = style.chartLineColor,
            style = Stroke(
                width = 5f,
                cap = StrokeCap.Round
            )
        )

        drawPoints.forEachIndexed { index, point ->
            if(isShowingDataPoints) {
                val circleOffset = Offset(
                    x = point.x,
                    y = point.y
                )
                drawCircle(
                    color = style.selectedColor,
                    radius = 10f,
                    center = circleOffset
                )

                if(selectedDataPointIndex == index) {
                    drawCircle(
                        color = Color.White,
                        radius = 15f,
                        center = circleOffset
                    )
                    drawCircle(
                        color = style.selectedColor,
                        radius = 15f,
                        center = circleOffset,
                        style = Stroke(
                            width = 3f
                        )
                    )
                }
            }
        }
    }
}

private fun getSelectedDataPointIndex(
    touchOffsetX: Float,
    triggerWidth: Float,
    drawPoints: List<DataPoint>
): Int {
    val triggerRangeLeft = touchOffsetX - triggerWidth / 2f
    val triggerRangeRight = touchOffsetX + triggerWidth / 2f
    return drawPoints.indexOfFirst {
        it.x in triggerRangeLeft..triggerRangeRight
    }
}

@Preview(widthDp = 1000)
@Composable
private fun LineChartPreview() {
    CryptoTrackerTheme {
        val coinHistoryRandomized = remember {
            (1..20).map {
                CoinPrice(
                    priceUsd = Random.nextFloat() * 1000.0,
                    dateTime = ZonedDateTime.now().plusHours(it.toLong())
                )
            }
        }
        val style = ChartStyle(
            chartLineColor = Color.Black,
            unselectedColor = Color(0xFF7C7C7C),
            selectedColor = Color.Black,
            helperLinesThicknessPx = 1f,
            axisLinesThicknessPx = 5f,
            labelFontSize = 14.sp,
            minYLabelSpacing = 25.dp,
            verticalPadding = 8.dp,
            horizontalPadding = 8.dp,
            xAxisLabelSpacing = 8.dp
        )
        val dataPoints = remember {
            coinHistoryRandomized.map {
                DataPoint(
                    x = it.dateTime.hour.toFloat(),
                    y = it.priceUsd.toFloat(),
                    xLabel = DateTimeFormatter
                        .ofPattern("ha\nM/d")
                        .format(it.dateTime)
                )
            }
        }
        LineChart(
            dataPoints = dataPoints,
            style = style,
            visibleDataPointsIndices = 0..19,
            unit = "$",
            modifier = Modifier
                .width(700.dp)
                .height(300.dp)
                .background(Color.White),
            selectedDataPoint = dataPoints[1]
        )
    }
}