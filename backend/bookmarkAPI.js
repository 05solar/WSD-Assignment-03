import express from 'express';
import mysql from 'mysql2/promise';
import jwt from 'jsonwebtoken';

const bookmarkApp = express.Router();
bookmarkApp.use(express.json());

const db = await mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '0518',
  database: 'web03'
});

// JWT secret key
const JWT_SECRET = process.env.JWT_SECRET;

/**
 * @swagger
 * tags:
 *   - name: 북마크 관리
 *     description: 북마크 추가/제거 및 조회
 */

/**
 * @swagger
 * /bookmarks:
 *   post:
 *     tags:
 *       - 북마크 관리
 *     summary: 북마크 추가/제거
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               job_id:
 *                 type: string
 *               note:
 *                 type: string
 *     responses:
 *       200:
 *         description: 북마크가 추가되거나 제거됨
 *       400:
 *         description: 요청 데이터 오류
 *       500:
 *         description: 서버 오류
 */

/**
 * @swagger
 * /bookmarks:
 *   get:
 *     tags:
 *       - 북마크 관리
 *     summary: 북마크 목록 조회
 *     parameters:
 *       - name: page
 *         in: query
 *         description: 페이지 번호
 *         required: false
 *         schema:
 *           type: integer
 *       - name: filter
 *         in: query
 *         description: "필터 조건 (예: 지역별, 경력별)"
 *         required: false
 *         schema:
 *           type: string
 *       - name: sort
 *         in: query
 *         description: "정렬 기준 (예: 최신순)"
 *         required: false
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: 북마크 목록 반환
 *       500:
 *         description: 서버 오류
 */

/**
 * @swagger
 * /jobs/{id}:
 *   get:
 *     tags:
 *       - 북마크 관리
 *     summary: 공고 상세 조회
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         schema:
 *           type: string
 *         description: 공고 ID
 *     responses:
 *       200:
 *         description: 공고 상세 정보
 *       500:
 *         description: 서버 오류
 */

// Middleware to verify JWT
bookmarkApp.use(async (req, res, next) => {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    return res.status(401).json({ error: 'Unauthorized' });
  }
  try {
    req.user = jwt.verify(token, JWT_SECRET);
    next();
  } catch (err) {
    return res.status(401).json({ error: 'Invalid token' });
  }
});

// 북마크 추가/제거
bookmarkApp.post('/bookmarks', async (req, res) => {
  const { job_id, note } = req.body;
  const user_id = req.user.user_id;

  if (!job_id) {
    return res.status(400).json({ error: 'Job ID is required' });
  }

  try {
    const [rows] = await db.execute('SELECT * FROM bookmarks WHERE job_id = ? AND user_id = ?', [job_id, user_id]);

    if (rows.length > 0) {
      // 이미 북마크가 존재하면 제거
      await db.execute('DELETE FROM bookmarks WHERE job_id = ? AND user_id = ?', [job_id, user_id]);
      return res.json({ message: 'Bookmark removed' });
    } else {
      // 북마크가 없으면 추가
      await db.execute(
        'INSERT INTO bookmarks (bookmark_id, user_id, job_id, note, createdAt, updatedAt) VALUES (UUID(), ?, ?, ?, NOW(), NOW())',
        [user_id, job_id, note || null]
      );
      return res.json({ message: 'Bookmark added' });
    }
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to process bookmark' });
  }
});

// 북마크 조회
bookmarkApp.get('/bookmarks', async (req, res) => {
  const { page = 1, filter, sort } = req.query;
  const user_id = req.user.user_id;

  const pageSize = 20;
  const offset = (page - 1) * pageSize;

  let query = 'SELECT * FROM bookmarks WHERE user_id = ?';
  const params = [user_id];

  if (filter) {
    query += ' AND category = ?';
    params.push(filter);
  }

  if (sort) {
    query += ` ORDER BY ${sort}`;
  } else {
    query += ' ORDER BY createdAt DESC';
  }

  query += ' LIMIT ? OFFSET ?';
  params.push(pageSize, offset);

  try {
    const [rows] = await db.execute(query, params);
    res.json(rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to fetch bookmarks' });
  }
});

// 공고 상세 조회
bookmarkApp.get('/jobs/:id', async (req, res) => {
  const { id } = req.params;

  try {
    const [rows] = await db.execute('SELECT * FROM jobs WHERE job_id = ?', [id]);
    if (rows.length === 0) {
      return res.status(404).json({ error: 'Job not found' });
    }

    // 조회수 증가
    await db.execute('UPDATE jobs SET views = views + 1 WHERE job_id = ?', [id]);

    res.json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to fetch job details' });
  }
});

export default bookmarkApp;
