import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './routes/ProtectedRoute';
import PublicOnlyRoute from './routes/PublicOnlyRoute';

// Layouts
import PublicLayout from './layouts/PublicLayout';
import DoctorLayout from './layouts/DoctorLayout';

// Pages
import LandingPage from './pages/public/LandingPage';
import CalculatorPage from './pages/public/CalculatorPage';
import LoginPage from './pages/doctor/LoginPage';
import DashboardPage from './pages/doctor/DashboardPage';
import PatientDetailPage from './pages/doctor/PatientDetailPage';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public Routes (Visible to Everyone) */}
          <Route element={<PublicLayout />}>
            <Route path="/" element={<LandingPage />} />
            <Route path="/bmi" element={<CalculatorPage />} />
          </Route>

          {/* Public Only Routes (LoggedIn Doctors cannot view) */}
          <Route element={<PublicOnlyRoute />}>
            <Route path="/login" element={<LoginPage />} />
          </Route>

          {/* Protected Routes (Authenticated Doctors Only) */}
          <Route element={<ProtectedRoute />}>
            <Route element={<DoctorLayout />}>
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/patients/:id" element={<PatientDetailPage />} />
            </Route>
          </Route>

          {/* Fallback Catch-All Redirect */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
