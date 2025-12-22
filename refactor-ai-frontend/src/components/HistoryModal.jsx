import { useState, useEffect } from 'react';
import api from '../services/api';

function HistoryModal({ isOpen, onClose, token, onSelectHistory }) {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (isOpen) {
      loadHistory();
    }
  }, [isOpen]);

  const loadHistory = async () => {
    setLoading(true);
    try {
      const response = await api.getHistory(token);
      if (response.success) {
        setHistory(response.history || []);
      }
    } catch (error) {
      console.error('Failed to load history:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-2xl max-w-4xl w-full mx-4 max-h-[80vh] overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-4 flex justify-between items-center">
          <h2 className="text-2xl font-bold">Analysis History</h2>
          <button
            onClick={onClose}
            className="text-white hover:bg-white hover:bg-opacity-20 rounded-full p-2 transition"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto max-h-[calc(80vh-80px)]">
          {loading ? (
            <div className="text-center py-12 text-gray-400">Loading...</div>
          ) : history.length === 0 ? (
            <div className="text-center py-12 text-gray-400">
              <p className="text-lg">No history yet</p>
              <p className="text-sm mt-2">Your analyses will appear here</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {history.map((item) => (
                <div
                  key={item.id}
                  onClick={() => {
                    onSelectHistory(item);
                    onClose();
                  }}
                  className="p-4 border-2 rounded-lg hover:border-blue-500 hover:shadow-lg cursor-pointer transition"
                >
                  <div className="flex justify-between items-start mb-3">
                    <div className="flex items-center gap-2">
                      <span className="bg-red-100 text-red-700 px-3 py-1 rounded-full text-sm font-semibold">
                        {item.smellsCount} issues
                      </span>
                    </div>
                    <span className="text-xs text-gray-500">
                      {formatDate(item.createdAt)}
                    </span>
                  </div>
                  
                  <div className="text-sm text-gray-700 mb-2">
                    <span className="font-semibold">Types:</span> {item.smellTypes}
                  </div>
                  
                  <div className="bg-gray-100 p-2 rounded text-xs font-mono text-gray-600 overflow-hidden">
                    <div className="truncate">{item.originalCode.substring(0, 100)}...</div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default HistoryModal;