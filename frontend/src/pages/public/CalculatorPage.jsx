import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import apiClient from '../../api/client';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import Card from '../../components/common/Card';
import BmiResultCard from '../../components/dashboard/BmiResultCard';
import './CalculatorPage.css';

const CalculatorPage = () => {
  const [unitSystem, setUnitSystem] = useState('METRIC');
  const [calculationResult, setCalculationResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState(null);

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
    reset,
  } = useForm({
    defaultValues: {
      name: '',
      phone: '',
      age: '',
      gender: 'Male',
      heightCm: '',
      heightFt: '',
      heightIn: '',
      weightKg: '',
      weightLbs: '',
    },
  });

  const onSubmit = async (data) => {
    setLoading(true);
    setApiError(null);
    
    // Construct values based on selected unit system
    let height = 0;
    let weight = 0;

    if (unitSystem === 'METRIC') {
      height = parseFloat(data.heightCm);
      weight = parseFloat(data.weightKg);
    } else {
      // Imperial height: feet & inches converted to total inches
      const feet = parseFloat(data.heightFt) || 0;
      const inches = parseFloat(data.heightIn) || 0;
      height = (feet * 12) + inches;
      weight = parseFloat(data.weightLbs);
    }

    const payload = {
      name: data.name,
      phone: data.phone,
      age: parseInt(data.age, 10),
      gender: data.gender,
      height,
      weight,
      unitSystem,
    };

    try {
      const response = await apiClient.post('/bmi/calculate', payload);
      setCalculationResult(response.data);
    } catch (error) {
      if (error.response) {
        if (error.response.status === 429) {
          setApiError('Rate limit exceeded: Please wait a moment before trying again.');
        } else {
          setApiError(error.response.data?.message || 'Error occurred while recording BMI calculation.');
        }
      } else {
        setApiError('Unable to connect to the medical servers. Please try again.');
      }
      setCalculationResult(null);
    } finally {
      setLoading(false);
    }
  };

  const handleUnitChange = (system) => {
    setUnitSystem(system);
    setApiError(null);
    // Clear calculation results on system switch to avoid mismatch
    setCalculationResult(null);
  };

  return (
    <div className="calculator-page-container container animate-fade-in">
      <div className="calculator-header">
        <h1>Clinical BMI Calculator</h1>
        <p className="calculator-lead">
          Calculate and log your body mass index directly into the SwasthyaLipi database.
        </p>
      </div>

      <div className="calculator-layout-grid">
        {/* Form panel */}
        <Card className="calculator-form-card">
          <div className="unit-toggle-row">
            <button
              type="button"
              className={`toggle-btn ${unitSystem === 'METRIC' ? 'active' : ''}`}
              onClick={() => handleUnitChange('METRIC')}
            >
              Metric System (cm / kg)
            </button>
            <button
              type="button"
              className={`toggle-btn ${unitSystem === 'IMPERIAL' ? 'active' : ''}`}
              onClick={() => handleUnitChange('IMPERIAL')}
            >
              Imperial System (ft / in / lbs)
            </button>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="calc-form">
            <h3 className="form-section-title">Patient Demographics</h3>
            
            <div className="form-group-row">
              <Input
                label="Full Name"
                placeholder="e.g. Robin Sharma"
                error={errors.name?.message}
                {...register('name', { required: 'Patient name is required' })}
              />
              <Input
                label="Phone Number"
                placeholder="10-digit number"
                error={errors.phone?.message}
                {...register('phone', {
                  required: 'Phone number is required',
                  pattern: {
                    value: /^[0-9]{10}$/,
                    message: 'Please enter a valid 10-digit phone number',
                  },
                })}
              />
            </div>

            <div className="form-group-row">
              <Input
                label="Age (years)"
                type="number"
                placeholder="e.g. 28"
                error={errors.age?.message}
                {...register('age', {
                  required: 'Age is required',
                  min: { value: 0, message: 'Age cannot be negative' },
                  max: { value: 120, message: 'Enter a valid clinical age' },
                })}
              />
              <div className="custom-input-wrapper">
                <label className="custom-input-label">Gender</label>
                <select 
                  className="custom-select"
                  {...register('gender', { required: 'Gender is required' })}
                >
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>
            </div>

            <h3 className="form-section-title">Physical Metrics</h3>
            
            {unitSystem === 'METRIC' ? (
              <div className="form-group-row">
                <Input
                  label="Height (cm)"
                  type="number"
                  step="0.1"
                  placeholder="e.g. 175"
                  error={errors.heightCm?.message}
                  {...register('heightCm', {
                    required: 'Height in cm is required',
                    min: { value: 50, message: 'Minimum height is 50 cm' },
                    max: { value: 250, message: 'Maximum height is 250 cm' },
                  })}
                />
                <Input
                  label="Weight (kg)"
                  type="number"
                  step="0.1"
                  placeholder="e.g. 72"
                  error={errors.weightKg?.message}
                  {...register('weightKg', {
                    required: 'Weight in kg is required',
                    min: { value: 2, message: 'Minimum weight is 2 kg' },
                    max: { value: 400, message: 'Maximum weight is 400 kg' },
                  })}
                />
              </div>
            ) : (
              <div className="form-group-row-imperial">
                <div className="imperial-height-group">
                  <div className="height-sub-input">
                    <Input
                      label="Height (ft)"
                      type="number"
                      placeholder="ft"
                      error={errors.heightFt?.message}
                      {...register('heightFt', {
                        required: 'Feet are required',
                        min: { value: 1, message: 'Min 1' },
                        max: { value: 8, message: 'Max 8' },
                      })}
                    />
                  </div>
                  <div className="height-sub-input">
                    <Input
                      label="Height (in)"
                      type="number"
                      placeholder="in"
                      error={errors.heightIn?.message}
                      {...register('heightIn', {
                        required: 'Inches are required',
                        min: { value: 0, message: 'Min 0' },
                        max: { value: 11, message: 'Max 11' },
                      })}
                    />
                  </div>
                </div>
                <div className="imperial-weight-input">
                  <Input
                    label="Weight (lbs)"
                    type="number"
                    step="0.1"
                    placeholder="lbs"
                    error={errors.weightLbs?.message}
                    {...register('weightLbs', {
                      required: 'Weight in lbs is required',
                      min: { value: 5, message: 'Min 5 lbs' },
                      max: { value: 900, message: 'Max 900 lbs' },
                    })}
                  />
                </div>
              </div>
            )}

            {apiError && (
              <div className="calc-api-error">
                <span className="error-icon">⚠️</span>
                <span>{apiError}</span>
              </div>
            )}

            <div className="submit-button-row">
              <Button type="submit" loading={loading} className="w-100">
                Log BMI Calculation
              </Button>
            </div>
          </form>
        </Card>

        {/* Results presentation panel */}
        <div className="results-panel">
          {calculationResult ? (
            <div className="result-outer-container animate-scale-in">
              <h3 className="result-title">Recorded Result</h3>
              <BmiResultCard
                bmiValue={calculationResult.bmiValue}
                classification={calculationResult.classification}
                height={calculationResult.height}
                weight={calculationResult.weight}
              />
              <div className="patient-success-notice">
                <span>✅ patient entry saved. Chronological records automatically merged under: <strong>{calculationResult.phoneNumber || watch('phone')}</strong>.</span>
              </div>
            </div>
          ) : (
            <div className="no-result-card">
              <span className="no-result-icon">📊</span>
              <h4>Awaiting Calculation Input</h4>
              <p>Fill out the diagnostics form and submit to see BMI score, WHO classification status, and clinical risk assessments.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CalculatorPage;
