import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate, useNavigate } from 'react-router-dom';
import { 
  LineChart, Line, XAxis, Tooltip, ResponsiveContainer, 
  Radar, RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, 
  CartesianGrid, BarChart, Bar, Legend, YAxis 
} from 'recharts';
import { motion } from 'framer-motion';
import { 
  TrendingUp, AlertTriangle, BookOpen, Award, Activity, 
  GraduationCap, LayoutDashboard, LogIn, LogOut, Lock, Mail, User 
} from 'lucide-react';

import { fetchTopStudents, fetchStudentDashboard, fetchFaculties, fetchSemesters, fetchStudentsList } from './api';

const Card = ({ children, className, delay = 0 }) => (
  <motion.div 
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.5, delay }}
    className={`bg-white rounded-2xl shadow-xl shadow-indigo-100/50 border border-slate-100 p-6 ${className}`}
  >
    {children}
  </motion.div>
);

const StatBox = ({ label, value, icon: Icon, color }) => (
  <div className="flex items-center gap-4">
    <div className={`p-3 rounded-xl text-white shadow-lg ${color}`}>
      <Icon size={24} />
    </div>
    <div>
      <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">{label}</p>
      <p className="text-2xl font-black text-slate-800">{value}</p>
    </div>
  </div>
);

const Login = ({ onLogin }) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    
    if (!email || !password) {
      setError('Будь ласка, заповніть всі поля');
      return;
    }

    setLoading(true);

    setTimeout(() => {
      setLoading(false);
      const userId = email.toLowerCase().includes('alex') ? "2" : "1";
      
      onLogin(userId);
      navigate('/dashboard');
    }, 1000);
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-slate-50">
      <motion.div 
        initial={{ scale: 0.9, opacity: 0 }} 
        animate={{ scale: 1, opacity: 1 }} 
        className="bg-white p-8 md:p-10 rounded-3xl shadow-2xl w-full max-w-md border border-slate-100"
      >
        <div className="text-center mb-8">
          <div className="bg-indigo-600 p-4 rounded-2xl w-fit mx-auto mb-6 text-white shadow-lg shadow-indigo-200">
            <GraduationCap size={40} />
          </div>
          <h1 className="text-3xl font-black text-slate-800 mb-2">З поверненням!</h1>
          <p className="text-slate-500">Введіть свої дані для входу в систему</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          
          <div className="space-y-2">
            <label className="text-sm font-bold text-slate-700 ml-1">Email</label>
            <div className="relative">
              <Mail size={20} className="absolute left-4 top-3.5 text-slate-400" />
              <input 
                type="email" 
                placeholder="student@university.edu" 
                className="w-full pl-12 pr-4 py-3 rounded-xl border border-slate-200 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 outline-none transition-all font-medium text-slate-700"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
          </div>

          <div className="space-y-2">
            <label className="text-sm font-bold text-slate-700 ml-1">Пароль</label>
            <div className="relative">
              <Lock size={20} className="absolute left-4 top-3.5 text-slate-400" />
              <input 
                type="password" 
                placeholder="••••••••" 
                className="w-full pl-12 pr-4 py-3 rounded-xl border border-slate-200 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 outline-none transition-all font-medium text-slate-700"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          {error && (
            <div className="text-rose-500 text-sm font-bold bg-rose-50 p-3 rounded-lg flex items-center gap-2">
              <AlertTriangle size={16} /> {error}
            </div>
          )}

          <button 
            type="submit" 
            disabled={loading}
            className="w-full bg-indigo-600 hover:bg-indigo-700 active:scale-95 text-white font-bold py-4 rounded-xl shadow-lg shadow-indigo-200 transition-all flex justify-center items-center gap-2 disabled:opacity-70 disabled:cursor-not-allowed"
          >
            {loading ? 'Вхід...' : 'Увійти'} <LogIn size={20} />
          </button>

          <div className="text-center">
            <p className="text-xs text-slate-400 mt-4">
              Демо доступ: <span className="font-mono text-indigo-500 bg-indigo-50 px-1 rounded">user@test.com</span> / <span className="font-mono text-indigo-500 bg-indigo-50 px-1 rounded">123456</span>
            </p>
          </div>
        </form>
      </motion.div>
    </div>
  );
};

