import React from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import './PublicLayout.css';

const PublicLayout = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <div className="public-layout-wrapper">
      <header className="public-header">
        <div className="container public-header-container">
          <Link to="/" className="brand-logo">
            <span className="brand-icon">🔬</span>
            <span className="brand-text">SwasthyaLipi</span>
          </Link>
          <nav className="public-nav">
            <Link to="/bmi" className="nav-link">Calculate BMI</Link>
            {user ? (
              <>
                <Link to="/dashboard" className="nav-btn nav-btn-primary">Dashboard</Link>
                <button onClick={handleLogout} className="nav-btn nav-btn-secondary">Logout</button>
              </>
            ) : (
              <Link to="/login" className="nav-btn nav-btn-primary">Doctor Access</Link>
            )}
          </nav>
        </div>
      </header>

      <main className="public-main animate-fade-in">
        <Outlet />
      </main>

      <footer className="public-footer">
        <div className="container public-footer-container">
          <div className="footer-section brand-info">
            <h3>SwasthyaLipi</h3>
            <p>Premium Clinical Body Mass Index Monitoring System</p>
            <p className="doctor-tag">Under supervision of: <strong>Dr. Pulak Kumar Jana</strong></p>
          </div>
          <div className="footer-section contact-info">
            <h4>Contact Clinic</h4>
            <p>📍 54 Phool Bagan Road, Kolkata - 700086</p>
            <p>📞 8334008000 / 9681231942</p>
            <p>✉️ swasthyalipi@swasthyalipi.com</p>
          </div>
        </div>
        <div className="footer-bottom">
          <p>&copy; {new Date().getFullYear()} SwasthyaLipi. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
};

export default PublicLayout;
