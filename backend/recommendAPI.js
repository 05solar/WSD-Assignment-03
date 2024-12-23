import express from 'express';
import mysql from 'mysql2/promise';

const recommendApp = express.Router();
recommendApp.use(express.json());

const db = await mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '0518',
  database: 'web03',
});

/**
 * @swagger
 * tags:
 *   - name: 추천 관리
 *     description: 사용자 스킬과 직무 스킬 매칭 및 추천
 */

/**
 * @swagger
 * /jobs/match:
 *   get:
 *     tags:
 *       - 추천 관리
 *     summary: 적합한 공고 추천
 *     parameters:
 *       - name: user_id
 *         in: query
 *         required: true
 *         schema:
 *           type: string
 *         description: 사용자 ID
 *       - name: page
 *         in: query
 *         required: false
 *         schema:
 *           type: integer
 *           default: 1
 *         description: 페이지 번호
 *       - name: sort
 *         in: query
 *         required: false
 *         schema:
 *           type: string
 *           enum: [match_rate, salary, deadline]
 *           default: match_rate
 *         description: 정렬 기준
 *     responses:
 *       200:
 *         description: 적합한 공고 리스트
 *       400:
 *         description: 요청 데이터 오류
 *       500:
 *         description: 서버 오류
 */

/**
 * @swagger
 * /jobs/{job_id}/match/{user_id}:
 *   get:
 *     tags:
 *       - 추천 관리
 *     summary: 직무-스킬 적합도 분석
 *     parameters:
 *       - name: job_id
 *         in: path
 *         required: true
 *         schema:
 *           type: string
 *         description: 직무 공고 ID
 *       - name: user_id
 *         in: path
 *         required: true
 *         schema:
 *           type: string
 *         description: 사용자 ID
 *     responses:
 *       200:
 *         description: 매칭률 데이터 반환
 *       400:
 *         description: 요청 데이터 오류
 *       500:
 *         description: 서버 오류
 */

// 적합한 공고 추천
recommendApp.get('/jobs/match', async (req, res) => {
  const { user_id, page = 1, sort = 'match_rate' } = req.query;
  const pageSize = 10;
  const offset = (page - 1) * pageSize;

  if (!user_id) {
    return res.status(400).json({ error: 'user_id is required' });
  }

  try {
    const [userSkills] = await db.execute(
      'SELECT skill_name FROM userskills WHERE user_id = ?',
      [user_id]
    );

    if (userSkills.length === 0) {
      return res.status(404).json({ error: 'No skills found for the user' });
    }

    const userSkillSet = userSkills.map(skill => skill.skill_name);
    const sortOrder = sort === 'salary' ? 'salary_max DESC' : 'match_rate DESC';

    const [jobs] = await db.execute(
      `SELECT jobs.job_id, jobs.title, jobs.description, jobs.salary_min, jobs.salary_max, 
              (SELECT COUNT(*) FROM jobskills WHERE jobskills.job_id = jobs.job_id AND jobskills.skill_name IN (?)) / 
              (SELECT COUNT(*) FROM jobskills WHERE jobskills.job_id = jobs.job_id) AS match_rate 
       FROM jobs
       WHERE jobs.status = 'active'
       ORDER BY ${sortOrder}
       LIMIT ?, ?`,
      [userSkillSet, offset, pageSize]
    );

    res.json(jobs);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to fetch job recommendations' });
  }
});

// 직무-스킬 적합도 분석
recommendApp.get('/jobs/:job_id/match/:user_id', async (req, res) => {
  const { job_id, user_id } = req.params;

  try {
    const [userSkills] = await db.execute(
      'SELECT skill_name FROM userskills WHERE user_id = ?',
      [user_id]
    );
    const [jobSkills] = await db.execute(
      'SELECT skill_name FROM jobskills WHERE job_id = ?',
      [job_id]
    );

    if (userSkills.length === 0 || jobSkills.length === 0) {
      return res.status(404).json({ error: 'No matching data found' });
    }

    const userSkillSet = new Set(userSkills.map(skill => skill.skill_name));
    const jobSkillSet = new Set(jobSkills.map(skill => skill.skill_name));

    const matchedSkills = Array.from(jobSkillSet).filter(skill =>
      userSkillSet.has(skill)
    );

    const matchRate =
      (matchedSkills.length / jobSkillSet.size) * 100;

    res.json({ matchRate, matchedSkills });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to analyze skill matching' });
  }
});

export default recommendApp;
