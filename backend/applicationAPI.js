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

// 지원하기
applicationApp.post('/applications', async (req, res) => {
  const { job_id, resume_version, cover_letter } = req.body;

  if (!job_id || !resume_version) {
    return res.status(400).json({ error: 'Required fields are missing' });
  }

  try {
    await db.execute(
      'INSERT INTO applications (application_id, job_id, resume_version, cover_letter, createdAt, updatedAt) VALUES (UUID(), ?, ?, ?, NOW(), NOW())',
      [job_id, resume_version, cover_letter]
    );
    res.status(201).json({ message: 'Application submitted successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to submit application' });
  }
});

// 지원 내역 조회
applicationApp.get('/applications', async (req, res) => {
  try {
    const [rows] = await db.execute('SELECT * FROM applications');
    res.json(rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to fetch applications' });
  }
});

// 지원 취소
applicationApp.delete('/applications/:id', async (req, res) => {
  const { id } = req.params;

  try {
    const [result] = await db.execute('DELETE FROM applications WHERE application_id = ?', [id]);

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: 'Application not found' });
    }

    res.json({ message: 'Application deleted successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to delete application' });
  }
});

export default applicationApp;