const Dashboard = ({ studentId, onSelectStudent }) => {
  const [data, setData] = useState(null);
  const [students, setStudents] = useState([]);

  useEffect(() => {
    fetchStudentsList({ groupPrefix: 'ІПЗ-' }).then(setStudents);
  }, []);

  useEffect(() => {
    if (students.length === 0) {
      return;
    }
    const currentExists = students.some((s) => String(s.id) === String(studentId));
    if (!studentId || !currentExists) {
      onSelectStudent(String(students[0].id));
    }
  }, [students, studentId, onSelectStudent]);

  useEffect(() => {
    let cancelled = false;

    if (studentId) {
      setData(null);
      fetchStudentDashboard(studentId).then((response) => {
        if (!cancelled) {
          setData(response);
        }
      });
    }

    return () => {
      cancelled = true;
    };
  }, [studentId]);

  const chartColors = {
    text: '#64748b',
    grid: '#e2e8f0',
    tooltipBg: '#fff',
    tooltipText: '#0f172a'
  };

  if (!data) return <div className="flex h-screen items-center justify-center text-indigo-600 font-bold">Завантаження профілю...</div>;

  return (
    <div className="space-y-8">
       <header className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div>
            <h1 className="text-3xl md:text-4xl font-extrabold text-slate-900 tracking-tight mb-2">
              Привіт, {data.name.split(' ')[1]} 👋
            </h1>
            <p className="text-slate-500 font-medium">Ось твоя аналітика за поточний семестр</p>
          </div>
          <div className="flex flex-col md:flex-row gap-3 items-stretch md:items-center">
            <select
              className="rounded-xl border border-slate-200 px-3 py-2 text-sm min-w-[260px]"
              value={String(studentId)}
              onChange={(e) => onSelectStudent(e.target.value)}
            >
              {students.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.fullName} ({s.groupName || 'N/A'})
                </option>
              ))}
            </select>
            <div className="px-4 py-2 bg-gradient-to-r from-violet-600 to-indigo-600 text-white rounded-full text-sm font-bold shadow-lg flex items-center gap-2">
               <Award size={16} /> {data.cluster}
            </div>
          </div>
        </header>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card delay={0.1}><StatBox label="Середній бал" value={data.gpa} icon={Activity} color="bg-blue-500" /></Card>
        <Card delay={0.2}><StatBox label="Рейтинг" value={`#${data.ranking.stream}`} icon={TrendingUp} color="bg-emerald-500" /></Card>
        <Card delay={0.3}><StatBox label="Кредити" value={data.credits} icon={BookOpen} color="bg-violet-500" /></Card>
        <Card delay={0.4}>
          <div className="flex items-center gap-4">
            <div className={`p-3 rounded-xl text-white shadow-lg ${data.subjects.some(s => s.score < 50) ? 'bg-rose-500 animate-pulse' : 'bg-emerald-500'}`}>
              <AlertTriangle size={24} />
            </div>
            <div>
              <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">Статус</p>
              <p className={`text-xl font-black ${data.subjects.some(s => s.score < 50) ? 'text-rose-500' : 'text-emerald-500'}`}>
                 {data.subjects.some(s => s.score < 50) ? 'Є Борги' : 'Все ОК'}
              </p>
            </div>
          </div>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        <Card className="col-span-1 lg:col-span-2 h-96" delay={0.5}>
          <h3 className="text-lg font-bold text-slate-700 mb-6 flex items-center gap-2">
            <TrendingUp size={20} className="text-indigo-600"/> Порівняння: Ти vs Група
          </h3>
          <ResponsiveContainer width="100%" height="85%">
            <BarChart data={data.subjects} barGap={8}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke={chartColors.grid} />
              <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{fill: chartColors.text, fontSize: 12}} />
              <Tooltip cursor={{fill: '#f1f5f9'}} contentStyle={{borderRadius: '12px', border: 'none', backgroundColor: chartColors.tooltipBg, color: chartColors.tooltipText, boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'}} />
              <Legend iconType="circle" wrapperStyle={{ color: chartColors.text }} />
              <Bar dataKey="score" name="Твій бал" fill="#6366f1" radius={[4, 4, 0, 0]} barSize={30} />
              <Bar dataKey="avgGroup" name="Середнє по групі" fill="#cbd5e1" radius={[4, 4, 0, 0]} barSize={30} />
            </BarChart>
          </ResponsiveContainer>
        </Card>

        <Card className="h-96 flex flex-col items-center justify-center relative bg-gradient-to-b from-white to-indigo-50/30" delay={0.6}>
          <h3 className="absolute top-6 left-6 text-lg font-bold text-slate-700">Компетенції</h3>
          <ResponsiveContainer width="100%" height="100%">
            <RadarChart cx="50%" cy="50%" outerRadius="65%" data={data.radarData}>
              <PolarGrid stroke={chartColors.grid} />
              <PolarAngleAxis dataKey="subject" tick={{ fill: chartColors.text, fontSize: 11, fontWeight: 600 }} />
              <Radar name="Skills" dataKey="A" stroke="#8b5cf6" strokeWidth={3} fill="#8b5cf6" fillOpacity={0.5} />
              <Tooltip />
            </RadarChart>
          </ResponsiveContainer>
        </Card>
      </div>
      
      <Card className="h-80" delay={0.65}>
         <h3 className="text-lg font-bold text-slate-700 mb-6">Динаміка успішності</h3>
         <ResponsiveContainer width="100%" height="85%">
            <LineChart data={data.trendData}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke={chartColors.grid} />
              <XAxis dataKey="semester" axisLine={false} tickLine={false} tick={{fill: chartColors.text}} />
              <YAxis hide domain={[0, 100]} />
              <Tooltip contentStyle={{borderRadius: '12px', border: 'none', backgroundColor: chartColors.tooltipBg, color: chartColors.tooltipText}} />
              <Line type="monotone" dataKey="gpa" stroke="#6366f1" strokeWidth={4} dot={{r: 6, fill: '#6366f1', strokeWidth: 2, stroke: '#fff'}} />
            </LineChart>
         </ResponsiveContainer>
      </Card>

      <Card delay={0.7} className="overflow-hidden">
        <h3 className="text-lg font-bold text-slate-700 mb-4">Предмети та оцінки</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-left min-w-[600px]">
            <thead className="text-xs text-slate-400 uppercase border-b border-slate-100">
              <tr>
                <th className="pb-3 pl-2">Предмет</th>
                <th className="pb-3">Категорія</th>
                <th className="pb-3 text-center">Твій Бал</th>
                <th className="pb-3 text-center">Середній</th>
                <th className="pb-3 text-right">Статус</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50">
              {data.subjects.map((sub) => (
                <tr key={sub.id} className="group hover:bg-slate-50 transition-colors">
                  <td className="py-4 pl-2 font-bold text-slate-700">{sub.name}</td>
                  <td className="py-4 text-slate-500 text-sm">{sub.category}</td>
                  <td className={`py-4 text-center font-black ${sub.score < 50 ? 'text-rose-500' : 'text-slate-800'}`}>{sub.score}</td>
                  <td className="py-4 text-center text-slate-400">{sub.avgGroup}</td>
                  <td className="py-4 text-right pr-2">
                    {sub.score < 50 ? 
                      <span className="bg-rose-100 text-rose-600 px-3 py-1 rounded-full text-xs font-bold">BORG</span> : 
                      <span className="bg-emerald-100 text-emerald-600 px-3 py-1 rounded-full text-xs font-bold">OK</span>
                    }
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
};

const Ranking = () => {
  const [list, setList] = useState([]);
  const [faculties, setFaculties] = useState([]);
  const [semesters, setSemesters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [scope, setScope] = useState('STREAM');
  const [sortBy, setSortBy] = useState('PERFORMANCE');
  const [direction, setDirection] = useState('DESC');
  const [formOfStudy, setFormOfStudy] = useState('');
  const [debtFilter, setDebtFilter] = useState('ALL');
  const [semesterId, setSemesterId] = useState('SEM1');
  const [facultyId, setFacultyId] = useState('');
  const [limit, setLimit] = useState('1000');

  useEffect(() => {
    fetchFaculties().then(setFaculties);
    fetchSemesters().then((items) => {
      setSemesters(items);
      if (items.length > 0) {
        setSemesterId(items[0].id);
      }
    });
  }, []);

  useEffect(() => {
    if (scope === 'FACULTY' && facultyId === '') {
      setList([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    fetchTopStudents({
      semesterId,
      scope,
      facultyId: facultyId === '' ? null : Number(facultyId),
      formOfStudy: formOfStudy || null,
      debtFilter,
      sortBy,
      direction,
      limit: limit === 'ALL' ? 5000 : Number(limit),
    }).then((data) => {
      setList(data);
      setLoading(false);
    });
  }, [semesterId, scope, facultyId, formOfStudy, debtFilter, sortBy, direction, limit]);

  return (
    <Card>
      <h2 className="text-2xl font-bold text-slate-800 mb-4">Рейтинг</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-8 gap-3 mb-6">
        <select className="rounded-xl border border-slate-200 px-3 py-2 text-sm" value={scope} onChange={(e) => setScope(e.target.value)}>
          <option value="STREAM">Потік</option>
          <option value="FACULTY">Факультет</option>
          <option value="UNIVERSITY">Університет</option>
        </select>
        <select className="rounded-xl border border-slate-200 px-3 py-2 text-sm" value={semesterId} onChange={(e) => setSemesterId(e.target.value)}>
          {semesters.map((s) => (
            <option key={s.id} value={s.id}>{s.name || s.id}</option>
          ))}
          {semesters.length === 0 && <option value="SEM1">SEM1</option>}
        </select>
        <select className="rounded-xl border border-slate-200 px-3 py-2 text-sm" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
          <option value="PERFORMANCE">За успішністю</option>
          <option value="ALPHABET">За алфавітом</option>
          <option value="GROUP">За групою</option>
        </select>
        <select className="rounded-xl border border-slate-200 px-3 py-2 text-sm" value={direction} onChange={(e) => setDirection(e.target.value)}>
          <option value="DESC">Спадання</option>
          <option value="ASC">Зростання</option>
        </select>
        <select className="rounded-xl border border-slate-200 px-3 py-2 text-sm" value={formOfStudy} onChange={(e) => setFormOfStudy(e.target.value)}>
          <option value="">Форма навчання: всі</option>
          <option value="BUDGET">Бюджет</option>
          <option value="CONTRACT">Контракт</option>
        </select>
        <select className="rounded-xl border border-slate-200 px-3 py-2 text-sm" value={debtFilter} onChange={(e) => setDebtFilter(e.target.value)}>
          <option value="ALL">Борги: всі</option>
          <option value="WITH_DEBTS">Тільки боржники</option>
          <option value="WITHOUT_DEBTS">Тільки без боргів</option>
        </select>
        <select
          className="rounded-xl border border-slate-200 px-3 py-2 text-sm disabled:bg-slate-100"
          value={facultyId}
          onChange={(e) => setFacultyId(e.target.value)}
          disabled={scope !== 'FACULTY'}
        >
          <option value="">Факультет</option>
          {faculties.map((f) => (
            <option key={f.id} value={f.id}>{f.name}</option>
          ))}
        </select>
        <select className="rounded-xl border border-slate-200 px-3 py-2 text-sm" value={limit} onChange={(e) => setLimit(e.target.value)}>
          <option value="200">Ліміт: 200</option>
          <option value="500">Ліміт: 500</option>
          <option value="1000">Ліміт: 1000</option>
          <option value="ALL">Ліміт: всі</option>
        </select>
      </div>
      <div className="overflow-x-auto rounded-xl border border-slate-100">
        <table className="w-full text-left min-w-[700px]">
          <thead className="bg-slate-50 text-slate-500 text-xs uppercase font-bold">
            <tr>
              <th className="p-4">#</th>
              <th className="p-4">Студент</th>
              <th className="p-4">Група</th>
              <th className="p-4">Форма</th>
              <th className="p-4">Факультет</th>
              <th className="p-4 text-right">Бал</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 bg-white">
            {list.map((s, i) => {
              const isRisk = Number(s.debtCount || 0) > 0;
              return (
              <tr
                key={s.studentId || i}
                className={`${isRisk ? 'bg-rose-50 hover:bg-rose-100/60' : 'hover:bg-indigo-50/50'} transition-colors`}
              >
                <td className="p-4 font-bold text-slate-400">
                  {i === 0 ? '🥇' : i === 1 ? '🥈' : i === 2 ? '🥉' : i + 1}
                </td>
                <td className={`p-4 font-bold ${isRisk ? 'text-rose-700' : 'text-slate-700'}`}>
                  {s.fullName}
                  {isRisk ? ' (борг)' : ''}
                </td>
                <td className="p-4 text-slate-500 text-sm font-medium">{s.groupName}</td>
                <td className="p-4 text-slate-500 text-sm font-medium">{s.formOfStudy === 'BUDGET' ? 'Бюджет' : s.formOfStudy === 'CONTRACT' ? 'Контракт' : '-'}</td>
                <td className="p-4 text-slate-500 text-sm font-medium">{s.facultyName || '-'}</td>
                <td className={`p-4 text-right font-black text-lg ${isRisk ? 'text-rose-600' : 'text-indigo-600'}`}>
                  {Number(s.averageScore || 0).toFixed(1)}
                </td>
              </tr>
            )})}
          </tbody>
        </table>
        {list.length === 0 && !loading && (
             <div className="p-8 text-center text-slate-400">
                Список порожній або бекенд недоступний.
             </div>
        )}
      </div>
    </Card>
  );
};

export default function App() {
  const [studentId, setStudentId] = useState(localStorage.getItem('studentId'));

  const handleLogin = (id) => {
    setStudentId(id);
    localStorage.setItem('studentId', id);
  };

  const handleLogout = () => {
    setStudentId(null);
    localStorage.removeItem('studentId');
  };

  const handleStudentChange = (id) => {
    setStudentId(String(id));
    localStorage.setItem('studentId', String(id));
  };

  return (
    <Router>
      <div className="min-h-screen bg-slate-50 font-sans text-slate-800 flex flex-col md:flex-row">
        
        {studentId && (
          <>
            <nav className="hidden md:flex fixed w-20 h-full bg-slate-900 flex-col items-center py-10 gap-8 z-50 shadow-2xl">
              <div className="p-3 bg-indigo-500 rounded-xl text-white shadow-lg mb-4">
                <GraduationCap size={28} />
              </div>
              <Link to="/dashboard" className="p-3 text-slate-400 hover:text-white transition-colors">
                <LayoutDashboard size={28} />
              </Link>
              <Link to="/ranking" className="p-3 text-slate-400 hover:text-white transition-colors">
                <TrendingUp size={28} />
              </Link>
              <button onClick={handleLogout} className="mt-auto mb-8 text-rose-500 hover:text-rose-400 transition-colors">
                <LogOut size={28} />
              </button>
            </nav>

            <nav className="md:hidden fixed bottom-0 w-full bg-white border-t border-slate-200 flex justify-around py-3 z-50 px-6 safe-area-bottom">
              <Link to="/dashboard" className="text-slate-400 hover:text-indigo-600"><LayoutDashboard size={24} /></Link>
              <div className="relative -top-6 bg-indigo-600 p-4 rounded-full text-white shadow-lg border-4 border-slate-50">
                <GraduationCap size={24} />
              </div>
              <Link to="/ranking" className="text-slate-400 hover:text-indigo-600"><TrendingUp size={24} /></Link>
              <button onClick={handleLogout} className="text-rose-500"><LogOut size={24} /></button>
            </nav>
          </>
        )}

        <main className={`flex-1 p-6 md:p-12 max-w-[1600px] mx-auto pb-24 md:pb-12 ${studentId ? 'md:ml-20' : ''}`}>
          <Routes>
            <Route path="/login" element={!studentId ? <Login onLogin={handleLogin} /> : <Navigate to="/dashboard" />} />
            
            <Route path="/dashboard" element={studentId ? <Dashboard studentId={studentId} onSelectStudent={handleStudentChange} /> : <Navigate to="/login" />} />
            <Route path="/ranking" element={studentId ? <Ranking /> : <Navigate to="/login" />} />
            
            <Route path="*" element={<Navigate to="/login" />} />
          </Routes>
        </main>

      </div>
    </Router>
  );
}
