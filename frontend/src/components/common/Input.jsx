import React from 'react';
import './Input.css';

const Input = React.forwardRef(({
  label,
  error,
  type = 'text',
  name,
  placeholder,
  className = '',
  required = false,
  ...props
}, ref) => {
  return (
    <div className={`input-group-wrapper ${error ? 'has-error' : ''} ${className}`}>
      {label && (
        <label className="input-label" htmlFor={name}>
          {label} {required && <span className="required-asterisk">*</span>}
        </label>
      )}
      <input
        ref={ref}
        type={type}
        id={name}
        name={name}
        placeholder={placeholder}
        className="custom-input"
        {...props}
      />
      {error && <span className="input-error-msg">{error}</span>}
    </div>
  );
});

Input.displayName = 'Input';

export default Input;
