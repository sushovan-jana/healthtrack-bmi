import React from 'react';
import Card from '../common/Card';
import './StatCard.css';

const StatCard = ({
  title,
  value,
  icon,
  description,
  trend,
  color = 'teal',
}) => {
  return (
    <Card className={`stat-card stat-card-${color}`}>
      <div className="stat-card-header">
        <span className="stat-card-title">{title}</span>
        <span className="stat-card-icon">{icon}</span>
      </div>
      <div className="stat-card-body">
        <h3 className="stat-card-value">{value}</h3>
        {description && <p className="stat-card-description">{description}</p>}
        {trend && (
          <span className={`stat-card-trend ${trend.positive ? 'trend-positive' : 'trend-negative'}`}>
            {trend.positive ? '▲' : '▼'} {trend.text}
          </span>
        )}
      </div>
    </Card>
  );
};

export default StatCard;
