import React from 'react';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ReferenceLine,
} from 'recharts';
import { formatDate, formatBmi } from '../../utils/formatters';
import './BmiTrendChart.css';

const BmiTrendChart = ({ trendData = [] }) => {
  if (trendData.length === 0) {
    return (
      <div className="trend-chart-empty">
        <span className="chart-empty-icon">📈</span>
        <p>No historical BMI calculations recorded for this patient.</p>
      </div>
    );
  }

  // Format data for chart display
  const formattedData = trendData.map((item) => ({
    ...item,
    formattedDate: formatDate(item.calculatedAt),
    bmi: Number(item.bmiValue.toFixed(1)),
    weight: item.weight,
    height: item.height,
  }));

  // Determine Y-axis padding boundaries
  const bmiValues = formattedData.map((d) => d.bmi);
  const minBmiVal = Math.min(...bmiValues);
  const maxBmiVal = Math.max(...bmiValues);
  const yMin = Math.floor(Math.min(minBmiVal, 17) - 1);
  const yMax = Math.ceil(Math.max(maxBmiVal, 32) + 1);

  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <div className="chart-custom-tooltip">
          <p className="tooltip-date">{data.formattedDate}</p>
          <div className="tooltip-details">
            <p>
              <strong>BMI:</strong> <span className="tooltip-value">{formatBmi(data.bmi)} kg/m²</span>
            </p>
            <p>
              <strong>Weight:</strong> {data.weight} kg
            </p>
            <p>
              <strong>Height:</strong> {data.height} cm
            </p>
            <p className="tooltip-class">
              <strong>Category:</strong> {data.classification}
            </p>
          </div>
        </div>
      );
    }
    return null;
  };

  return (
    <div className="bmi-trend-chart-wrapper">
      <h3 className="chart-title">BMI Historical Timeline</h3>
      <div className="chart-container" style={{ width: '100%', height: 320 }}>
        <ResponsiveContainer>
          <LineChart
            data={formattedData}
            margin={{ top: 20, right: 30, left: 0, bottom: 10 }}
          >
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border-gray)" />
            <XAxis 
              dataKey="formattedDate" 
              tick={{ fill: 'var(--neutral-slate-medium)', fontSize: 11 }}
              tickLine={false}
              axisLine={{ stroke: 'var(--border-gray)' }}
            />
            <YAxis 
              domain={[yMin, yMax]}
              tick={{ fill: 'var(--neutral-slate-medium)', fontSize: 11 }}
              tickLine={false}
              axisLine={{ stroke: 'var(--border-gray)' }}
            />
            <Tooltip content={<CustomTooltip />} />
            
            {/* WHO Threshold Guide Lines */}
            <ReferenceLine 
              y={18.5} 
              stroke="var(--status-warning)" 
              strokeDasharray="4 4"
              label={{ 
                value: 'Underweight <18.5', 
                position: 'insideBottomLeft', 
                fill: 'var(--status-warning)', 
                fontSize: 10,
                fontWeight: 600,
                offset: 5
              }} 
            />
            <ReferenceLine 
              y={25.0} 
              stroke="var(--status-warning)" 
              strokeDasharray="4 4"
              label={{ 
                value: 'Overweight >25.0', 
                position: 'insideBottomLeft', 
                fill: 'var(--status-warning)', 
                fontSize: 10,
                fontWeight: 600,
                offset: 5
              }} 
            />
            <ReferenceLine 
              y={30.0} 
              stroke="var(--status-danger)" 
              strokeDasharray="4 4"
              label={{ 
                value: 'Obese >30.0', 
                position: 'insideBottomLeft', 
                fill: 'var(--status-danger)', 
                fontSize: 10,
                fontWeight: 600,
                offset: 5
              }} 
            />

            <Line
              type="monotone"
              dataKey="bmi"
              stroke="var(--primary-teal-dark)"
              strokeWidth={3}
              dot={{ r: 5, strokeWidth: 2, fill: 'var(--neutral-white)', stroke: 'var(--primary-teal-dark)' }}
              activeDot={{ r: 7, strokeWidth: 0, fill: 'var(--primary-teal-medium)' }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default BmiTrendChart;
