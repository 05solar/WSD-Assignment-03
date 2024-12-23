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
import reviewApp from './reviewAPI.js'; // reviewAPI.js 파일 가져오기






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
  apis: ['./userAPI.js', './jobAPI.js', './applicationAPI.js', './bookmarkAPI.js', './reviewAPI.js', './recommendAPI.js'] 
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


// 회원가입 (POST /auth/register)
app.post('/auth/register', async (req, res) => {
  const { email, password, name } = req.body;

  if (!email || !password || !name) {
    return res.status(400).json({ error: 'All fields are required' });
  }

  // 이메일 형식 검증
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return res.status(400).json({ error: 'Invalid email format' });
  }

  try {
    // 중복 회원 검사
    const [existingUser] = await db.execute('SELECT * FROM users WHERE email = ?', [email]);
    if (existingUser.length > 0) {
      return res.status(400).json({ error: 'Email is already registered' });
    }

    // 비밀번호 암호화
    const hashedPassword = Buffer.from(password).toString('base64');

    // 사용자 정보 저장
    await db.execute(
      'INSERT INTO users (user_id, email, password, name, createdAt, updatedAt) VALUES (UUID(), ?, ?, ?, NOW(), NOW())',
      [email, hashedPassword, name]
    );

    res.status(201).json({ message: 'User registered successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error registering user' });
  }
});

// 로그인 (POST /auth/login)
app.post('/auth/login', async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({ error: 'Email and password are required' });
  }

  try {
    // 사용자 인증
    const [rows] = await db.execute('SELECT * FROM users WHERE email = ?', [email]);
    if (rows.length === 0) {
      return res.status(404).json({ error: 'User not found' });
    }

    const user = rows[0];
    const decodedPassword = Buffer.from(user.password, 'base64').toString();
    if (password !== decodedPassword) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }

    // JWT 토큰 발급
    const token = jwt.sign({ user_id: user.user_id }, JWT_SECRET, { expiresIn: '1h' });
    const refreshToken = jwt.sign({ user_id: user.user_id }, JWT_SECRET, { expiresIn: '7d' });

    // 로그인 이력 저장
    await db.execute('UPDATE users SET last_login_at = NOW() WHERE user_id = ?', [user.user_id]);

    res.json({ accessToken: token, refreshToken });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error logging in user' });
  }
});

// 토큰 갱신 (POST /auth/refresh)
app.post('/auth/refresh', async (req, res) => {
  const { refreshToken } = req.body;

  if (!refreshToken) {
    return res.status(400).json({ error: 'Refresh token is required' });
  }

  try {
    // Refresh 토큰 검증
    const decoded = jwt.verify(refreshToken, JWT_SECRET);

    // 새로운 Access 토큰 발급
    const newAccessToken = jwt.sign({ user_id: decoded.user_id }, JWT_SECRET, { expiresIn: '1h' });

    res.json({ accessToken: newAccessToken });
  } catch (err) {
    console.error(err);
    res.status(401).json({ error: 'Invalid refresh token' });
  }
});

// 회원정보수정 (PUT /auth/profile)
app.put('/auth/profile', async (req, res) => {
  const token = req.headers.authorization?.split(' ')[1];
  const { password, name, phone } = req.body;

  if (!token) {
    return res.status(401).json({ error: 'Unauthorized' });
  }

  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    const updateFields = [];
    const params = [];

    // 비밀번호 변경
    if (password) {
      const hashedPassword = Buffer.from(password).toString('base64');
      updateFields.push('password = ?');
      params.push(hashedPassword);
    }

    // 프로필 정보 수정
    if (name) {
      updateFields.push('name = ?');
      params.push(name);
    }

    if (phone) {
      updateFields.push('phone = ?');
      params.push(phone);
    }

    if (updateFields.length === 0) {
      return res.status(400).json({ error: 'At least one field to update is required' });
    }

    params.push(decoded.user_id);
    const query = `UPDATE users SET ${updateFields.join(', ')}, updatedAt = NOW() WHERE user_id = ?`;
    await db.execute(query, params);

    res.json({ message: 'Profile updated successfully' });
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
