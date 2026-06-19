import React from 'react';
import { Link, Outlet, useNavigate, useLocation } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import './DoctorLayout.css';

const DoctorLayout = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="doctor-layout-wrapper">
      <aside className="doctor-sidebar">
        <div className="sidebar-header">
          <Link to="/dashboard" className="sidebar-logo">
            <span>🔬</span>
            <span className="brand-text">SwasthyaLipi</span>
          </Link>
          <span className="badge badge-normal">Doctor Panel</span>
        </div>

        <nav className="sidebar-menu">
          <Link 
            to="/dashboard" 
            className={`menu-item ${location.pathname === '/dashboard' ? 'active' : ''}`}
          >
            <span className="menu-icon">👥</span>
            <span className="menu-label">Patient Directory</span>
          </Link>
          <Link 
            to="/bmi" 
            target="_blank"
            className="menu-item"
          >
            <span className="menu-icon">🧮</span>
            <span className="menu-label">Open Calculator</span>
          </Link>
        </nav>

        <div className="sidebar-footer">
          <div className="doctor-profile">
            <div className="avatar">🩺</div>
            <div className="profile-details">
              <span className="doc-name">{user?.name || 'Dr. Pulak Kumar Jana'}</span>
              <span className="doc-email">{user?.email || 'swasthyalipi@swasthyalipi.com'}</span>
            </div>
          </div>
          <button onClick={handleLogout} className="logout-btn">
            🚪 Logout Session
          </button>
        </div>
      </aside>

      <div className="doctor-content-area">
        <header className="doctor-content-header">
          <div className="header-greeting">
            <h2>Welcome back, {user?.name?.split(' ')[0] || 'Doctor'}</h2>
            <p>Clinic Monitoring Systems | SwasthyaLipi</p>
          </div>
          <div className="header-status">
            <div className="status-indicator online"></div>
            <span>Connected to PostgreSQL</span>
          </div>
        </header>

        <main className="doctor-main-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default DoctorLayout;
