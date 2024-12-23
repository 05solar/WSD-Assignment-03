import express from 'express';
import mysql from 'mysql2/promise';

const bookmarkApp = express.Router();
bookmarkApp.use(express.json());

const db = await mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '0518',
  database: 'web03'
});

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
 *     summary: 북마크 조회
 *     responses:
 *       200:
 *         description: 북마크 목록 반환
 *       500:
 *         description: 서버 오류
 */

// 북마크 추가/제거
bookmarkApp.post('/bookmarks', async (req, res) => {
  const { job_id, note } = req.body;

  if (!job_id) {
    return res.status(400).json({ error: 'Job ID is required' });
  }

  try {
    const [rows] = await db.execute('SELECT * FROM bookmarks WHERE job_id = ?', [job_id]);

    if (rows.length > 0) {
      // 이미 북마크가 존재하면 제거
      await db.execute('DELETE FROM bookmarks WHERE job_id = ?', [job_id]);
      return res.json({ message: 'Bookmark removed' });
    } else {
      // 북마크가 없으면 추가
      await db.execute(
        'INSERT INTO bookmarks (bookmark_id, job_id, note, createdAt, updatedAt) VALUES (UUID(), ?, ?, NOW(), NOW())',
        [job_id, note || null]
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
  try {
    const [rows] = await db.execute('SELECT * FROM bookmarks');
    res.json(rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to fetch bookmarks' });
  }
});

export default bookmarkApp;
