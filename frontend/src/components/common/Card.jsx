import React from 'react';
import './Card.css';

const Card = ({
  children,
  className = '',
  onClick,
  ...props
}) => {
  return (
    <div
      className={`custom-card ${onClick ? 'card-clickable' : ''} ${className}`}
      onClick={onClick}
      {...props}
    >
      {children}
    </div>
  );
};

export default Card;
