import express from 'express';
import mysql from 'mysql2/promise';

const applicationApp = express.Router();
applicationApp.use(express.json());

const db = await mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '0518',
  database: 'web03'
});

/**
 * @swagger
 * tags:
 *   - name: 지원 관리
 *     description: 지원 생성, 조회, 취소
 *   - name: 채용공고 관리
 *     description: 채용공고 생성, 조회, 검색, 필터링, 정렬, 수정, 삭제
 */

/**
 * @swagger
 * /applications:
 *   post:
 *     tags:
 *       - 지원 관리
 *     summary: 지원하기
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               job_id:
 *                 type: string
 *               resume_version:
 *                 type: string
 *               cover_letter:
 *                 type: string
 *     responses:
 *       201:
 *         description: 지원 성공
 *       400:
 *         description: 요청 데이터 오류
 *       500:
 *         description: 서버 오류
 */

/**
 * @swagger
 * /applications:
 *   get:
 *     tags:
 *       - 지원 관리
 *     summary: 지원 내역 조회
 *     responses:
 *       200:
 *         description: 지원 내역 반환
 *       500:
 *         description: 서버 오류
 */

/**
 * @swagger
 * /applications/{id}:
 *   delete:
 *     tags:
 *       - 지원 관리
 *     summary: 지원 취소
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         schema:
 *           type: string
 *         description: 지원 ID
 *     responses:
 *       200:
 *         description: 지원 취소 성공
 *       404:
 *         description: 지원 내역 없음
 *       500:
 *         description: 서버 오류
 */

/**
 * @swagger
 * /jobs:
 *   get:
 *     tags:
 *       - 채용공고 관리
 *     summary: 채용공고 목록 조회, 검색, 필터링, 정렬
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

// 채용공고 API
applicationApp.get('/jobs', async (req, res) => {
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

applicationApp.get('/jobs/:id', async (req, res) => {
  const { id } = req.params;

  try {
    const [rows] = await db.execute('SELECT * FROM jobs WHERE job_id = ?', [id]);
    if (rows.length === 0) {
      return res.status(404).json({ error: 'Job not found' });
    }
    res.json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to fetch job' });
  }
});

applicationApp.post('/jobs', async (req, res) => {
  const { title, description, requirements, salary } = req.body;

  try {
    await db.execute(
      'INSERT INTO jobs (job_id, title, description, requirements, salary, createdAt, updatedAt) VALUES (UUID(), ?, ?, ?, ?, NOW(), NOW())',
      [title, description, requirements, salary]
    );
    res.status(201).json({ message: 'Job created successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to create job' });
  }
});

applicationApp.put('/jobs/:id', async (req, res) => {
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

applicationApp.delete('/jobs/:id', async (req, res) => {
  const { id } = req.params;

  try {
    await db.execute('DELETE FROM jobs WHERE job_id = ?', [id]);
    res.json({ message: 'Job deleted successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to delete job' });
  }
});

export default applicationApp;
