/**
 * Format timestamp into standard local date format
 */
export const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  } catch (error) {
    return dateString;
  }
};

/**
 * Format timestamp to show date and time
 */
export const formatDateTime = (dateString) => {
  if (!dateString) return 'N/A';
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch (error) {
    return dateString;
  }
};

/**
 * Round BMI value to one decimal place
 */
export const formatBmi = (value) => {
  if (value === undefined || value === null) return 'N/A';
  return Number(value).toFixed(1);
};

/**
 * Convert Centimeters to Feet and Inches representation
 */
export const cmToFeetInches = (cm) => {
  const inchesTotal = cm / 2.54;
  const feet = Math.floor(inchesTotal / 12);
  const inches = Math.round(inchesTotal % 12);
  return { feet, inches };
};

/**
 * Convert Kilograms to Pounds
 */
export const kgToLbs = (kg) => {
  return Math.round(kg * 2.20462);
};

/**
 * Convert Feet and Inches to Centimeters
 */
export const feetInchesToCm = (feet, inches) => {
  const totalInches = (Number(feet) * 12) + Number(inches);
  return Number((totalInches * 2.54).toFixed(1));
};

/**
 * Convert Pounds to Kilograms
 */
export const lbsToKg = (lbs) => {
  return Number((Number(lbs) / 2.20462).toFixed(1));
};

/**
 * Map WHO classifications to visual classes and risk descriptions
 */
export const getWhoBadgeDetails = (classification) => {
  const key = (classification || '').toUpperCase();
  
  if (key.includes('UNDERWEIGHT')) {
    return {
      class: 'underweight',
      label: 'Underweight',
      risk: 'Nutritional Deficiency Risk',
      color: 'var(--status-warning)',
    };
  } else if (key.includes('NORMAL')) {
    return {
      class: 'normal',
      label: 'Normal Weight',
      risk: 'Low Risk / Healthy Status',
      color: 'var(--status-normal)',
    };
  } else if (key.includes('OVERWEIGHT')) {
    return {
      class: 'overweight',
      label: 'Overweight',
      risk: 'Increased Co-morbidity Risk',
      color: 'var(--status-warning)',
    };
  } else if (key.includes('OBESE') || key.includes('OBESITY')) {
    return {
      class: 'obese',
      label: 'Obese',
      risk: 'High Co-morbidity Risk',
      color: 'var(--status-danger)',
    };
  } else {
    return {
      class: 'unknown',
      label: classification || 'Unknown',
      risk: 'Undetermined Health Risk',
      color: 'var(--neutral-slate-medium)',
    };
  }
};
