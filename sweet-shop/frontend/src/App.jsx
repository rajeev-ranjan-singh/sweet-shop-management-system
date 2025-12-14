import { useState, useEffect } from 'react';
import axios from 'axios';
const API_URL = 'http://localhost:8080/api';

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [role, setRole] = useState(localStorage.getItem('role'));
  const [view, setView] = useState('login'); 

  const logout = () => { setToken(null); setRole(null); localStorage.clear(); setView('login'); };

  return (
    <div className="container">
      <h1>🍬 Sweet Shop (Java Spring)</h1>
      {token && <div className="nav"><span>Welcome! Role: {role}</span><button onClick={logout}>Logout</button></div>}
      {!token && view === 'login' && <Login setToken={setToken} setRole={setRole} setView={setView} />}
      {!token && view === 'register' && <Register setView={setView} />}
      {token && <Dashboard token={token} role={role} />}
    </div>
  );
}

function Login({ setToken, setRole, setView }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const handleLogin = async () => {
    try {
      const res = await axios.post(`${API_URL}/auth/login`, { username, password });
      setToken(res.data.token); setRole(res.data.role);
      localStorage.setItem('token', res.data.token); localStorage.setItem('role', res.data.role);
    } catch (err) { setError('Invalid credentials'); }
  };
  return (<div><h2>Login</h2>{error && <p className="error">{error}</p>}<input placeholder="Username" onChange={e => setUsername(e.target.value)} /><input type="password" placeholder="Password" onChange={e => setPassword(e.target.value)} /><button onClick={handleLogin}>Login</button><p>Need an account? <a href="#" onClick={() => setView('register')}>Register</a></p></div>);
}

function Register({ setView }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('user');
  const handleRegister = async () => {
    try { await axios.post(`${API_URL}/auth/register`, { username, password, role }); alert('Registered!'); setView('login'); } catch(e) { alert('Failed'); }
  };
  return (<div><h2>Register</h2><input placeholder="Username" onChange={e => setUsername(e.target.value)} /><input type="password" placeholder="Password" onChange={e => setPassword(e.target.value)} /><select onChange={e => setRole(e.target.value)}><option value="user">User</option><option value="admin">Admin</option></select><button onClick={handleRegister}>Register</button><p>Have an account? <a href="#" onClick={() => setView('login')}>Login</a></p></div>);
}

function Dashboard({ token, role }) {
  const [sweets, setSweets] = useState([]);
  const [newSweet, setNewSweet] = useState({ name: '', category: '', price: '', quantity: '' });
  useEffect(() => { fetchSweets(); }, []);
  const fetchSweets = async () => { try { const res = await axios.get(`${API_URL}/sweets`, { headers: { Authorization: `Bearer ${token}` } }); setSweets(res.data); } catch(e) {} };
  const purchase = async (id) => { try { await axios.post(`${API_URL}/sweets/${id}/purchase`, {}, { headers: { Authorization: `Bearer ${token}` } }); fetchSweets(); } catch(e) { alert('Error'); } };
  const addSweet = async () => { await axios.post(`${API_URL}/sweets`, newSweet, { headers: { Authorization: `Bearer ${token}` } }); fetchSweets(); };
  const deleteSweet = async (id) => { if(!confirm("Are you sure?")) return; await axios.delete(`${API_URL}/sweets/${id}`, { headers: { Authorization: `Bearer ${token}` } }); fetchSweets(); };

  return (
    <div>
      <h2>Inventory</h2>
      {sweets.map(s => (<div key={s.id} className="sweet-card"><div><strong>{s.name}</strong> ({s.category}) - ${s.price} | Stock: {s.quantity}</div><div><button disabled={s.quantity < 1} onClick={() => purchase(s.id)}>Purchase</button>{role === 'admin' && <button onClick={() => deleteSweet(s.id)} style={{marginLeft: '5px', background: '#555'}}>Delete</button>}</div></div>))}
      {role === 'admin' && (<div style={{marginTop: '30px', borderTop: '2px solid #ddd', paddingTop: '20px'}}><h3>Admin: Add Sweet</h3><input placeholder="Name" onChange={e => setNewSweet({...newSweet, name: e.target.value})} /><input placeholder="Category" onChange={e => setNewSweet({...newSweet, category: e.target.value})} /><input placeholder="Price" type="number" onChange={e => setNewSweet({...newSweet, price: e.target.value})} /><input placeholder="Quantity" type="number" onChange={e => setNewSweet({...newSweet, quantity: e.target.value})} /><button onClick={addSweet}>Add Item</button></div>)}
    </div>
  );
}
export default App;
