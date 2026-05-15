require('dotenv').config();
const express = require('express');
const cors = require('cors');
const morgan = require('morgan');
const jwt = require('jsonwebtoken');

const app = express();
const PORT = process.env.PORT || 3000;
const JWT_SECRET = process.env.JWT_SECRET || 'hasiru-dev-secret-change-in-production';

app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(morgan('dev'));

// In-memory store for demo; production uses Firebase Admin SDK
const db = {
  trees: [],
  pits: [],
  species: [
    { id: 'neem', nameEn: 'Neem', oxygenFactor: 1.5 },
    { id: 'peepal', nameEn: 'Peepal', oxygenFactor: 1.8 }
  ],
  users: [],
  leaderboard: []
};

function authMiddleware(req, res, next) {
  const header = req.headers.authorization;
  if (!header?.startsWith('Bearer ')) return res.status(401).json({ error: 'Unauthorized' });
  try {
    req.user = jwt.verify(header.slice(7), JWT_SECRET);
    next();
  } catch {
    res.status(401).json({ error: 'Invalid token' });
  }
}

// Auth
app.post('/api/auth/login', (req, res) => {
  const { email } = req.body;
  const token = jwt.sign({ email, uid: 'demo-user' }, JWT_SECRET, { expiresIn: '7d' });
  res.json({ token, user: { email, uid: 'demo-user' } });
});

// Trees
app.get('/api/trees', (req, res) => {
  const { city } = req.query;
  let trees = db.trees;
  if (city) trees = trees.filter(t => t.city === city);
  res.json(trees);
});

app.post('/api/trees', authMiddleware, (req, res) => {
  const tree = { id: Date.now().toString(), ...req.body, createdAt: Date.now() };
  tree.oxygenScore = (tree.girthCm || 30) * (tree.speciesFactor || 1.5);
  db.trees.push(tree);
  res.status(201).json(tree);
});

// Empty pits
app.get('/api/pits', (req, res) => {
  const { city } = req.query;
  let pits = db.pits;
  if (city) pits = pits.filter(p => p.city === city);
  res.json(pits);
});

app.post('/api/pits', authMiddleware, (req, res) => {
  const pit = { id: Date.now().toString(), ...req.body, createdAt: Date.now() };
  db.pits.push(pit);
  res.status(201).json(pit);
});

app.patch('/api/pits/:id/status', authMiddleware, (req, res) => {
  const pit = db.pits.find(p => p.id === req.params.id);
  if (!pit) return res.status(404).json({ error: 'Not found' });
  pit.status = req.body.status;
  pit.adminNotes = req.body.notes || '';
  res.json(pit);
});

// Analytics
app.get('/api/analytics/oxygen', (req, res) => {
  const { city } = req.query;
  const trees = city ? db.trees.filter(t => t.city === city) : db.trees;
  const totalOxygen = trees.reduce((sum, t) => sum + (t.oxygenScore || 0), 0);
  res.json({
    city: city || 'all',
    totalTrees: trees.length,
    totalOxygen,
    weeklyTrend: [10, 15, 22, 18, 30, 28, 35]
  });
});

// Species guide
app.get('/api/species', (_req, res) => res.json(db.species));

// Leaderboard
app.get('/api/leaderboard', (req, res) => {
  res.json(db.leaderboard.length ? db.leaderboard : [
    { userId: '1', userName: 'Green Warrior', points: 450, treesTagged: 23, rank: 1 },
    { userId: '2', userName: 'Tree Friend', points: 380, treesTagged: 19, rank: 2 }
  ]);
});

// Admin
app.get('/api/admin/stats', authMiddleware, (_req, res) => {
  res.json({
    totalUsers: db.users.length || 150,
    totalTrees: db.trees.length,
    totalPits: db.pits.length,
    oxygenIndex: db.trees.reduce((s, t) => s + (t.oxygenScore || 0), 0),
    activeAreas: ['Indiranagar', 'Jayanagar', 'VV Puram'],
    growthRate: 12.5
  });
});

app.get('/api/health', (_req, res) => res.json({ status: 'ok', service: 'hasiru-usiru-api' }));

app.listen(PORT, () => console.log(`Hasiru-Usiru API running on port ${PORT}`));
