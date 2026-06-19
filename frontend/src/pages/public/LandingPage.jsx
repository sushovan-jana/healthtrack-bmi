import React from 'react';
import { Link } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import Card from '../../components/common/Card';
import './LandingPage.css';

const LandingPage = () => {
  const { user } = useAuth();

  return (
    <div className="landing-container container animate-fade-in">
      <div className="landing-hero">
        <h1 className="hero-title">Welcome to SwasthyaLipi</h1>
        <p className="hero-subtitle">
          Premium Clinical Body Mass Index (BMI) Monitoring & Analytics Portal.
        </p>
        <p className="hero-lead">
          Providing instant WHO-grade BMI classifications, trend diagnostics, and automated medical reports for patients and clinic staff.
        </p>
      </div>

      <div className="landing-grid">
        {/* Patient Calculator Card */}
        <Link to="/bmi" className="landing-card-link">
          <Card className="landing-card">
            <div className="card-emoji-header">🧮</div>
            <h2>Patient Calculator</h2>
            <p>
              Submit height and weight details to calculate your BMI index instantly. 
              Supports metric (cm/kg) and imperial (feet/inches/lbs) unit configurations.
            </p>
            <span className="card-action-text">Compute BMI now →</span>
          </Card>
        </Link>

        {/* Doctor Login Card */}
        <Link to={user ? "/dashboard" : "/login"} className="landing-card-link">
          <Card className="landing-card card-teal-accent">
            <div className="card-emoji-header">🩺</div>
            <h2>Doctor Access Portal</h2>
            <p>
              Access doctor workspace to review patient lists, audit BMI timeline analytics, 
              write medical recommendations, and generate professional PDF clinical reports.
            </p>
            <span className="card-action-text">
              {user ? "Go to Dashboard →" : "Sign in to workspace →"}
            </span>
          </Card>
        </Link>
      </div>

      <div className="landing-features">
        <div className="feature-item">
          <span className="feature-icon">🛡️</span>
          <h4>Rate-Protected Calculations</h4>
          <p>Strict rate limits protect clinical servers from request floods.</p>
        </div>
        <div className="feature-item">
          <span className="feature-icon">📁</span>
          <h4>Continuous Clinical Folders</h4>
          <p>Patients are identified securely by phone numbers to retain chronological histories.</p>
        </div>
        <div className="feature-item">
          <span className="feature-icon">📄</span>
          <h4>Downloadable PDF Reports</h4>
          <p>Generate styled medical PDF sheets containing BMI history charts and doctor notes.</p>
        </div>
      </div>
    </div>
  );
};

export default LandingPage;
