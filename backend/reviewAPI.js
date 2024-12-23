import express from 'express';
import mysql from 'mysql2/promise';

const reviewApp = express.Router();
reviewApp.use(express.json());

const db = await mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '0518',
  database: 'web03'
});

/**
 * @swagger
 * tags:
 *   - name: 리뷰 관리
 *     description: 회사 리뷰 생성, 조회, 삭제
 */

/**
 * @swagger
 * /reviews:
 *   post:
 *     tags:
 *       - 리뷰 관리
 *     summary: 회사 리뷰 작성
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               company_id:
 *                 type: string
 *               user_id:
 *                 type: string
 *               rating:
 *                 type: integer
 *                 minimum: 1
 *                 maximum: 5
 *               review:
 *                 type: string
 *     responses:
 *       201:
 *         description: 리뷰 작성 성공
 *       400:
 *         description: 요청 데이터 오류
 *       500:
 *         description: 서버 오류
 */
reviewApp.post('/reviews', async (req, res) => {
  const { company_id, user_id, rating, review } = req.body;

  if (!company_id || !user_id || !rating) {
    return res.status(400).json({ error: 'Missing required fields' });
  }

  try {
    await db.execute(
      'INSERT INTO reviews (review_id, company_id, user_id, rating, review, created_at, updated_at) VALUES (UUID(), ?, ?, ?, ?, NOW(), NOW())',
      [company_id, user_id, rating, review || null]
    );
    res.status(201).json({ message: 'Review submitted successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to submit review' });
  }
});

/**
 * @swagger
 * /reviews/{company_id}:
 *   get:
 *     tags:
 *       - 리뷰 관리
 *     summary: 회사 리뷰 조회
 *     parameters:
 *       - name: company_id
 *         in: path
 *         required: true
 *         schema:
 *           type: string
 *         description: 회사 ID
 *       - name: page
 *         in: query
 *         schema:
 *           type: integer
 *           default: 1
 *         description: 페이지 번호
 *       - name: sort
 *         in: query
 *         schema:
 *           type: string
 *           default: 'latest'
 *         description: 정렬 기준 (latest, rating)
 *     responses:
 *       200:
 *         description: 리뷰 목록 반환
 *       500:
 *         description: 서버 오류
 */
reviewApp.get('/reviews/:company_id', async (req, res) => {
  const { company_id } = req.params;
  const { page = 1, sort = 'latest' } = req.query;
  const limit = 10;
  const offset = (page - 1) * limit;

  let orderBy = 'created_at DESC';
  if (sort === 'rating') {
    orderBy = 'rating DESC';
  }

  try {
    const [rows] = await db.execute(
      `SELECT * FROM reviews WHERE company_id = ? ORDER BY ${orderBy} LIMIT ? OFFSET ?`,
      [company_id, limit, offset]
    );
    res.json(rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to fetch reviews' });
  }
});

/**
 * @swagger
 * /reviews/{review_id}:
 *   delete:
 *     tags:
 *       - 리뷰 관리
 *     summary: 리뷰 삭제
 *     parameters:
 *       - name: review_id
 *         in: path
 *         required: true
 *         schema:
 *           type: string
 *         description: 리뷰 ID
 *     responses:
 *       200:
 *         description: 리뷰 삭제 성공
 *       404:
 *         description: 리뷰가 존재하지 않음
 *       500:
 *         description: 서버 오류
 */
reviewApp.delete('/reviews/:review_id', async (req, res) => {
  const { review_id } = req.params;

  try {
    const [result] = await db.execute('DELETE FROM reviews WHERE review_id = ?', [review_id]);

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: 'Review not found' });
    }

    res.json({ message: 'Review deleted successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to delete review' });
  }
});

export default reviewApp;
