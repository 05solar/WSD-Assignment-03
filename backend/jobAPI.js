import express from 'express';
import mysql from 'mysql2/promise';
import jwt from 'jsonwebtoken';

const jobApp = express.Router();
jobApp.use(express.json());

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
 *   - name: 채용공고 관리
 *     description: 채용공고 생성, 조회, 수정, 삭제
 */

/**
 * @swagger
 * /jobs:
 *   get:
 *     tags:
 *       - 채용공고 관리
 *     summary: 채용공고 목록 조회
 *     parameters:
 *       - name: search
 *         in: query
 *         description: 검색어
 *         required: false
 *         schema:
 *           type: string
 *       - name: filter
 *         in: query
 *         description: 필터 조건
 *         required: false
 *         schema:
 *           type: string
 *       - name: sort
 *         in: query
 *         description: 정렬 조건
 *         required: false
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: 채용공고 목록
 */

/**
 * @swagger
 * /jobs/{id}:
 *   get:
 *     tags:
 *       - 채용공고 관리
 *     summary: 채용공고 상세 조회
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         schema:
 *           type: string
 *         description: 공고 ID
 *     responses:
 *       200:
 *         description: 공고 세부 정보
 */

/**
 * @swagger
 * /jobs:
 *   post:
 *     tags:
 *       - 채용공고 관리
 *     summary: 채용공고 추가
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               title:
 *                 type: string
 *               description:
 *                 type: string
 *               requirements:
 *                 type: string
 *               salary:
 *                 type: string
 *     responses:
 *       201:
 *         description: 채용공고 생성 성공
 */

/**
 * @swagger
 * /jobs/{id}:
 *   put:
 *     tags:
 *       - 채용공고 관리
 *     summary: 채용공고 수정
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         schema:
 *           type: string
 *         description: 공고 ID
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               title:
 *                 type: string
 *               description:
 *                 type: string
 *     responses:
 *       200:
 *         description: 채용공고 수정 성공
 */

/**
 * @swagger
 * /jobs/{id}:
 *   delete:
 *     tags:
 *       - 채용공고 관리
 *     summary: 채용공고 삭제
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         schema:
 *           type: string
 *         description: 공고 ID
 *     responses:
 *       200:
 *         description: 채용공고 삭제 성공
 */

// 채용공고 목록 조회
jobApp.get('/jobs', async (req, res) => {
  const { search, filter, sort } = req.query;

  let query = 'SELECT * FROM jobs WHERE 1=1';
  const params = [];

  if (search) {
    query += ' AND title LIKE ?';
    params.push(`%${search}%`);
  }

  if (filter) {
    query += ' AND employment_type = ?';
    params.push(filter);
  }

  if (sort) {
    query += ` ORDER BY ${sort}`;
  }

  try {
    const [rows] = await db.execute(query, params);
    res.json(rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to fetch jobs' });
  }
});

// 채용공고 상세 조회
// 채용공고 목록 조회 (페이지네이션 추가)
jobApp.get('/jobs', async (req, res) => {
    const { search, filter, sort, page = 1, size = 20 } = req.query;
  
    let query = 'SELECT * FROM jobs WHERE 1=1';
    const params = [];
  
    // 검색 조건 추가
    if (search) {
      query += ' AND title LIKE ?';
      params.push(`%${search}%`);
    }
  
    // 필터 조건 추가
    if (filter) {
      query += ' AND employment_type = ?';
      params.push(filter);
    }
  
    // 정렬 조건 추가
    if (sort) {
      query += ` ORDER BY ${sort}`;
    }
  
    // 페이지네이션 처리
    const offset = (page - 1) * size;
    query += ' LIMIT ? OFFSET ?';
    params.push(parseInt(size, 10), parseInt(offset, 10));
  
    try {
      const [rows] = await db.execute(query, params);
      res.json({
        page: parseInt(page, 10),
        size: parseInt(size, 10),
        data: rows,
      });
    } catch (err) {
      console.error(err);
      res.status(500).json({ error: 'Failed to fetch jobs' });
    }
  });
  
// 채용공고 추가
jobApp.post('/jobs', async (req, res) => {
  const { title, description, requirements, salary } = req.body;

  try {
    await db.execute('INSERT INTO jobs (job_id, title, description, requirements, salary, createdAt, updatedAt) VALUES (UUID(), ?, ?, ?, ?, NOW(), NOW())', [title, description, requirements, salary]);
    res.status(201).json({ message: 'Job created successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to create job' });
  }
});

// 채용공고 수정
jobApp.put('/jobs/:id', async (req, res) => {
  const { id } = req.params;
  const { title, description } = req.body;

  try {
    await db.execute('UPDATE jobs SET title = ?, description = ?, updatedAt = NOW() WHERE job_id = ?', [title, description, id]);
    res.json({ message: 'Job updated successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to update job' });
  }
});

// 채용공고 삭제
jobApp.delete('/jobs/:id', async (req, res) => {
  const { id } = req.params;

  try {
    await db.execute('DELETE FROM jobs WHERE job_id = ?', [id]);
    res.json({ message: 'Job deleted successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to delete job' });
  }
});

export default jobApp;
