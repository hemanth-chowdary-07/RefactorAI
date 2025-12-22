import { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import Editor from '@monaco-editor/react';
import api from '../services/api';

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

  const handleAnalyze = async () => {
    setAnalyzing(true);
    try {
      const response = await api.refactorCode(code, token);
      setResults(response);
    } catch (error) {
      alert('Analysis failed: ' + error.message);
    } finally {
      setAnalyzing(false);
    }
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
          <button
            onClick={logout}
            className="bg-red-500 text-white px-6 py-2 rounded-lg hover:bg-red-600 transition"
          >
            Logout
          </button>
        </div>
      </nav>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto p-6">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          
          {/* Code Editor */}
          <div className="bg-white rounded-lg shadow-lg p-6">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-gray-800">Code Editor</h2>
              <button
                onClick={handleAnalyze}
                disabled={analyzing}
                className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-2 rounded-lg hover:from-blue-700 hover:to-purple-700 transition disabled:opacity-50"
              >
                {analyzing ? 'Analyzing...' : 'Analyze & Refactor'}
              </button>
            </div>
            
            <div className="border rounded-lg overflow-hidden">
              <Editor
                height="500px"
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
            
            {!results ? (
              <div className="flex items-center justify-center h-[500px] text-gray-400">
                <div className="text-center">
                  <p className="text-lg">No analysis yet</p>
                  <p className="text-sm mt-2">Click "Analyze & Refactor" to start</p>
                </div>
              </div>
            ) : (
              <div className="space-y-4">
                {/* Code Smells */}
                <div>
                  <h3 className="font-semibold text-gray-700 mb-2">
                    Detected Issues ({results.detectedSmells?.length || 0})
                  </h3>
                  <div className="space-y-2 max-h-60 overflow-y-auto">
                    {results.detectedSmells?.map((smell, idx) => (
                      <div key={idx} className="bg-red-50 border-l-4 border-red-500 p-3 rounded">
                        <p className="font-semibold text-red-700">{smell.type}</p>
                        <p className="text-sm text-red-600">{smell.location}</p>
                        <p className="text-sm text-gray-600 mt-1">{smell.description}</p>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Refactored Code Preview */}
                {results.refactoredCode && (
                  <div>
                    <h3 className="font-semibold text-gray-700 mb-2">Refactored Code Preview</h3>
                    <div className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto">
                      <pre className="text-sm">{results.refactoredCode}</pre>
                    </div>
                  </div>
                )}

                {/* Success Message */}
                {results.saved && (
                  <div className="bg-green-50 border-l-4 border-green-500 p-3 rounded">
                    <p className="text-green-700 font-semibold">✓ Analysis saved to history!</p>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;