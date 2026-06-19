import React from 'react';
import './LoadingSkeleton.css';

const LoadingSkeleton = ({
  variant = 'block',
  width = '100%',
  height = '20px',
  className = '',
}) => {
  return (
    <div
      className={`skeleton skeleton-${variant} ${className}`}
      style={{ width, height }}
    />
  );
};

export default LoadingSkeleton;
