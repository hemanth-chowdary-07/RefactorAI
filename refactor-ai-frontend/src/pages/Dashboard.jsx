import { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import Editor from '@monaco-editor/react';
import api from '../services/api';
import HistoryModal from '../components/HistoryModal';

function Dashboard() {
  const { user, logout, token } = useContext(AuthContext);
  const [code, setCode] = useState(`public class Example {
    public void method() {
        int x = 100;
        int y = 200;
        
        if (x > 50) {
            if (y > 100) {
                if (x + y > 250) {
                    System.out.println("Result");
                }
            }
        }
    }
}`);
  const [analyzing, setAnalyzing] = useState(false);
  const [results, setResults] = useState(null);
  const [showHistory, setShowHistory] = useState(false);

  const handleAnalyze = async () => {
    setAnalyzing(true);
    setResults(null); // Clear old results
    try {
      const response = await api.refactorCode(code, token);
      setResults(response);
    } catch (error) {
      setResults({
        error: true,
        message: error.message || 'Analysis failed. Please check if the backend is running.'
      });
    } finally {
      setAnalyzing(false);
    }
  };

  const loadSampleCode = () => {
    setCode(`public class Calculator {
    
    public void calculate() {
        int x = 100;
        int y = 200;
        
        if (x > 50) {
            if (y > 100) {
                if (x + y > 250) {
                    if (x * y > 10000) {
                        System.out.println("Big numbers!");
                    }
                }
            }
        }
    }
    
    public String processData(String data) {
        String result = "";
        for (int i = 0; i < 100; i++) {
            result = result + data + i;
        }
        return result;
    }
}`);
    setResults(null);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation Bar */}
      <nav className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-gray-800">RefactorAI</h1>
            <p className="text-sm text-gray-600">Welcome, {user.username}</p>
          </div>
          <div className="flex gap-3">
            <button
              onClick={() => setShowHistory(true)}
              className="bg-blue-500 text-white px-6 py-2 rounded-lg hover:bg-blue-600 transition"
            >
              History
            </button>
            <button
              onClick={logout}
              className="bg-red-500 text-white px-6 py-2 rounded-lg hover:bg-red-600 transition"
            >
              Logout
            </button>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto p-6">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          
          {/* Code Editor */}
          <div className="bg-white rounded-lg shadow-lg p-6">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-gray-800">Code Editor</h2>
              <div className="flex gap-2">
                <button
                  onClick={loadSampleCode}
                  className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300 transition text-sm"
                >
                  Load Sample
                </button>
                <button
                  onClick={handleAnalyze}
                  disabled={analyzing}
                  className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-2 rounded-lg hover:from-blue-700 hover:to-purple-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {analyzing ? 'Analyzing...' : 'Analyze & Refactor'}
                </button>
              </div>
            </div>
            
            <div className="border rounded-lg overflow-hidden">
              <Editor
                height="600px"
                defaultLanguage="java"
                value={code}
                onChange={(value) => setCode(value || '')}
                theme="vs-dark"
                options={{
                  minimap: { enabled: false },
                  fontSize: 14,
                  lineNumbers: 'on',
                  scrollBeyondLastLine: false,
                }}
              />
            </div>
          </div>

          {/* Results Panel */}
          <div className="bg-white rounded-lg shadow-lg p-6">
            <h2 className="text-xl font-bold text-gray-800 mb-4">Analysis Results</h2>
            
            {analyzing ? (
              <div className="flex flex-col items-center justify-center h-[600px] text-gray-400">
                <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-blue-600 mb-4"></div>
                <p className="text-lg font-semibold text-gray-700">Analyzing your code...</p>
                <p className="text-sm mt-2">AI is working its magic ✨</p>
              </div>
            ) : !results ? (
              <div className="flex items-center justify-center h-[600px] text-gray-400">
                <div className="text-center">
                  <div className="text-6xl mb-4">🔍</div>
                  <p className="text-lg font-semibold text-gray-700">No analysis yet</p>
                  <p className="text-sm mt-2">Paste your Java code and click "Analyze & Refactor"</p>
                  <p className="text-sm text-blue-600 mt-4">💡 Try clicking "Load Sample" to get started!</p>
                </div>
              </div>
            ) : results.error ? (
              <div className="flex items-center justify-center h-[600px]">
                <div className="text-center">
                  <div className="text-6xl mb-4">❌</div>
                  <p className="text-lg text-red-600 font-semibold mb-2">Analysis Failed</p>
                  <p className="text-sm text-gray-600 mb-4">{results.message}</p>
                  <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-4">
                    <p className="text-sm text-yellow-800">
                      <strong>Tip:</strong> Make sure your backend is running on port 8080
                    </p>
                  </div>
                  <button
                    onClick={handleAnalyze}
                    className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition"
                  >
                    Try Again
                  </button>
                </div>
              </div>
            ) : (
              <div className="space-y-4 max-h-[600px] overflow-y-auto">
                {/* Code Smells */}
                {results.detectedSmells && results.detectedSmells.length > 0 && (
                  <div>
                    <h3 className="font-semibold text-gray-700 mb-2">
                      Detected Issues ({results.detectedSmells.length})
                    </h3>
                    <div className="space-y-2">
                      {results.detectedSmells.map((smell, idx) => (
                        <div key={idx} className="bg-red-50 border-l-4 border-red-500 p-3 rounded">
                          <p className="font-semibold text-red-700">{smell.type}</p>
                          <p className="text-sm text-red-600">{smell.location}</p>
                          <p className="text-sm text-gray-600 mt-1">{smell.description}</p>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* Refactored Code Preview */}
                {results.refactoredCode && (
                  <div>
                    <h3 className="font-semibold text-gray-700 mb-2">✨ Refactored Code</h3>
                    <div className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto">
                      <pre className="text-sm">{results.refactoredCode}</pre>
                    </div>
                  </div>
                )}
                
                {/* Success badge if saved */}
                {results.saved && (
                  <div className="bg-green-50 border border-green-200 rounded-lg p-3 flex items-center gap-2">
                    <span className="text-green-600 text-xl">✓</span>
                    <span className="text-green-700 font-medium">Saved to your history!</span>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* History Modal */}
      <HistoryModal
        isOpen={showHistory}
        onClose={() => setShowHistory(false)}
        token={token}
        onSelectHistory={(item) => {
          setCode(item.originalCode);
          setResults({
            originalCode: item.originalCode,
            refactoredCode: item.refactoredCode,
            diff: item.diff,
            detectedSmells: [],
            saved: true
          });
        }}
      />
    </div>
  );
}

export default Dashboard;