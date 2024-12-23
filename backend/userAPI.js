import express from 'express';
import jwt from 'jsonwebtoken';
import bcrypt from 'bcrypt';
import mysql from 'mysql2/promise';
import dotenv from 'dotenv';
import swaggerJsdoc from 'swagger-jsdoc';
import swaggerUi from 'swagger-ui-express';
import jobApp from './jobAPI.js'; // jobAPI.js 파일을 가져옵니다.
import applicationApp from './applicationAPI.js';
import bookmarkApp from './bookmarkAPI.js'; // bookmarkAPI.js 가져오기





dotenv.config();

const app = express();
app.use(express.json());

const db = await mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '0518',
  database: 'web03'
});


// JWT secret key
const JWT_SECRET = process.env.JWT_SECRET;

const swaggerOptions = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'WSD03 API',
      version: '1.0.0',
      description: 'API documentation for WSD03',
    },
    servers: [
      {
        url: 'http://localhost:3000',
        description: 'Local development server',
      },
    ],
    components: {
      schemas: {
        User: {
          type: 'object',
          properties: {
            email: {
              type: 'string',
              format: 'email',
              description: 'User email address',
            },
            password: {
              type: 'string',
              format: 'password',
              description: 'User password',
            },
            name: {
              type: 'string',
              description: 'User name',
            },
          },
          required: ['email', 'password', 'name'],
        },
      },
    },
  },
  apis: ['./userAPI.js', './jobAPI.js', './applicationAPI.js', './bookmarkAPI.js'] // bookmarkAPI.js 추가
};



const swaggerDocs = swaggerJsdoc(swaggerOptions);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocs));

/**
 * @swagger
 * tags:
 *   - name: 유저 관리
 *     description: 로그인 / 회원가입 / 유저 관리 / 유저 업데이트 
 */

/**
 * @swagger
 * /auth/register:
 *   post:
 *     tags:
 *       - 유저 관리
 *     summary: 회원가입
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/User'
 *     responses:
 *       201:
 *         description: User registered successfully
 *       400:
 *         description: Validation error
 */

/**
 * @swagger
 * /auth/login:
 *   post:
 *     tags:
 *       - 유저 관리
 *     summary: 로그인 
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/User'
 *     responses:
 *       200:
 *         description: Login successful
 *       401:
 *         description: Unauthorized
 */

/**
 * @swagger
 * /auth/profile:
 *   get:
 *     tags:
 *       - 유저 관리 
 *     summary: 유저 프로필 확인
 *     responses:
 *       200:
 *         description: User profile data
 *       401:
 *         description: Unauthorized
 */

/**
 * @swagger
 * /auth/profile:
 *   put:
 *     tags:
 *       - 유저 관리
 *     summary: 유저 프로필 업데이트 
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               name:
 *                 type: string
 *               phone:
 *                 type: string
 *     responses:
 *       200:
 *         description: User profile updated successfully
 *       400:
 *         description: Validation error
 *       401:
 *         description: Unauthorized
 */


// User registration
app.post('/auth/register', async (req, res) => {
  const { email, password, name } = req.body;

  if (!email || !password || !name) {
    return res.status(400).json({ error: 'All fields are required' });
  }

  try {
    const hashedPassword = await bcrypt.hash(password, 10);
    await db.execute('INSERT INTO users (user_id, email, password, name, createdAt, updatedAt) VALUES (UUID(), ?, ?, ?, NOW(), NOW())', [email, hashedPassword, name]);
    res.status(201).json({ message: 'User registered successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error registering user' });
  }
});

// User login
app.post('/auth/login', async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({ error: 'Email and password are required' });
  }

  try {
    const [rows] = await db.execute('SELECT * FROM users WHERE email = ?', [email]);
    if (rows.length === 0) {
      return res.status(404).json({ error: 'User not found' });
    }

    const user = rows[0];
    const isPasswordValid = await bcrypt.compare(password, user.password);
    if (!isPasswordValid) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }

    const token = jwt.sign({ user_id: user.user_id }, JWT_SECRET, { expiresIn: '1h' });
    res.json({ token });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error logging in user' });
  }
});

// Get user information
app.get('/auth/profile', async (req, res) => {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    return res.status(401).json({ error: 'Unauthorized' });
  }

  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    const [rows] = await db.execute('SELECT user_id, email, name, phone, createdAt, updatedAt FROM users WHERE user_id = ?', [decoded.user_id]);
    if (rows.length === 0) {
      return res.status(404).json({ error: 'User not found' });
    }

    res.json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(401).json({ error: 'Invalid token' });
  }
});

// Update user information
app.put('/auth/profile', async (req, res) => {
  const token = req.headers.authorization?.split(' ')[1];
  const { name, phone } = req.body;

  if (!token) {
    return res.status(401).json({ error: 'Unauthorized' });
  }

  try {
    const decoded = jwt.verify(token, JWT_SECRET);

    if (!name && !phone) {
      return res.status(400).json({ error: 'At least one field (name or phone) must be provided' });
    }

    const updateFields = [];
    const params = [];

    if (name) {
      updateFields.push('name = ?');
      params.push(name);
    }

    if (phone) {
      updateFields.push('phone = ?');
      params.push(phone);
    }

    params.push(decoded.user_id);

    const query = `UPDATE users SET ${updateFields.join(', ')}, updatedAt = NOW() WHERE user_id = ?`;
    await db.execute(query, params);

    res.json({ message: 'User profile updated successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error updating profile' });
  }
});

// Start the server
const PORT = 3000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
