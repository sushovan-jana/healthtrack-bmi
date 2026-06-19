import React, { useState, useEffect, useCallback } from 'react';
import apiClient from '../../api/client';
import StatCard from '../../components/dashboard/StatCard';
import SearchBar from '../../components/dashboard/SearchBar';
import PatientTable from '../../components/dashboard/PatientTable';
import Button from '../../components/common/Button';
import './DashboardPage.css';

const DashboardPage = () => {
  // Directory States
  const [patients, setPatients] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [patientsLoading, setPatientsLoading] = useState(false);

  // Analytics States
  const [analytics, setAnalytics] = useState(null);
  const [analyticsLoading, setAnalyticsLoading] = useState(true);

  // Notifications (Undo banner)
  const [undoTarget, setUndoTarget] = useState(null); // { id, name }
  const [notification, setNotification] = useState(null);

  // Fetch Analytics
  const fetchAnalytics = async () => {
    try {
      const response = await apiClient.get('/doctors/analytics');
      setAnalytics(response.data);
    } catch (error) {
      console.error('Error loading analytics', error);
    } finally {
      setAnalyticsLoading(false);
    }
  };

  // Fetch Patients Directory
  const fetchPatients = useCallback(async (query, pageNum) => {
    setPatientsLoading(true);
    try {
      const response = await apiClient.get('/doctors/patients', {
        params: {
          search: query,
          page: pageNum,
          size: 8, // Page size
        },
      });
      setPatients(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error('Error fetching patients directory', error);
    } finally {
      setPatientsLoading(false);
    }
  }, []);

  // Initial Load
  useEffect(() => {
    fetchAnalytics();
    fetchPatients(searchQuery, page);
  }, [fetchPatients]);

  // Search trigger (debounced by SearchBar)
  const handleSearch = (query) => {
    setSearchQuery(query);
    setPage(0); // Reset page to 0 on new search
    fetchPatients(query, 0);
  };

  // Page trigger
  const handlePageChange = (newPageNum) => {
    setPage(newPageNum);
    fetchPatients(searchQuery, newPageNum);
  };

  // Delete Patient (Soft Delete)
  const handleDeletePatient = async (id, name) => {
    try {
      await apiClient.delete(`/doctors/patients/${id}`);
      
      // Setup undo action carrier
      setUndoTarget({ id, name });
      setNotification(`Patient "${name}" has been soft-deleted.`);
      
      // Auto-clear notification after 8 seconds
      setTimeout(() => {
        setNotification((prev) => (prev && prev.includes(name) ? null : prev));
      }, 8000);

      // Reload directory & analytics
      fetchPatients(searchQuery, page);
      fetchAnalytics();
    } catch (error) {
      alert(error.response?.data?.message || 'Error occurred during patient deletion.');
    }
  };

  // Undo soft delete (Restore Patient)
  const handleUndoDelete = async () => {
    if (!undoTarget) return;
    try {
      await apiClient.post(`/doctors/patients/${undoTarget.id}/restore`);
      setNotification(null);
      setUndoTarget(null);
      
      // Reload directory & analytics
      fetchPatients(searchQuery, page);
      fetchAnalytics();
    } catch (error) {
      alert(error.response?.data?.message || 'Failed to restore patient.');
    }
  };

  return (
    <div className="dashboard-page-container container animate-fade-in">
      {/* Undo Notification banner */}
      {notification && (
        <div className="undo-toast-banner">
          <div className="toast-content">
            <span className="toast-icon">🗑️</span>
            <span className="toast-text">{notification}</span>
          </div>
          <button onClick={handleUndoDelete} className="toast-undo-btn">
            Undo Deletion
          </button>
        </div>
      )}

      {/* Analytics Cards Grid */}
      <section className="analytics-section">
        <h3 className="section-title">Clinic Performance Overview</h3>
        {analyticsLoading ? (
          <div className="analytics-skeleton-grid">
            <div className="skeleton-card"></div>
            <div className="skeleton-card"></div>
            <div className="skeleton-card"></div>
            <div className="skeleton-card"></div>
          </div>
        ) : (
          <div className="analytics-grid">
            <StatCard
              title="Active Patients"
              value={analytics?.totalPatients || 0}
              icon="👥"
              description="Registered patients in portal"
              className="stat-card-teal"
            />
            <StatCard
              title="Total Calculations"
              value={analytics?.totalBmiCalculations || 0}
              icon="🧮"
              description="Historical records checked"
              className="stat-card-slate"
            />
            <StatCard
              title="Avg BMI Score"
              value={analytics?.averageBmi ? analytics.averageBmi.toFixed(1) : 'N/A'}
              icon="📊"
              description="Patient index average"
              className="stat-card-teal"
            />
            <StatCard
              title="Highest BMI"
              value={analytics?.highestBmi ? analytics.highestBmi.toFixed(1) : 'N/A'}
              icon="📈"
              description="Critical boundary index"
              className="stat-card-danger"
            />
          </div>
        )}

        {/* WHO BMI Distribution Row */}
        {!analyticsLoading && analytics && (
          <div className="distribution-row">
            <div className="distribution-card">
              <h4 className="distribution-title">Patient Weight Category Distribution</h4>
              <div className="distribution-bar-grid">
                
                <div className="dist-bar-item">
                  <div className="dist-label-row">
                    <span className="dist-category">Underweight (&lt; 18.5)</span>
                    <span className="dist-count">{analytics.underweightPatientsCount || 0} patients</span>
                  </div>
                  <div className="dist-progress-track">
                    <div 
                      className="dist-progress-fill progress-underweight" 
                      style={{ width: `${Math.min(100, ((analytics.underweightPatientsCount || 0) / (analytics.totalPatients || 1)) * 100)}%` }}
                    ></div>
                  </div>
                </div>

                <div className="dist-bar-item">
                  <div className="dist-label-row">
                    <span className="dist-category">Normal Weight (18.5 - 24.9)</span>
                    <span className="dist-count">{analytics.normalWeightPatientsCount || 0} patients</span>
                  </div>
                  <div className="dist-progress-track">
                    <div 
                      className="dist-progress-fill progress-normal" 
                      style={{ width: `${Math.min(100, ((analytics.normalWeightPatientsCount || 0) / (analytics.totalPatients || 1)) * 100)}%` }}
                    ></div>
                  </div>
                </div>

                <div className="dist-bar-item">
                  <div className="dist-label-row">
                    <span className="dist-category">Overweight (25 - 29.9)</span>
                    <span className="dist-count">{analytics.overweightPatientsCount || 0} patients</span>
                  </div>
                  <div className="dist-progress-track">
                    <div 
                      className="dist-progress-fill progress-overweight" 
                      style={{ width: `${Math.min(100, ((analytics.overweightPatientsCount || 0) / (analytics.totalPatients || 1)) * 100)}%` }}
                    ></div>
                  </div>
                </div>

                <div className="dist-bar-item">
                  <div className="dist-label-row">
                    <span className="dist-category">Obese (&ge; 30)</span>
                    <span className="dist-count">{analytics.obesePatientsCount || 0} patients</span>
                  </div>
                  <div className="dist-progress-track">
                    <div 
                      className="dist-progress-fill progress-obese" 
                      style={{ width: `${Math.min(100, ((analytics.obesePatientsCount || 0) / (analytics.totalPatients || 1)) * 100)}%` }}
                    ></div>
                  </div>
                </div>

              </div>
            </div>
          </div>
        )}
      </section>

      {/* Directory Section */}
      <section className="directory-section">
        <div className="directory-header-row">
          <h3 className="section-title">Patient Directory Folder</h3>
          <SearchBar onSearch={handleSearch} />
        </div>

        <PatientTable
          patients={patients}
          currentPage={page}
          totalPages={totalPages}
          onPageChange={handlePageChange}
          onDeletePatient={handleDeletePatient}
          loading={patientsLoading}
        />
      </section>
    </div>
  );
};

export default DashboardPage;
