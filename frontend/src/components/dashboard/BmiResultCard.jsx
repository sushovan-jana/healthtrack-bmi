import React from 'react';
import { formatBmi, getWhoBadgeDetails } from '../../utils/formatters';
import Card from '../common/Card';
import './BmiResultCard.css';

const BmiResultCard = ({ bmiValue, classification, height, weight }) => {
  const badge = getWhoBadgeDetails(classification);
  const bmiNum = Number(bmiValue);

  // Calculate needle position percentage (clamped between 15 and 40 for display purposes)
  const minBmi = 15;
  const maxBmi = 40;
  const needlePos = Math.min(Math.max(((bmiNum - minBmi) / (maxBmi - minBmi)) * 100, 0), 100);

  return (
    <Card className="bmi-result-card animate-scale-in">
      <div className="bmi-result-grid">
        <div className="bmi-score-panel">
          <span className="bmi-score-label">Computed BMI</span>
          <div className="bmi-score-value" style={{ color: badge.color }}>
            {formatBmi(bmiValue)}
          </div>
          <span className="bmi-score-unit">kg/m²</span>
        </div>

        <div className="bmi-status-panel">
          <div className="bmi-badge-row">
            <span 
              className="bmi-category-badge"
              style={{ backgroundColor: `${badge.color}15`, color: badge.color, borderColor: badge.color }}
            >
              {badge.label}
            </span>
          </div>
          <div className="bmi-risk-row">
            <span className="risk-label">Clinical Risk Level:</span>
            <span className="risk-value" style={{ color: badge.color }}>{badge.risk}</span>
          </div>
          <div className="bmi-info-row">
            <span>Inputs: {height} cm / {weight} kg</span>
          </div>
        </div>
      </div>

      {/* Visual BMI Gauge */}
      <div className="bmi-gauge-wrapper">
        <div className="bmi-gauge-labels">
          <span>15</span>
          <span>18.5</span>
          <span>25</span>
          <span>30</span>
          <span>40</span>
        </div>
        <div className="bmi-gauge-bar">
          <div className="gauge-segment segment-underweight" title="Underweight (< 18.5)"></div>
          <div className="gauge-segment segment-normal" title="Normal (18.5 - 24.9)"></div>
          <div className="gauge-segment segment-overweight" title="Overweight (25 - 29.9)"></div>
          <div className="gauge-segment segment-obese" title="Obese (>= 30)"></div>
          
          {/* Needle Indicator */}
          <div 
            className="bmi-gauge-needle" 
            style={{ left: `${needlePos}%`, backgroundColor: badge.color }}
          >
            <div className="needle-tooltip" style={{ backgroundColor: badge.color }}>
              {formatBmi(bmiValue)}
            </div>
          </div>
        </div>
        <div className="bmi-gauge-segment-labels">
          <span style={{ flex: 1.4 }}>Underweight</span>
          <span style={{ flex: 2.6 }}>Normal</span>
          <span style={{ flex: 2.0 }}>Overweight</span>
          <span style={{ flex: 4.0 }}>Obese</span>
        </div>
      </div>
    </Card>
  );
};

export default BmiResultCard;
