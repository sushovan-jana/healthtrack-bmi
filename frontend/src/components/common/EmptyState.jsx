import React from 'react';
import './EmptyState.css';

const EmptyState = ({
  icon = '🔍',
  title = 'No Results Found',
  description = 'Try adjusting your search filters or add a new patient calculation.',
  action,
}) => {
  return (
    <div className="empty-state-container animate-fade-in">
      <div className="empty-state-icon">{icon}</div>
      <h3 className="empty-state-title">{title}</h3>
      <p className="empty-state-description">{description}</p>
      {action && <div className="empty-state-action">{action}</div>}
    </div>
  );
};

export default EmptyState;
