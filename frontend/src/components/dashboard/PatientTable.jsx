import React from 'react';
import { useNavigate } from 'react-router-dom';
import { formatDate, formatBmi, getWhoBadgeDetails } from '../../utils/formatters';
import Button from '../common/Button';
import './PatientTable.css';

const PatientTable = ({
  patients = [],
  currentPage = 0,
  totalPages = 0,
  onPageChange,
  onDeletePatient,
  loading = false,
}) => {
  const navigate = useNavigate();

  const handleRowClick = (patientId) => {
    navigate(`/patients/${patientId}`);
  };

  if (loading) {
    return (
      <div className="table-loading-state">
        <div className="table-spinner"></div>
        <p>Loading patient directory...</p>
      </div>
    );
  }

  return (
    <div className="patient-table-container">
      <div className="table-responsive">
        <table className="patient-table">
          <thead>
            <tr>
              <th>Patient Name</th>
              <th>Phone Number</th>
              <th>Age</th>
              <th>Gender</th>
              <th>Latest BMI Result</th>
              <th>WHO Classification</th>
              <th className="text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            {patients.length === 0 ? (
              <tr>
                <td colSpan="7" className="empty-table-cell">
                  <div className="empty-table-wrapper">
                    <span className="empty-icon">👥</span>
                    <p className="empty-text">No active patients found in system directory.</p>
                  </div>
                </td>
              </tr>
            ) : (
              patients.map((patient) => {
                const latestCalc = patient.latestBmiValue;
                const latestClass = patient.latestClassification;
                const latestDate = patient.latestCalculatedAt;
                const badge = getWhoBadgeDetails(latestClass);

                return (
                  <tr key={patient.id} className="patient-row-hover">
                    <td className="patient-name-cell" onClick={() => handleRowClick(patient.id)}>
                      <div className="patient-avatar-name">
                        <div className="name-avatar">
                          {patient.name.charAt(0).toUpperCase()}
                        </div>
                        <div className="name-details">
                          <span className="patient-name">{patient.name}</span>
                          {latestDate && (
                            <span className="patient-meta-date">
                              Last calc: {formatDate(latestDate)}
                            </span>
                          )}
                        </div>
                      </div>
                    </td>
                    <td>{patient.phoneNumber}</td>
                    <td>{patient.age} yrs</td>
                    <td className="gender-capitalize">{patient.gender.toLowerCase()}</td>
                    <td>
                      {latestCalc ? (
                        <span className="bmi-value-highlight">
                          {formatBmi(latestCalc)}
                        </span>
                      ) : (
                        <span className="text-muted">No records</span>
                      )}
                    </td>
                    <td>
                      {latestClass ? (
                        <span 
                          className={`who-badge badge-${badge.class}`}
                          style={{ borderColor: badge.color, color: badge.color, backgroundColor: `${badge.color}10` }}
                        >
                          {badge.label}
                        </span>
                      ) : (
                        <span className="who-badge badge-unknown">N/A</span>
                      )}
                    </td>
                    <td>
                      <div className="action-buttons-group">
                        <Button
                          variant="secondary"
                          className="btn-action"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleRowClick(patient.id);
                          }}
                        >
                          View Folder
                        </Button>
                        <Button
                          variant="danger"
                          className="btn-action btn-delete-patient"
                          onClick={(e) => {
                            e.stopPropagation();
                            if (window.confirm(`Are you sure you want to soft delete patient "${patient.name}"?`)) {
                              onDeletePatient(patient.id, patient.name);
                            }
                          }}
                        >
                          Delete
                        </Button>
                      </div>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className="pagination-bar">
          <span className="pagination-info">
            Showing Page <strong>{currentPage + 1}</strong> of <strong>{totalPages}</strong>
          </span>
          <div className="pagination-buttons">
            <Button
              variant="secondary"
              disabled={currentPage === 0}
              onClick={() => onPageChange(currentPage - 1)}
            >
              Previous Page
            </Button>
            <Button
              variant="secondary"
              disabled={currentPage >= totalPages - 1}
              onClick={() => onPageChange(currentPage + 1)}
            >
              Next Page
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};

export default PatientTable;
