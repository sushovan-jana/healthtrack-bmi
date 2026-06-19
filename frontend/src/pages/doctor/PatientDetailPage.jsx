import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import apiClient from '../../api/client';
import { formatDate, formatDateTime, formatBmi, getWhoBadgeDetails } from '../../utils/formatters';
import Card from '../../components/common/Card';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import BmiTrendChart from '../../components/dashboard/BmiTrendChart';
import './PatientDetailPage.css';

const PatientDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [patient, setPatient] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [chartData, setChartData] = useState([]);
  const [chartLoading, setChartLoading] = useState(true);
  const [pdfGenerating, setPdfGenerating] = useState(false);
  const [noteLoading, setNoteLoading] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    defaultValues: {
      noteContent: '',
    },
  });

  // Fetch Patient Details
  const fetchPatientDetails = async () => {
    try {
      const response = await apiClient.get(`/doctors/patients/${id}`);
      setPatient(response.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Patient details not found or inactive.');
    } finally {
      setLoading(false);
    }
  };

  // Fetch BMI Trend Chart Data
  const fetchBmiTrend = async () => {
    try {
      const response = await apiClient.get(`/doctors/patients/${id}/bmi-trend`);
      setChartData(response.data);
    } catch (err) {
      console.error('Error fetching trend data', err);
    } finally {
      setChartLoading(false);
    }
  };

  useEffect(() => {
    fetchPatientDetails();
    fetchBmiTrend();
  }, [id]);

  // Add Note Form Submit
  const onAddNote = async (data) => {
    setNoteLoading(true);
    try {
      const response = await apiClient.post(`/doctors/patients/${id}/notes`, {
        note: data.noteContent,
      });
      // Append the new note to patient notes state
      setPatient((prev) => ({
        ...prev,
        notes: [response.data, ...prev.notes],
      }));
      reset(); // Clear note field
    } catch (err) {
      alert(err.response?.data?.message || 'Error occurred while saving note.');
    } finally {
      setNoteLoading(false);
    }
  };

  // Delete Note Click
  const onDeleteNote = async (noteId) => {
    if (!window.confirm('Are you sure you want to delete this clinical recommendation?')) return;
    try {
      await apiClient.delete(`/doctors/notes/${noteId}`);
      // Remove note from state
      setPatient((prev) => ({
        ...prev,
        notes: prev.notes.filter((n) => n.id !== noteId),
      }));
    } catch (err) {
      alert(err.response?.data?.message || 'Error deleting note.');
    }
  };

  // Generate and Download PDF Report
  const handleGenerateReport = async () => {
    setPdfGenerating(true);
    try {
      const response = await apiClient.get(`/doctors/patients/${id}/report`, {
        responseType: 'blob',
      });
      
      const fileBlob = new Blob([response.data], { type: 'application/pdf' });
      const fileURL = URL.createObjectURL(fileBlob);
      
      // Dynamic link trigger
      const link = document.createElement('a');
      link.href = fileURL;
      const safeName = patient?.name.replace(/[^a-zA-Z0-9]/g, '_') || 'Patient';
      link.setAttribute('download', `report_${safeName}_${new Date().toISOString().slice(0,10)}.pdf`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (err) {
      console.error('PDF Generation Error:', err);
      // Attempt to parse blob error if backend sent a JSON error inside the blob
      if (err.response && err.response.data && err.response.data instanceof Blob) {
        const text = await err.response.data.text();
        try {
          const jsonError = JSON.parse(text);
          alert(`Failed to generate report: ${jsonError.message || jsonError.error || 'Server error'}`);
          return;
        } catch (e) {
          // not json
        }
      }
      alert('Failed to generate clinical PDF report. Ensure the backend has finished deploying the font updates.');
    } finally {
      setPdfGenerating(false);
    }
  };

  if (loading) {
    return (
      <div className="detail-loading-state">
        <div className="spinner"></div>
        <p>Retrieving patient clinical folder...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="detail-error-state container">
        <h2>⚠️ Navigation Error</h2>
        <p>{error}</p>
        <Button onClick={() => navigate('/dashboard')} variant="primary">
          Return to Directory
        </Button>
      </div>
    );
  }

  const latestCalc = patient?.calculations?.[0];
  const badge = getWhoBadgeDetails(latestCalc?.classification);

  return (
    <div className="patient-detail-container animate-fade-in">
      {/* Top Navigation Row */}
      <div className="detail-action-bar">
        <Button onClick={() => navigate('/dashboard')} variant="secondary" className="back-btn">
          ← Back to Directory
        </Button>
        <Button 
          onClick={handleGenerateReport} 
          variant="primary" 
          loading={pdfGenerating}
          className="report-btn"
        >
          📄 Generate PDF Report
        </Button>
      </div>

      {/* Split layout: Info on left, Chart on right */}
      <div className="detail-grid-top">
        {/* Profile Card */}
        <Card className="profile-summary-card">
          <div className="profile-card-header">
            <div className="detail-avatar">{patient.name.charAt(0).toUpperCase()}</div>
            <div className="detail-title-block">
              <h2>{patient.name}</h2>
              <span className="profile-phone">📱 {patient.phoneNumber}</span>
            </div>
          </div>

          <div className="profile-meta-grid">
            <div className="meta-box">
              <span className="meta-label">Patient Age</span>
              <span className="meta-value">{patient.age} years</span>
            </div>
            <div className="meta-box">
              <span className="meta-label">Gender</span>
              <span className="meta-value gender-capitalize">{patient.gender.toLowerCase()}</span>
            </div>
            <div className="meta-box">
              <span className="meta-label">Registered On</span>
              <span className="meta-value">{formatDate(patient.createdAt)}</span>
            </div>
            <div className="meta-box">
              <span className="meta-label">Last Updated</span>
              <span className="meta-value">{formatDate(patient.updatedAt)}</span>
            </div>
          </div>

          {latestCalc ? (
            <div className="latest-bmi-summary">
              <h4 className="meta-label">Current Diagnosis Status</h4>
              <div className="diag-badge-row">
                <span className="diag-bmi-num">{formatBmi(latestCalc.bmiValue)}</span>
                <span 
                  className={`who-badge badge-${badge.class}`}
                  style={{ borderColor: badge.color, color: badge.color, backgroundColor: `${badge.color}10` }}
                >
                  {badge.label}
                </span>
              </div>
              <p className="diag-risk-label">Risk assessment: <strong style={{ color: badge.color }}>{badge.risk}</strong></p>
            </div>
          ) : (
            <div className="latest-bmi-summary no-records">
              <p>No BMI diagnostic entries found on this profile.</p>
            </div>
          )}
        </Card>

        {/* Timeline Line Chart */}
        <div className="detail-chart-wrapper">
          {chartLoading ? (
            <div className="chart-skeleton-loader"></div>
          ) : (
            <BmiTrendChart trendData={chartData} />
          )}
        </div>
      </div>

      {/* Bottom Section: Notes & Calculations Table */}
      <div className="detail-grid-bottom">
        {/* Notes Recommendations section */}
        <div className="notes-column">
          <Card className="notes-card">
            <h3 className="section-title">Clinical Recommendations</h3>
            
            {/* New note form */}
            <form onSubmit={handleSubmit(onAddNote)} className="new-note-form">
              <div className="form-group-note">
                <textarea
                  className={`note-textarea ${errors.noteContent ? 'textarea-error' : ''}`}
                  placeholder="Type advice or doctor observations... (e.g. Reduce sugar intake, follow up in 30 days)"
                  rows="3"
                  {...register('noteContent', { 
                    required: 'Recommendation note cannot be empty',
                    maxLength: { value: 1000, message: 'Note length must not exceed 1000 characters' }
                  })}
                />
                {errors.noteContent && (
                  <span className="textarea-error-message">{errors.noteContent.message}</span>
                )}
              </div>
              <div className="note-submit-row">
                <Button type="submit" loading={noteLoading} variant="primary">
                  Save Recommendation
                </Button>
              </div>
            </form>

            {/* Notes timeline list */}
            <div className="notes-list-timeline">
              {patient.notes.length === 0 ? (
                <div className="empty-notes-timeline">
                  <span className="notes-empty-icon">📝</span>
                  <p>No doctor recommendations written yet.</p>
                </div>
              ) : (
                patient.notes.map((note) => (
                  <div key={note.id} className="note-timeline-item animate-scale-in">
                    <div className="note-item-header">
                      <span className="note-timestamp">{formatDateTime(note.createdAt)}</span>
                      <button 
                        onClick={() => onDeleteNote(note.id)} 
                        className="note-delete-btn"
                        title="Delete recommendation"
                      >
                        ✕
                      </button>
                    </div>
                    <p className="note-item-text">{note.note}</p>
                  </div>
                ))
              )}
            </div>
          </Card>
        </div>

        {/* Calculations Timeline Table */}
        <div className="history-column">
          <Card className="history-card">
            <h3 className="section-title">Diagnostic History</h3>
            <div className="history-table-wrapper">
              <table className="history-table">
                <thead>
                  <tr>
                    <th>Date & Time</th>
                    <th>Height</th>
                    <th>Weight</th>
                    <th>BMI Value</th>
                    <th>WHO Class</th>
                  </tr>
                </thead>
                <tbody>
                  {patient.calculations.length === 0 ? (
                    <tr>
                      <td colSpan="5" className="empty-table-cell text-center">
                        No calculation entries recorded.
                      </td>
                    </tr>
                  ) : (
                    patient.calculations.map((calc) => {
                      const calcBadge = getWhoBadgeDetails(calc.classification);
                      return (
                        <tr key={calc.id}>
                          <td className="font-semibold">{formatDateTime(calc.calculatedAt)}</td>
                          <td>{calc.height} cm</td>
                          <td>{calc.weight} kg</td>
                          <td className="font-bold">{formatBmi(calc.bmiValue)}</td>
                          <td>
                            <span 
                              className={`who-badge badge-${calcBadge.class}`}
                              style={{ 
                                borderColor: calcBadge.color, 
                                color: calcBadge.color, 
                                backgroundColor: `${calcBadge.color}10`,
                                fontSize: '0.75rem',
                                padding: '0.15rem 0.5rem'
                              }}
                            >
                              {calcBadge.label}
                            </span>
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default PatientDetailPage;
