import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import useAuth from '../../hooks/useAuth';
import Card from '../../components/common/Card';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import './LoginPage.css';

const LoginPage = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [authError, setAuthError] = useState(null);
  const [loading, setLoading] = useState(false);

  const from = location.state?.from?.pathname || '/dashboard';

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const onSubmit = async (data) => {
    setLoading(true);
    setAuthError(null);
    try {
      await login(data.email, data.password);
      navigate(from, { replace: true });
    } catch (error) {
      if (error.response) {
        if (error.response.status === 401 || error.response.status === 403) {
          setAuthError('Invalid email address or password.');
        } else {
          setAuthError(error.response.data?.message || 'Authentication error.');
        }
      } else {
        setAuthError('Unable to contact servers. Please check your network connection.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page-container container animate-fade-in">
      <Card className="login-form-card">
        <div className="login-header">
          <span className="login-icon">🩺</span>
          <h2>Doctor Workspace Login</h2>
          <p>Sign in using your clinical credentials</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="login-form">
          <Input
            label="Email Address"
            type="email"
            placeholder="e.g. doctor@swasthyalipi.com"
            error={errors.email?.message}
            {...register('email', {
              required: 'Email address is required',
              pattern: {
                value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                message: 'Invalid email address format',
              },
            })}
          />

          <Input
            label="Security Password"
            type="password"
            placeholder="••••••••"
            error={errors.password?.message}
            {...register('password', {
              required: 'Password is required',
              minLength: {
                value: 6,
                message: 'Password must be at least 6 characters',
              },
            })}
          />

          {authError && (
            <div className="login-api-error">
              <span className="error-icon">⚠️</span>
              <span>{authError}</span>
            </div>
          )}

          <div className="login-button-row">
            <Button type="submit" loading={loading} className="w-100">
              Sign In to Workspace
            </Button>
          </div>
        </form>
      </Card>
      
      <p className="login-notice">
        Access is restricted to authorized clinic staff of SwasthyaLipi.
      </p>
    </div>
  );
};

export default LoginPage;
